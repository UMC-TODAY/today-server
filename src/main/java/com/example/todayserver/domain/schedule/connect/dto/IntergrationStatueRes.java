package com.example.todayserver.domain.schedule.connect.dto;

import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;

import java.util.List;

public record IntergrationStatueRes(
        List<ProviderStatus> providers
) {

    public record ProviderStatus(
            ExternalProvider provider,
            boolean connected
    ) {
    }
}