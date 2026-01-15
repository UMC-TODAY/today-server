package com.example.todayserver.domain.schedule.service;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.member.excpetion.code.MemberErrorCode;
import com.example.todayserver.domain.member.repository.MemberRepository;
import com.example.todayserver.domain.schedule.converter.ScheduleCreateConverter;
import com.example.todayserver.domain.schedule.dto.ScheduleCreateReq;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.entity.SubSchedule;
import com.example.todayserver.domain.schedule.repository.ScheduleRepository;
import com.example.todayserver.domain.schedule.repository.SubScheduleRepository;
import com.example.todayserver.domain.schedule.validator.ScheduleCreateValidator;
import com.example.todayserver.global.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final SubScheduleRepository subScheduleRepository;
    private final ScheduleCreateConverter scheduleCreateConverter;
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
}
