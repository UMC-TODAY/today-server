package com.example.todayserver.domain.analysis.service;

import com.example.todayserver.domain.analysis.dto.request.DifficultyRequest;
import com.example.todayserver.domain.analysis.dto.request.FocusChecklistRequest;
import com.example.todayserver.domain.analysis.dto.response.BadgeStatsResponse;
import com.example.todayserver.domain.analysis.dto.response.DifficultyResponse;
import com.example.todayserver.domain.analysis.dto.response.FocusChecklistResponse;
import com.example.todayserver.domain.analysis.dto.response.GrassMapResponse;
import com.example.todayserver.domain.analysis.dto.response.TogetherDaysResponse;
import com.example.todayserver.domain.analysis.dto.response.WeeklyCompletionResponse;
import com.example.todayserver.domain.analysis.entity.DailyDifficulty;
import com.example.todayserver.domain.analysis.entity.FocusChecklist;
import com.example.todayserver.domain.analysis.enums.DifficultyLevel;
import com.example.todayserver.domain.analysis.repository.DailyDifficultyRepository;
import com.example.todayserver.domain.analysis.repository.FocusChecklistRepository;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final ScheduleRepository scheduleRepository;
    private final DailyDifficultyRepository dailyDifficultyRepository;
    private final FocusChecklistRepository focusChecklistRepository;
    private final MemberRepository memberRepository;

    // 고정된 체크리스트 항목들
    private static final List<String> DEFAULT_CHECKLIST_CONTENTS = List.of(
            "필요한 참고 자료 탭만 열기",
            "휴대폰 무음 및 뒤집기",
            "물 또는 음료 준비하기",
            "완료할 일정 정하기"
    );

    //요일별 계획 대비 완료율 조회
    public WeeklyCompletionResponse getWeeklyCompletionRate(Member member) {
        // 최근 3개월 데이터 조회
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(3);

        // TASK와 EVENT 모두 조회
        List<Schedule> taskSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(),
                ScheduleType.TASK,
                startDate,
                endDate
        );
        
        List<Schedule> eventSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(),
                ScheduleType.EVENT,
                startDate,
                endDate
        );
        
        // 두 리스트 합치기
        List<Schedule> schedules = new ArrayList<>();
        schedules.addAll(taskSchedules);
        schedules.addAll(eventSchedules);

        // 요일별로 그룹화
        Map<DayOfWeek, List<Schedule>> schedulesByDayOfWeek = schedules.stream()
                .collect(Collectors.groupingBy(schedule -> 
                    schedule.getStartedAt().getDayOfWeek()
                ));

        // 요일별 완료율 계산
        List<WeeklyCompletionResponse.DayCompletionRate> weeklyRates = new ArrayList<>();
        List<Double> completionRates = new ArrayList<>();

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            List<Schedule> daySchedules = schedulesByDayOfWeek.getOrDefault(dayOfWeek, Collections.emptyList());
            
            int totalCount = daySchedules.size();
            int completedCount = (int) daySchedules.stream()
                    .filter(Schedule::isDone)
                    .count();
            
            double completionRate = totalCount > 0 
                    ? Math.round((double) completedCount / totalCount * 1000.0) / 10.0 
                    : 0.0;
            
            completionRates.add(completionRate);

            weeklyRates.add(WeeklyCompletionResponse.DayCompletionRate.builder()
                    .dayOfWeek(dayOfWeek.name())
                    .dayName(getDayName(dayOfWeek))
                    .totalCount(totalCount)
                    .completedCount(completedCount)
                    .completionRate(completionRate)
                    .build());
        }

        // 통계 계산
        WeeklyCompletionResponse.Statistics statistics = calculateStatistics(completionRates);

        // 분석 메시지 생성
        List<WeeklyCompletionResponse.AnalysisMessage> analysisMessages =
                generateAnalysisMessages(weeklyRates, statistics);

        return WeeklyCompletionResponse.builder()
                .weeklyRates(weeklyRates)
                .analysisMessages(analysisMessages)
                .statistics(statistics)
                .build();
    }

    //요일 한글 이름 반환
    private String getDayName(DayOfWeek dayOfWeek) {
        return dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN);
    }

    // 통계 계산
    private WeeklyCompletionResponse.Statistics calculateStatistics(List<Double> completionRates) {
        List<Double> nonZeroRates = completionRates.stream()
                .filter(rate -> rate > 0)
                .toList();

        if (nonZeroRates.isEmpty()) {
            return WeeklyCompletionResponse.Statistics.builder()
                    .highestRate(0.0)
                    .lowestRate(0.0)
                    .averageRate(0.0)
                    .deviation(0.0)
                    .build();
        }

        double highest = Collections.max(completionRates);
        double lowest = Collections.min(nonZeroRates);
        double average = Math.round(completionRates.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0) * 10.0) / 10.0;
        double deviation = Math.round((highest - lowest) * 10.0) / 10.0;

        return WeeklyCompletionResponse.Statistics.builder()
                .highestRate(highest)
                .lowestRate(lowest)
                .averageRate(average)
                .deviation(deviation)
                .build();
    }

    // 분석 메시지 생성
    private List<WeeklyCompletionResponse.AnalysisMessage> generateAnalysisMessages(
            List<WeeklyCompletionResponse.DayCompletionRate> weeklyRates,
            WeeklyCompletionResponse.Statistics statistics) {

        List<WeeklyCompletionResponse.AnalysisMessage> messages = new ArrayList<>();

        // 편차 분석
        if (statistics.getDeviation() > 30.0) {
            messages.add(WeeklyCompletionResponse.AnalysisMessage.builder()
                    .type("DEVIATION")
                    .message("요일별로 계획 유지 비율의 차이가 나타납니다.")
                    .recommendation("요일별 일정 밀도를 조정해보는 것도 도움이 될 수 있습니다.")
                    .build());
        }

        // 높은 완료율 요일 찾기
        List<String> highCompletionDays = weeklyRates.stream()
                .filter(rate -> rate.getCompletionRate() >= 70.0 && rate.getTotalCount() > 0)
                .map(WeeklyCompletionResponse.DayCompletionRate::getDayName)
                .toList();

        if (!highCompletionDays.isEmpty()) {
            String daysString = String.join(", ", highCompletionDays);
            messages.add(WeeklyCompletionResponse.AnalysisMessage.builder()
                    .type("HIGH_COMPLETION")
                    .message(daysString + "에는 계획한 일정이 비교적 잘 유지되고 있습니다.")
                    .recommendation("완료율이 높은 요일의 일정 구성을 참고해보세요.")
                    .build());
        }

        return messages;
    }

    // TODAY와 함께 하고 있어요 (가입일로부터 경과 일수)
    public TogetherDaysResponse getTogetherDays(Member member) {
        LocalDate joinedDate = member.getCreatedAt().toLocalDate();
        LocalDate today = LocalDate.now();
        
        // 경과 일수 계산 (당일 포함하려면 +1)
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(joinedDate, today);
        int togetherDays = (int) daysBetween + 1;
        
        String message = String.format("TO:DAY와 %d일째 함께하고 있어요!", togetherDays);
        
        return TogetherDaysResponse.builder()
                .togetherDays(togetherDays)
                .joinedAt(joinedDate.toString())
                .message(message)
                .build();
    }

    // 일정소화난이도 평가 등록
    @Transactional
    public DifficultyResponse.Create createDailyDifficulty(Member member, DifficultyRequest.Create request) {
        // 이미 해당 날짜에 평가가 있는지 확인
        if (dailyDifficultyRepository.existsByMemberAndDate(member, request.getDate())) {
            throw new IllegalArgumentException("해당 날짜에 이미 난이도 평가가 존재합니다.");
        }

        // 난이도 레벨 검증 및 이름 가져오기
        DifficultyLevel difficultyLevel = DifficultyLevel.fromLevel(request.getDifficultyLevel());

        // 엔티티 생성 및 저장
        DailyDifficulty dailyDifficulty = DailyDifficulty.builder()
                .member(member)
                .date(request.getDate())
                .difficultyLevel(request.getDifficultyLevel())
                .build();

        DailyDifficulty saved = dailyDifficultyRepository.save(dailyDifficulty);

        return DifficultyResponse.Create.builder()
                .difficultyId(saved.getId())
                .date(saved.getDate().toString())
                .difficultyLevel(saved.getDifficultyLevel())
                .difficultyName(difficultyLevel.getName())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    // 일정소화난이도 평가 수정
    @Transactional
    public DifficultyResponse.Update updateDailyDifficulty(Member member, DifficultyRequest.Update request) {
        // 해당 날짜의 평가 찾기
        DailyDifficulty dailyDifficulty = dailyDifficultyRepository.findByMemberAndDate(member, request.getDate())
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 난이도 평가가 존재하지 않습니다."));

        // 난이도 레벨 검증 및 이름 가져오기
        DifficultyLevel difficultyLevel = DifficultyLevel.fromLevel(request.getDifficultyLevel());

        // 난이도 수정
        dailyDifficulty.updateDifficultyLevel(request.getDifficultyLevel());

        return DifficultyResponse.Update.builder()
                .date(dailyDifficulty.getDate().toString())
                .difficultyLevel(dailyDifficulty.getDifficultyLevel())
                .difficultyName(difficultyLevel.getName())
                .updatedAt(dailyDifficulty.getUpdatedAt())
                .build();
    }

    //잔디맵 (최근 91일 일정 처리 집계)
    public GrassMapResponse getGrassMap(Member member) {
        // 91일 기간 설정 (오늘부터 -90일)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(90);

        // Schedule 조회 (TASK + EVENT)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Schedule> taskSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(),
                ScheduleType.TASK,
                startDateTime,
                endDateTime
        );

        List<Schedule> eventSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(),
                ScheduleType.EVENT,
                startDateTime,
                endDateTime
        );

        List<Schedule> allSchedules = new ArrayList<>();
        allSchedules.addAll(taskSchedules);
        allSchedules.addAll(eventSchedules);

        // 날짜별로 완료된 일정 수 집계
        Map<LocalDate, Long> completedCountByDate = allSchedules.stream()
                .filter(Schedule::isDone)
                .collect(Collectors.groupingBy(
                        schedule -> schedule.getStartedAt().toLocalDate(),
                        Collectors.counting()
                ));

        // 91일 동안의 grass 데이터 생성
        List<GrassMapResponse.Grass> grassList = new ArrayList<>();
        int totalCompleted = 0;
        int maxCompleted = 0;
        int activeDays = 0;

        for (int i = 0; i <= 90; i++) {
            LocalDate date = startDate.plusDays(i);
            int completedCount = completedCountByDate.getOrDefault(date, 0L).intValue();
            int level = calculateLevel(completedCount);

            if (completedCount > 0) {
                activeDays++;
            }
            totalCompleted += completedCount;
            maxCompleted = Math.max(maxCompleted, completedCount);

            grassList.add(GrassMapResponse.Grass.builder()
                    .date(date.toString())
                    .dayOfWeek(date.getDayOfWeek().name())
                    .completedCount(completedCount)
                    .level(level)
                    .build());
        }

        // 평균 계산
        double averageCompleted = Math.round((double) totalCompleted / 91 * 100.0) / 100.0;

        // Period
        GrassMapResponse.Period period = GrassMapResponse.Period.builder()
                .startDate(startDate.toString())
                .endDate(endDate.toString())
                .days(91)
                .build();

        // Grid
        GrassMapResponse.Grid grid = GrassMapResponse.Grid.builder()
                .rows(7)
                .cols(13)
                .size(91)
                .build();

        // Summary (추가해도 괜찮을 것 같은 정보 입니다 )
        GrassMapResponse.Summary summary = GrassMapResponse.Summary.builder()
                .totalCompletedCount(totalCompleted)
                .maxCompletedCount(maxCompleted)
                .averageCompletedCount(averageCompleted)
                .activeDays(activeDays)
                .build();

        // LevelCriteria
        GrassMapResponse.LevelCriteria levelCriteria = GrassMapResponse.LevelCriteria.builder()
                .level0("0개")
                .level1("1개")
                .level2("2~3개")
                .level3("4~5개")
                .level4("6개 이상")
                .build();

        return GrassMapResponse.builder()
                .period(period)
                .grid(grid)
                .grass(grassList)
                .summary(summary)
                .levelCriteria(levelCriteria)
                .build();
    }

    // 완료 수에 따른 레벨 계산
    private int calculateLevel(int completedCount) {
        if (completedCount == 0) return 0;
        if (completedCount == 1) return 1;
        if (completedCount <= 3) return 2;
        if (completedCount <= 5) return 3;
        return 4; // 6개 이상
    }

    // 몰입준비 체크리스트 조회
    @Transactional
    public FocusChecklistResponse getFocusChecklist(Member member) {
        // 체크리스트 조회 또는 초기화
        List<FocusChecklist> checklists = focusChecklistRepository.findByMemberOrderByIdAsc(member);
        
        // 체크리스트가 없으면 초기 생성
        if (checklists.isEmpty()) {
            checklists = initializeChecklist(member);
        }

        // 오늘 날짜와 다음 리셋 시간 계산
        LocalDate today = LocalDate.now();
        LocalDateTime nextReset = today.plusDays(1).atTime(6, 0, 0);

        // Response 생성
        List<FocusChecklistResponse.ChecklistItem> items = checklists.stream()
                .map(checklist -> FocusChecklistResponse.ChecklistItem.builder()
                        .itemId(checklist.getId())
                        .content(checklist.getContent())
                        .isCompleted(checklist.getIsCompleted())
                        .build())
                .toList();

        return FocusChecklistResponse.builder()
                .date(today.toString())
                .nextResetAt(nextReset)
                .items(items)
                .build();
    }

    // 체크리스트 초기 생성
    @Transactional
    protected List<FocusChecklist> initializeChecklist(Member member) {
        List<FocusChecklist> checklists = new ArrayList<>();
        
        for (String content : DEFAULT_CHECKLIST_CONTENTS) {
            FocusChecklist checklist = FocusChecklist.builder()
                    .content(content)
                    .isCompleted(false)
                    .member(member)
                    .build();
            checklists.add(focusChecklistRepository.save(checklist));
        }
        
        return checklists;
    }

    // 체크리스트 항목 완료 상태 수정
    @Transactional
    public void updateFocusChecklistItem(Long itemId, FocusChecklistRequest request) {
        FocusChecklist checklist = focusChecklistRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("체크리스트 항목을 찾을 수 없습니다."));
        
        checklist.updateCompleted(request.getIsCompleted());
    }


    // 뱃지 및 완료 일정 통계 조회
    public BadgeStatsResponse getBadgeStats(Member member) {
        // 1. 완료한 일정 수 계산
        long myCompletedCount = scheduleRepository.countByMemberIdAndIsDoneTrue(member.getId());

        // 2. 뱃지 카운트 계산
        int badgeCount = calculateBadgeCount(member);

        // 3. 전체 유저 대비 랭킹 계산
        List<Object[]> allMemberStats = scheduleRepository.countCompletedSchedulesGroupByMember();
        
        int completedScheduleRanking = calculateRankingPercent(myCompletedCount, allMemberStats);
        int badgeRanking = calculateBadgeRankingPercent(member.getId(), badgeCount);

        // 4. Response 생성
        BadgeStatsResponse.BadgeInfo badgeInfo = BadgeStatsResponse.BadgeInfo.builder()
                .totalCount(badgeCount)
                .rankingPercent(badgeRanking)
                .rankingDirection("UP")
                .build();

        BadgeStatsResponse.CompletedScheduleInfo completedScheduleInfo = BadgeStatsResponse.CompletedScheduleInfo.builder()
                .totalCount((int) myCompletedCount)
                .rankingPercent(completedScheduleRanking)
                .rankingDirection("UP")
                .build();

        return BadgeStatsResponse.builder()
                .badge(badgeInfo)
                .completedSchedule(completedScheduleInfo)
                .build();
    }

    /* 뱃지 카운트 계산 (최적화 버전)
       - 전체 기간 데이터를 2번의 쿼리로 조회 후 메모리에서 계산
     */
    private int calculateBadgeCount(Member member) {
        LocalDate startDate = member.getCreatedAt().toLocalDate();
        LocalDate endDate = LocalDate.now().minusDays(1);
        
        if (endDate.isBefore(startDate)) {
            return 0;
        }

        // 전체 기간 일정을 한 번에 조회 (2번의 쿼리만 실행)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<Schedule> eventSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(), ScheduleType.EVENT, startDateTime, endDateTime);
        List<Schedule> taskSchedules = scheduleRepository.findByMemberIdAndScheduleTypeAndStartedAtBetween(
                member.getId(), ScheduleType.TASK, startDateTime, endDateTime);

        // 날짜별로 그룹화 (메모리에서 처리)
        Map<LocalDate, List<Schedule>> eventsByDate = eventSchedules.stream()
                .filter(s -> s.getStartedAt() != null)
                .collect(Collectors.groupingBy(s -> s.getStartedAt().toLocalDate()));

        Map<LocalDate, List<Schedule>> tasksByDate = taskSchedules.stream()
                .filter(s -> s.getStartedAt() != null)
                .collect(Collectors.groupingBy(s -> s.getStartedAt().toLocalDate()));

        int badgeCount = 0;
        Map<LocalDate, Boolean> eventCompleteDays = new HashMap<>();
        Map<LocalDate, Boolean> taskCompleteDays = new HashMap<>();

        // 각 날짜별 완료 여부 체크 (DB 쿼리 없이 메모리에서 계산)
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<Schedule> dayEvents = eventsByDate.getOrDefault(date, Collections.emptyList());
            List<Schedule> dayTasks = tasksByDate.getOrDefault(date, Collections.emptyList());

            // EVENT 완료 체크
            boolean eventComplete = !dayEvents.isEmpty() && dayEvents.stream().allMatch(Schedule::isDone);
            eventCompleteDays.put(date, eventComplete);
            if (eventComplete) {
                badgeCount++;
            }

            // TASK 완료 체크
            boolean taskComplete = !dayTasks.isEmpty() && dayTasks.stream().allMatch(Schedule::isDone);
            taskCompleteDays.put(date, taskComplete);
            if (taskComplete) {
                badgeCount++;
            }
        }

        // 주간 연속 완료 체크 (월~일 기준)
        badgeCount += calculateWeeklyStreakBadges(eventCompleteDays, taskCompleteDays, startDate, endDate);

        // 월간 전체 완료 체크
        badgeCount += calculateMonthlyStreakBadges(eventCompleteDays, taskCompleteDays, startDate, endDate);

        return badgeCount;
    }

    // 주간 연속 완료 뱃지 계산 (월~일 7일 연속)
    private int calculateWeeklyStreakBadges(
            Map<LocalDate, Boolean> eventCompleteDays,
            Map<LocalDate, Boolean> taskCompleteDays,
            LocalDate startDate,
            LocalDate endDate) {
        
        int weeklyBadges = 0;
        
        // 첫 번째 일요일 찾기
        LocalDate firstSunday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        for (LocalDate sunday = firstSunday; !sunday.isAfter(endDate); sunday = sunday.plusWeeks(1)) {
            LocalDate monday = sunday.minusDays(6);
            
            // 해당 주의 월~일이 모두 범위 내에 있는지 확인
            if (monday.isBefore(startDate)) {
                continue;
            }
            
            // 7일 연속 완료 체크 (EVENT 또는 TASK 중 하나라도 완료)
            boolean weekComplete = true;
            for (LocalDate date = monday; !date.isAfter(sunday); date = date.plusDays(1)) {
                boolean dayComplete = eventCompleteDays.getOrDefault(date, false) 
                        || taskCompleteDays.getOrDefault(date, false);
                if (!dayComplete) {
                    weekComplete = false;
                    break;
                }
            }
            
            if (weekComplete) {
                weeklyBadges++;
            }
        }
        
        return weeklyBadges;
    }

    //월간 전체 완료 뱃지 계산
    private int calculateMonthlyStreakBadges(
            Map<LocalDate, Boolean> eventCompleteDays,
            Map<LocalDate, Boolean> taskCompleteDays,
            LocalDate startDate,
            LocalDate endDate) {
        
        int monthlyBadges = 0;
        
        // 시작 월부터 종료 월까지 반복
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);
        
        for (YearMonth month = startMonth; !month.isAfter(endMonth); month = month.plusMonths(1)) {
            LocalDate monthStart = month.atDay(1);
            LocalDate monthEnd = month.atEndOfMonth();
            
            // 해당 월이 완전히 범위 내에 있는지 확인
            if (monthStart.isBefore(startDate) || monthEnd.isAfter(endDate)) {
                continue;
            }
            
            // 한 달 전체 완료 체크
            boolean monthComplete = true;
            for (LocalDate date = monthStart; !date.isAfter(monthEnd); date = date.plusDays(1)) {
                boolean dayComplete = eventCompleteDays.getOrDefault(date, false) 
                        || taskCompleteDays.getOrDefault(date, false);
                if (!dayComplete) {
                    monthComplete = false;
                    break;
                }
            }
            
            if (monthComplete) {
                monthlyBadges++;
            }
        }
        
        return monthlyBadges;
    }

    // 완료 일정 수 기준 상위 % 계산

    private int calculateRankingPercent(long myCount, List<Object[]> allMemberStats) {
        if (allMemberStats.isEmpty()) {
            return 100; // 데이터가 없으면 상위 100%
        }

        // 나보다 완료 수가 많은 유저 수 계산
        long higherCount = allMemberStats.stream()
                .filter(stat -> ((Long) stat[1]) > myCount)
                .count();

        int totalMembers = allMemberStats.size();
        
        // 상위 % 계산: (나보다 높은 순위 수 / 전체 수) * 100
        int rankingPercent = (int) Math.ceil((double) (higherCount + 1) / totalMembers * 100);
        
        return Math.min(rankingPercent, 100);
    }

    //뱃지 카운트 기준 상위 % 계산 (단순화 - 완료 일정 랭킹과 동일하게 사용)
     private int calculateBadgeRankingPercent(Long memberId, int myBadgeCount) {
        // 성능 이슈로 단순화: 완료 일정 수 기반 랭킹을 그대로 사용
        // 추후 캐싱 적용 시 정확한 뱃지 랭킹 계산 가능
        return 1; // 임시로 상위 1% 반환
    }
}
