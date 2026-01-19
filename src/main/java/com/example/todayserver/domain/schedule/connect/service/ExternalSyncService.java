package com.example.todayserver.domain.schedule.connect.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import com.example.todayserver.domain.schedule.connect.repository.ExternalSourceRepository;
import com.example.todayserver.domain.schedule.connect.repository.ScheduleExternalRepository;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalCalendarClient;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalEventMapper;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import com.example.todayserver.global.common.exception.CustomException;
import com.example.todayserver.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalSyncService {

    private final ExternalAccountRepository externalAccountRepository;
    private final ExternalSourceRepository externalSourceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleExternalRepository scheduleExternalRepository;
    private final MemberRepository memberRepository;

    private final Map<ExternalProvider, ExternalCalendarClient> clientMap;
    private final Map<ExternalProvider, ExternalEventMapper> mapperMap;

    @Transactional
    public void syncMonth(Long memberId, ExternalProvider provider, int year, int month) {
        log.info("[ExternalSync][START] memberId={}, provider={}, year={}, month={}",
                memberId, provider, year, month);

        try {
            // 연동 계정 확인
            ExternalAccount account = externalAccountRepository
                    .findByMemberIdAndProviderAndStatus(memberId, provider, ExternalAccountStatus.CONNECTED)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXTERNAL_ACCOUNT_NOT_FOUND));

            // provider에 맞는 client / mapper 선택
            ExternalCalendarClient client = clientMap.get(provider);
            if (client == null) throw new CustomException(ErrorCode.EXTERNAL_CLIENT_NOT_REGISTERED);

            ExternalEventMapper mapper = mapperMap.get(provider);
            if (mapper == null) throw new CustomException(ErrorCode.EXTERNAL_MAPPER_NOT_REGISTERED);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

            // 외부 소스 목록 조회 및 저장(없으면 생성)
            List<ExternalSourceDto> sourceDtos = client.fetchSources(account);
            for (ExternalSourceDto dto : sourceDtos) {
                externalSourceRepository.findByExternalAccountIdAndSourceKey(account.getId(), dto.sourceKey())
                        .orElseGet(() -> externalSourceRepository.save(
                                ExternalSource.builder()
                                        .externalAccount(account)
                                        .sourceKey(dto.sourceKey())
                                        .sourceName(dto.sourceName())
                                        .metaJson(dto.metaJson())
                                        .syncEnabled(true)
                                        .build()
                        ));
            }

            // 이번 달 기간 계산
            LocalDateTime from = YearMonth.of(year, month).atDay(1).atStartOfDay();
            LocalDateTime to = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);

            // 이벤트 조회 및 (중복 제외) 저장 로직
            List<ExternalSource> enabledSources = externalSourceRepository.findEnabledByAccount(account.getId());

            int fetchedSources = enabledSources.size();
            int fetchedEventsTotal = 0;
            int skippedDuplicated = 0;
            int newEventsDetected = 0;

            for (ExternalSource source : enabledSources) {
                List<ExternalEventDto> events = client.fetchEvents(account, source, from, to);
                fetchedEventsTotal += events.size();

                for (ExternalEventDto event : events) {
                    ScheduleExternalVersionType versionType = mapper.versionType();

                    boolean exists = scheduleExternalRepository
                            .findByExternalSourceIdAndExternalEventIdAndVersionType(
                                    source.getId(),
                                    event.externalEventId(),
                                    versionType
                            )
                            .isPresent();

                    if (exists) {
                        skippedDuplicated++;
                        continue;
                    }

                    newEventsDetected++;

                    // 신규 이벤트 ->  Schedule 저장
                     Schedule schedule = mapper.toSchedule(event, member);
                     scheduleRepository.save(schedule);

                     ScheduleExternal mapping = mapper.toScheduleExternal(event, source, schedule);
                     scheduleExternalRepository.save(mapping);
                }
            }

            // 마지막 동기화 시각 기록
            account.updateLastSyncedAt(LocalDateTime.now());

            log.info("[ExternalSync][SUCCESS] memberId={}, provider={}, year={}, month={}, sources={}, events={}, newEvents={}, skippedDuplicated={}",
                    memberId, provider, year, month, fetchedSources, fetchedEventsTotal, newEventsDetected, skippedDuplicated);

        } catch (CustomException e) {
            log.error("[ExternalSync][FAIL] memberId={}, provider={}, year={}, month={}, errorCode={}, message={}",
                    memberId, provider, year, month, e.getErrorCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            // 예상 못한 예외는 원인 추적을 위해 stacktrace 포함
            log.error("[ExternalSync][FAIL] memberId={}, provider={}, year={}, month={}, unexpectedError={}",
                    memberId, provider, year, month, e.toString(), e);
            throw e;
        }
    }
}
