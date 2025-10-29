package com.rk.bilheteria.controller;

import com.rk.bilheteria.api.dto.CheckoutReq;
import com.rk.bilheteria.api.dto.CheckoutRes;
import com.rk.bilheteria.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/checkout") @RequiredArgsConstructor
public class CheckoutController {
    private final CheckoutService service;

    @PostMapping
    public ResponseEntity<CheckoutRes> create(@Valid @RequestBody CheckoutReq req){
        return ResponseEntity.ok(service.create(req));
    }
}
