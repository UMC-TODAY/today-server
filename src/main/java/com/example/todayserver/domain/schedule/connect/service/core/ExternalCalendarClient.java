package com.example.todayserver.domain.schedule.connect.service.core;

import com.example.todayserver.domain.schedule.connect.dto.ExternalEventDto;
import com.example.todayserver.domain.schedule.connect.dto.ExternalSourceDto;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.entity.ExternalSource;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;

import java.time.LocalDateTime;
import java.util.List;

public interface ExternalCalendarClient {
    // 외부 플랫폼
    ExternalProvider provider();

    // 외부 서비스에 존재하는 캘린더/DB/보드 목록 조회
    List<ExternalSourceDto> fetchSources(ExternalAccount account);

    // 일정 기간동안의 이벤트 조회
    List<ExternalEventDto> fetchEvents(
            ExternalAccount account,
            ExternalSource source,
            LocalDateTime from,
            LocalDateTime to
    );
}