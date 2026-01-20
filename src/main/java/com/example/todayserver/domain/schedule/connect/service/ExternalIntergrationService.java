package com.example.todayserver.domain.schedule.connect.service;

import com.example.todayserver.domain.schedule.connect.converter.ExternalIntegrationConverter;
import com.example.todayserver.domain.schedule.connect.dto.IntergrationStatueRes;
import com.example.todayserver.domain.schedule.connect.entity.ExternalAccount;
import com.example.todayserver.domain.schedule.connect.enums.ExternalAccountStatus;
import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.repository.ExternalAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExternalIntergrationService {

    private final ExternalAccountRepository externalAccountRepository;
    private final ExternalIntegrationConverter externalIntegrationConverter;

    @Transactional(readOnly = true)
    public IntergrationStatueRes getIntegrationStatus(Long memberId) {
        Set<ExternalProvider> connectedProviders = externalAccountRepository.findAllByMemberId(memberId).stream()
                .filter(a -> a.getStatus() == ExternalAccountStatus.CONNECTED)
                .map(ExternalAccount::getProvider)
                .collect(Collectors.toSet());

        return externalIntegrationConverter.toStatusRes(connectedProviders);
    }
}
