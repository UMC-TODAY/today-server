package com.example.todayserver.domain.analysis.service;

import com.example.todayserver.domain.analysis.dto.request.DifficultyRequest;
import com.example.todayserver.domain.analysis.dto.response.DifficultyResponse;
import com.example.todayserver.domain.analysis.dto.response.TogetherDaysResponse;
import com.example.todayserver.domain.analysis.dto.response.WeeklyCompletionResponse;
import com.example.todayserver.domain.analysis.entity.DailyDifficulty;
import com.example.todayserver.domain.analysis.enums.DifficultyLevel;
import com.example.todayserver.domain.analysis.repository.DailyDifficultyRepository;
import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleType;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalysisService {

    private final ScheduleRepository scheduleRepository;
    private final DailyDifficultyRepository dailyDifficultyRepository;

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
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());

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
}
