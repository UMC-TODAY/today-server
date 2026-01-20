package com.example.todayserver.domain.schedule.connect.service;

import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalSyncAsyncService {

    private final ExternalSyncService externalSyncService;

    @Async
    public void syncMonth(Long memberId, ExternalProvider provider, int year, int month) {
        externalSyncService.syncMonth(memberId, provider, year, month);
    }
}