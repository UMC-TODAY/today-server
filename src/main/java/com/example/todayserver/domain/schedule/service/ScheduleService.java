package com.example.todayserver.domain.schedule.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.converter.EventMonthlyConverter;
import com.example.todayserver.domain.schedule.converter.ScheduleCreateConverter;
import com.example.todayserver.domain.schedule.dto.EventMonthlyCompletionRes;
import com.example.todayserver.domain.schedule.dto.EventMonthlyListRes;
import com.example.todayserver.domain.schedule.dto.EventMonthlySearchReq;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import com.example.todayserver.domain.schedule.repository.SubScheduleRepository;
import com.example.todayserver.domain.schedule.validator.ScheduleCreateValidator;
import com.example.todayserver.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final SubScheduleRepository subScheduleRepository;
    private final ScheduleCreateConverter scheduleCreateConverter;
    private final EventMonthlyConverter eventMonthlyConverter;
    private final ScheduleCreateValidator scheduleCreateValidator;

    @Transactional
    public Long createSchedule(Long memberId, ScheduleCreateReq req) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        scheduleCreateValidator.validateForCreate(req);

        Schedule schedule = scheduleCreateConverter.toSchedule(req, member);

        scheduleRepository.save(schedule);

        // SubSchedule 저장 (있을 경우)
        List<SubSchedule> subSchedules = scheduleCreateConverter.toSubSchedules(req, schedule);
        if (!subSchedules.isEmpty()) {
            subScheduleRepository.saveAll(subSchedules);
        }

        return schedule.getId();
    }

    // 월별 일정 리스트 조회
    public EventMonthlyListRes getMonthlyEvents(Long memberId, EventMonthlySearchReq req) {
        Integer year = req.year();
        Integer month = req.month();

        // filter가 null 또는 공백이면 ALL로 처리
        String filterLabel = (req.filter() == null || req.filter().isBlank()) ? "ALL" : req.filter().toUpperCase();

        // 해당 연/월의 1일 ~ 말일 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        // DATETIME 범위 (startedAt 기준)
        LocalDateTime startedAtFrom = startDate.atStartOfDay();
        LocalDateTime startedAtTo = endDate.atTime(23, 59, 59);

        // 지난 일정 숨기기 여부 (null 이면 false)
        boolean hidePast = Boolean.TRUE.equals(req.hidePast());

        List<Schedule> schedules;

        if ("ALL".equals(filterLabel)) {
            // 한 달 일정 전체 조회
            schedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                    memberId,
                    ScheduleType.EVENT,
                    startedAtFrom,
                    startedAtTo
            );
        } else {
            // 특정 출처만 조회 (GOOGLE, NOTION, ICLOUD, CSV, LOCAL)
            ScheduleSource sourceFilter = ScheduleSource.valueOf(filterLabel);

            schedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetweenAndSource(
                    memberId,
                    ScheduleType.EVENT,
                    startedAtFrom,
                    startedAtTo,
                    sourceFilter
            );
        }

        // 지난 일정 숨기기 적용 (현재 월일 때만)
        if (hidePast) {
            LocalDate today = LocalDate.now();

            boolean isCurrentMonth =
                    year.equals(today.getYear()) &&
                            month.equals(today.getMonthValue());

            if (isCurrentMonth) {
                schedules = schedules.stream()
                        .filter(schedule -> {
                            if (schedule.getStartedAt() == null) {
                                return true;
                            }
                            LocalDate date = schedule.getStartedAt().toLocalDate();
                            // 오늘 이전 날짜면 숨김
                            return !date.isBefore(today);
                        })
                        .toList();
            }
        }
        return eventMonthlyConverter.toEventMonthlyListRes(filterLabel, schedules);
    }
}
