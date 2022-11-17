package com.backend.seperate.jpa.entity.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {
    private String grantType;
    private String accessToken;
    private Long tokenExpiresIn;
}
