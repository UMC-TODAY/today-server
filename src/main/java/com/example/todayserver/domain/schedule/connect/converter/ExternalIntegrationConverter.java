package com.example.todayserver.domain.schedule.connect.converter;

import com.example.todayserver.domain.schedule.connect.dto.IntergrationStatueRes;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

@Component
public class ExternalIntegrationConverter {

    public IntergrationStatueRes toStatusRes(Set<ExternalProvider> connectedProviders) {
        List<IntergrationStatueRes.ProviderStatus> providers = EnumSet.allOf(ExternalProvider.class).stream()
                .map(p -> new IntergrationStatueRes.ProviderStatus(p, connectedProviders.contains(p)))
                .toList();

        return new IntergrationStatueRes(providers);
    }
}
