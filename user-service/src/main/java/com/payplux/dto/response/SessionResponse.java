package com.payplux.dto.response;

import java.time.LocalDateTime;

public record SessionResponse(
        String device,
        String ip,
        LocalDateTime lastActiveAt
) {}