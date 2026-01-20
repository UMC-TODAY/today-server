package com.example.todayserver.domain.schedule.connect.service.notion;

import com.example.todayserver.domain.member.entity.Member;
import com.example.todayserver.domain.schedule.connect.converter.ScheduleConverter;
import com.example.todayserver.domain.schedule.connect.converter.ScheduleExternalConverter;
import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.entity.ScheduleExternal;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.enums.ScheduleExternalVersionType;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalEventMapper;
import com.example.todayserver.domain.schedule.entity.Schedule;
import com.example.todayserver.domain.schedule.enums.ScheduleSource;
import org.springframework.stereotype.Component;

@Component
public class NotionEventMapper implements ExternalEventMapper {

    @Override
    public ExternalProvider provider() {
        return ExternalProvider.NOTION;
    }

    @Override
    public ScheduleExternalVersionType versionType() {
        return ScheduleExternalVersionType.UPDATED_AT;
    }

    @Override
    public Schedule toSchedule(ExternalEventDto event, Member member) {
        return ScheduleConverter.fromExternalEvent(
                event,
                member,
                ScheduleSource.NOTION
        );
    }

    @Override
    public ScheduleExternal toScheduleExternal(ExternalEventDto event, ExternalSource source, Schedule schedule) {
        return ScheduleExternalConverter.newMapping(
                event,
                source,
                schedule,
                versionType()
        );
    }
}
