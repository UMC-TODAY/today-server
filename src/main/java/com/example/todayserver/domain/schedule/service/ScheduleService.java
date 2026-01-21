package com.example.todayserver.domain.schedule.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.converter.EventMonthlyConverter;
import com.example.todayserver.domain.schedule.converter.ScheduleCreateConverter;
import com.example.todayserver.domain.schedule.converter.ScheduleDetailConverter;
import com.example.todayserver.domain.schedule.converter.ScheduleUpdateConverter;
import com.example.todayserver.domain.schedule.dto.*;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import com.example.todayserver.domain.schedule.repository.SubScheduleRepository;
import com.example.todayserver.domain.schedule.validator.ScheduleCreateValidator;
import com.example.todayserver.global.common.exception.CustomException;

import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final SubScheduleRepository subScheduleRepository;
    private final ExternalAccountRepository externalAccountRepository;
    private final ScheduleCreateConverter scheduleCreateConverter;
    private final EventMonthlyConverter eventMonthlyConverter;
    private final ScheduleCreateValidator scheduleCreateValidator;
    private final ScheduleDetailConverter scheduleDetailConverter;
    private final ScheduleUpdateConverter scheduleUpdateConverter;


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

        List<ScheduleSource> allowedSources = getAllowedSources(memberId);

        List<Schedule> schedules;

        if ("ALL".equals(filterLabel)) {
            schedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetweenAndSourceIn(
                    memberId,
                    ScheduleType.EVENT,
                    startedAtFrom,
                    startedAtTo,
                    allowedSources
            );
        } else {
            ScheduleSource sourceFilter = ScheduleSource.valueOf(filterLabel);

            // 연동 해제된 source를 요청하면 빈 결과 반환
            if (!allowedSources.contains(sourceFilter)) {
                return eventMonthlyConverter.toEventMonthlyListRes(filterLabel, List.of());
            }

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
            boolean isCurrentMonth = year.equals(today.getYear()) && month.equals(today.getMonthValue());

            if (isCurrentMonth) {
                schedules = schedules.stream()
                        .filter(schedule -> {
                            if (schedule.getStartedAt() == null) return true;
                            LocalDate date = schedule.getStartedAt().toLocalDate();
                            return !date.isBefore(today);
                        })
                        .toList();
            }
        }

        return eventMonthlyConverter.toEventMonthlyListRes(filterLabel, schedules);
    }

    // 월별 일정 완료 현황 조회
    @Transactional(readOnly = true)
    public EventMonthlyCompletionRes getMonthlyEventCompletion(Long memberId, int year, int month) {

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        LocalDateTime startedAtFrom = startDate.atStartOfDay();
        LocalDateTime startedAtTo = endDate.atTime(23, 59, 59);

        List<ScheduleSource> allowedSources = getAllowedSources(memberId);

        long total = scheduleRepository.countByMemberIdAndScheduleTypeAndStartedAtBetweenAndSourceIn(
                memberId,
                ScheduleType.EVENT,
                startedAtFrom,
                startedAtTo,
                allowedSources
        );
        long completed = scheduleRepository.countByMemberIdAndScheduleTypeAndStartedAtBetweenAndIsDoneTrueAndSourceIn(
                memberId,
                ScheduleType.EVENT,
                startedAtFrom,
                startedAtTo,
                allowedSources
        );

        return EventMonthlyCompletionRes.of(year, month, total, completed);
    }

    // 일정 상세 조회
    @Transactional(readOnly = true)
    public ScheduleDetailRes getScheduleDetail(Long memberId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findByIdAndMember_Id(scheduleId, memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        List<SubSchedule> subSchedules = subScheduleRepository.findAllBySchedule_Id(schedule.getId());

        return scheduleDetailConverter.toDetailRes(schedule, subSchedules);
    }

    // 일정/할일 수정
    @Transactional
    public ScheduleDetailRes updateSchedule(Long memberId, Long scheduleId, ScheduleUpdateReq req) {
        Schedule schedule = scheduleRepository.findByIdAndMember_Id(scheduleId, memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        LocalDateTime newStartedAt = scheduleUpdateConverter.resolveStartedAt(schedule, req);
        LocalDateTime newEndedAt = scheduleUpdateConverter.resolveEndedAt(schedule, req);

        // Schedule 부분 업데이트 (요청 필드만 반영)
        schedule.updatePatch(
                req.scheduleType(),
                req.mode(),
                req.title(),
                req.memo(),
                req.bgColor(),
                req.emoji(),
                req.repeatType(),
                req.duration(),
                req.isAllDay(),
                newStartedAt,
                newEndedAt
        );

        // SubSchedule 부분 수정/추가
        if (req.subSchedules() != null && !req.subSchedules().isEmpty()) {
            for (SubScheduleUpdateReq s : req.subSchedules()) {

                // 수정 : subScheduleId가 있으면 해당 row 부분 수정
                if (s.subScheduleId() != null) {
                    SubSchedule target = subScheduleRepository
                            .findByIdAndSchedule_Id(s.subScheduleId(), schedule.getId())
                            .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

                    target.updatePatch(s.subTitle(), s.subColor(), s.subEmoji());
                    continue;
                }

                //  추가 : subScheduleId 없으면 신규 추가
                if (s.subTitle() == null || s.subTitle().isBlank()
                        || s.subColor() == null || s.subColor().isBlank()) {
                    throw new CustomException(MemberErrorCode.NOT_FOUND);
                }

                SubSchedule created = SubSchedule.builder()
                        .schedule(schedule)
                        .title(s.subTitle())
                        .color(s.subColor())
                        .emoji(s.subEmoji())
                        .build();

                subScheduleRepository.save(created);
            }
        }

        List<SubSchedule> latestSubs = subScheduleRepository.findAllBySchedule_Id(schedule.getId());
        return scheduleDetailConverter.toDetailRes(schedule, latestSubs);
    }
    // 로그인한 사용자(memberId)의 일정(scheduleId)을 찾아 요청값(is_done)으로 완료 상태를 변경
    // 일정이 없거나 본인 소유가 아니면 예외를 발생시키고, 변경된 id/is_done 값을 응답 DTO로 반환
    @Transactional
    public ScheduleStatusUpdateResponse updateScheduleStatus(Long memberId, Long scheduleId, ScheduleStatusUpdateRequest req) {
        Schedule schedule = scheduleRepository.findByIdAndMember_Id(scheduleId, memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.NOT_FOUND));

        schedule.updateDone(req.is_done());

        return new ScheduleStatusUpdateResponse(schedule.getId(), schedule.isDone());
    }

    // 내 일정/할 일을 조건(완료여부/타입/날짜/키워드)으로 조회해서 응답 DTO 리스트로 변환
    @Transactional(readOnly = true)
    public List<ScheduleSearchItemResponse> getSchedules(
            Long memberId,
            Boolean isDone,
            String category,
            LocalDate scheduleDate,
            String keyword
    ) {
        ScheduleType scheduleType = null;

        // category가 TASK/EVENT로 들어오면 ScheduleType 필터로 사용
        if (category != null && !category.isBlank()) {
            try {
                scheduleType = ScheduleType.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                scheduleType = null;
            }
        }

        LocalDateTime fromDt = null;
        LocalDateTime toDt = null;

        // scheduleDate가 있으면 해당 날짜(00:00:00~23:59:59) 기준으로 startedAt 범위를 생성
        if (scheduleDate != null) {
            fromDt = scheduleDate.atStartOfDay();
            toDt = scheduleDate.atTime(23, 59, 59);
        }

        List<Schedule> schedules = scheduleRepository.searchSchedules(
                memberId,
                isDone,
                scheduleType,
                (keyword == null || keyword.isBlank()) ? null : keyword,
                fromDt,
                toDt
        );

        return schedules.stream()
                .map(s -> new ScheduleSearchItemResponse(
                        s.getId(),
                        s.getTitle(),
                        s.isDone(),
                        s.getScheduleType().name(),
                        s.getStartedAt() == null ? null : s.getStartedAt().toLocalDate(),
                        s.getEmoji()
                ))
                .toList();
    }

    // 요청한 id 목록에 대해 본인 소유인 schedule만 일괄 삭제하고 삭제된 개수를 반환
    @Transactional
    public ScheduleBulkDeleteResponse deleteSchedulesBulk(Long memberId, ScheduleBulkDeleteRequest req) {
        if (req.getIds() == null || req.getIds().isEmpty()) {
            return new ScheduleBulkDeleteResponse(0);
        }

        int deleted = scheduleRepository.deleteAllByMemberIdAndIdIn(memberId, req.getIds());

        return new ScheduleBulkDeleteResponse(deleted);
    }

    // 허용 가능 소스 조회
    private List<ScheduleSource> getAllowedSources(Long memberId) {
        Set<ScheduleSource> allowedSources = EnumSet.of(ScheduleSource.LOCAL, ScheduleSource.CSV);

        List<ExternalAccount> connectedAccounts =
                externalAccountRepository.findAllByMemberIdAndStatus(memberId, ExternalAccountStatus.CONNECTED);

        for (ExternalAccount account : connectedAccounts) {
            allowedSources.add(ScheduleSource.valueOf(account.getProvider().name()));
        }

        return List.copyOf(allowedSources);
    }
}
