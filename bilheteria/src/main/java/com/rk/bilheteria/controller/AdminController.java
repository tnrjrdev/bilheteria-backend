package com.rk.bilheteria.controller;

import com.rk.bilheteria.domain.Order;
import com.rk.bilheteria.domain.OrderStatus;
import com.rk.bilheteria.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
public class AdminController {
    private final OrderRepository orders;

    @GetMapping("/orders")
    public Page<Order> list(@RequestParam(defaultValue="0") int page,
                            @RequestParam(defaultValue="20") int size){
        return orders.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> status(@PathVariable Long id, @RequestParam OrderStatus status){
        var o = orders.findById(id).orElse(null);
        if(o==null) return ResponseEntity.notFound().build();
        o.setStatus(status);
        orders.save(o);
        return ResponseEntity.noContent().build();
    }
}