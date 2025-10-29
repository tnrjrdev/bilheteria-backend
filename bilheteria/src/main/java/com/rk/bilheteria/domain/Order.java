package com.rk.bilheteria.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    private String buyerName;
    private String buyerWhats;  // 11 dígitos

    private Integer qtyMale;
    private Integer qtyFemale;

    private BigDecimal priceMaleApplied;   // preço aplicado no momento
    private BigDecimal priceFemaleApplied;

    private String couponCode;             // nullable
    private BigDecimal total;              // calculado

    private String txid;                   // máx. 25 chars
    @Column(length = 1024)
    private String emvPayload;             // payload EMV Pix

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ElementCollection
    @CollectionTable(name="order_companions", joinColumns=@JoinColumn(name="order_id"))
    private List<Companion> companions = new ArrayList<>();
}
