package com.example.todayserver.domain.schedule.repository;

import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 월별 일정 데이터 전체 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndStartedAtBetween(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo
    );

    // 월별 일정 데이터 전체 조회 (허용된 source 목록 기반으로 조회)
    List<Schedule> findByMemberIdAndScheduleTypeAndStartedAtBetweenAndSourceIn(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            List<ScheduleSource> sources
    );

    // 월별 일정 데이터 필터 조회
    List<Schedule> findByMemberIdAndScheduleTypeAndStartedAtBetweenAndSource(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            ScheduleSource source
    );

    // 월별 총 일정 개수 조회 (허용된 source 목록 기반으로 조회
    long countByMemberIdAndScheduleTypeAndStartedAtBetweenAndSourceIn(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            List<ScheduleSource> sources
    );

    // 월별 완료된 일정 총 개수 조회 (허용된 source 목록 기반으로 조회)
    long countByMemberIdAndScheduleTypeAndStartedAtBetweenAndIsDoneTrueAndSourceIn(
            Long memberId,
            ScheduleType scheduleType,
            LocalDateTime startedAtFrom,
            LocalDateTime startedAtTo,
            List<ScheduleSource> sources
    );


    // scheduleId와 memberId가 모두 일치하는 Schedule을 조회 (존재 + 소유권 검증을 한 번에 처리).
    Optional<Schedule> findByIdAndMember_Id(Long id, Long memberId);/**/

    // 내 일정/할 일 목록을 조건(완료여부/타입/날짜/키워드)으로 필터링해서 조회
    @Query("""
    select s
    from Schedule s
    where s.member.id = :memberId
      and (:isDone is null or s.isDone = :isDone)
      and (:scheduleType is null or s.scheduleType = :scheduleType)
      and (:keyword is null or s.title like concat('%', :keyword, '%'))
      and (:fromDt is null or (s.startedAt is not null and s.startedAt between :fromDt and :toDt))
    order by s.createdAt desc 
    """)
    List<Schedule> searchSchedules(
            @Param("memberId") Long memberId,
            @Param("isDone") Boolean isDone,
            @Param("scheduleType") ScheduleType scheduleType,
            @Param("keyword") String keyword,
            @Param("fromDt") LocalDateTime fromDt,
            @Param("toDt") LocalDateTime toDt
    );

    // 요청한 id 목록 중 본인(memberId) 소유인 schedule만 삭제하고 삭제된 개수를 반환
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
    delete from Schedule s
    where s.member.id = :memberId
      and s.id in :ids
    """)
    int deleteAllByMemberIdAndIdIn(@Param("memberId") Long memberId, @Param("ids") List<Long> ids);


}
