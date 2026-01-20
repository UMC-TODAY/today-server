package com.example.todayserver.domain.schedule.connect.service.core;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.domain.schedule.entity.Schedule;

// 외부에서 가져온 데이터를 Schedule 구조로 변환
public interface ExternalEventMapper {
    ExternalProvider provider();

    ScheduleExternalVersionType versionType();

    Schedule toSchedule(ExternalEventDto event, Member member);

    // 외부 이벤트를 내부 Schedule과 매핑하기 위한 ScheduleExternal 생성용 데이터
    ScheduleExternal toScheduleExternal(ExternalEventDto event, ExternalSource source, Schedule schedule);
}