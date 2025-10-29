package com.rk.bilheteria.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable @Data @NoArgsConstructor @AllArgsConstructor
public class Companion {
    private String nome;
    private String whats; // só dígitos (11)
}
