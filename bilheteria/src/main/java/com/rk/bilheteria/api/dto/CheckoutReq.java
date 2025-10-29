package com.rk.bilheteria.api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CheckoutReq(
        @NotBlank String buyerName,
        @Pattern(regexp="\\d{11}") String buyerWhats,
        @Min(0) @Max(10) int qtyMale,
        @Min(0) @Max(10) int qtyFemale,
        String couponCode,
        List<CompanionReq> companions
){}
