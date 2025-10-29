package com.rk.bilheteria.api.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CompanionReq(
        @NotBlank String nome,
        @Pattern(regexp="\\d{11}") String whats
){}
