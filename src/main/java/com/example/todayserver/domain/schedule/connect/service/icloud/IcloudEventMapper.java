package com.example.todayserver.domain.schedule.connect.service.icloud;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.converter.ScheduleConverter;
import com.example.todayserver.domain.schedule.connect.converter.ScheduleExternalConverter;
import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalEventMapper;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IcloudEventMapper implements ExternalEventMapper {

    @Override
    public ExternalProvider provider() {
        return ExternalProvider.ICLOUD;
    }

    @Override
    public ScheduleExternalVersionType versionType() {
        return ScheduleExternalVersionType.HASH;
    }

    @Override
    public Schedule toSchedule(ExternalEventDto event, Member member) {
        ScheduleSource source = ScheduleSource.ICLOUD;
        return ScheduleConverter.fromExternalEvent(event, member, source);
    }

    @Override
    public ScheduleExternal toScheduleExternal(ExternalEventDto event, ExternalSource source, Schedule schedule) {
        return ScheduleExternalConverter.newMapping(event, source, schedule, versionType());
    }
}
