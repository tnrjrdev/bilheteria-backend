package com.rk.bilheteria.config;

import jakarta.validation.constraints.DecimalMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Validated
@ConfigurationProperties(prefix = "rk.pricing")
public record PricingProps(
        @NotNull @DecimalMin("0.01") BigDecimal male,
        @NotNull @DecimalMin("0.01") BigDecimal female
) {}