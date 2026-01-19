package com.example.todayserver.domain.schedule.connect.config;

import com.example.todayserver.domain.schedule.connect.enums.ExternalProvider;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalCalendarClient;
import com.example.todayserver.domain.schedule.connect.service.core.ExternalEventMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ExternalClientRegistryConfig {

    @Bean
    public Map<ExternalProvider, ExternalCalendarClient> externalCalendarClientMap(
            List<ExternalCalendarClient> clients
    ) {
        Map<ExternalProvider, ExternalCalendarClient> map = new EnumMap<>(ExternalProvider.class);
        for (ExternalCalendarClient client : clients) {
            map.put(client.provider(), client);
        }
        return map;
    }

    @Bean
    public Map<ExternalProvider, ExternalEventMapper> externalEventMapperMap(
            List<ExternalEventMapper> mappers
    ) {
        Map<ExternalProvider, ExternalEventMapper> map = new EnumMap<>(ExternalProvider.class);
        for (ExternalEventMapper mapper : mappers) {
            map.put(mapper.provider(), mapper);
        }
        return map;
    }
}
