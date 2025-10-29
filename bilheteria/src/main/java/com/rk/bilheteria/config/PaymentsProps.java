package com.rk.bilheteria.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "rk.payments")
public record PaymentsProps(
        @NotBlank String pixKey,
        @NotBlank String merchant,
        @NotBlank String city
) {}
