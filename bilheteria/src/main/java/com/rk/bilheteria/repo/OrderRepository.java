package com.rk.bilheteria.repo;


import com.rk.bilheteria.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTxid(String txid);
}