package com.rk.bilheteria.api.dto;


public record CheckoutRes(
        Long orderId,
        String txid,
        String emvPayload,
        String status,
        String buyerName,
        String buyerWhats,
        int qtyMale, int qtyFemale,
        String priceMaleApplied,
        String priceFemaleApplied,
        String total
){}
