package com.payplux.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String role;
    private String refreshToken;
//    private String tokenType = "Bearer";
}
