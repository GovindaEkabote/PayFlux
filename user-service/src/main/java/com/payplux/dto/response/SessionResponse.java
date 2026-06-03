package com.payplux.dto.response;

import java.time.LocalDateTime;

public record SessionResponse(
        Long id,
        String device,
        String ip,
        LocalDateTime lastActiveAt
) {}