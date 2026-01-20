package com.example.todayserver.domain.schedule.connect.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NotionTokenRes(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("workspace_id") String workspaceId,
        @JsonProperty("workspace_name") String workspaceName,
        @JsonProperty("bot_id") String botId,
        @JsonProperty("owner") Object owner
) {}