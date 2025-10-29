package com.rk.bilheteria.service;

import com.rk.bilheteria.api.dto.CheckoutReq;
import com.rk.bilheteria.api.dto.CheckoutRes;
import com.rk.bilheteria.config.RkProps;
import com.rk.bilheteria.domain.*;
import com.rk.bilheteria.payments.PixEmvBuilder;
import com.rk.bilheteria.repo.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutService {
    private final OrderRepository orders;
    private final PricingService pricing;
    // troque RkProps por PaymentsProps
    private final com.rk.bilheteria.config.PaymentsProps payments;

    @Transactional
    public CheckoutRes create(CheckoutReq req){
        if (req.qtyMale() + req.qtyFemale() <= 0) {
            throw new IllegalArgumentException("Selecione ao menos 1 ingresso.");
        }

        var applied = pricing.resolve(req.couponCode());
        BigDecimal total =
                applied.male().multiply(BigDecimal.valueOf(req.qtyMale()))
                        .add(applied.female().multiply(BigDecimal.valueOf(req.qtyFemale())));

        if (total == null || total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Preço configurado inválido: total calculado <= 0. Verifique rk.pricing.male/female e cupons.");
        }

        String txid = ("RK" + System.currentTimeMillis()
                + org.apache.commons.lang3.RandomStringUtils.randomNumeric(3));
        txid = txid.substring(0, Math.min(25, txid.length()));

        // use os getters do record PaymentsProps
        String emv = com.rk.bilheteria.payments.PixEmvBuilder.build(
                payments.pixKey(),
                payments.merchant(),
                payments.city(),
                total,
                txid,
                "Baile do RK"
        );

        var order = Order.builder()
                .buyerName(req.buyerName())
                .buyerWhats(req.buyerWhats())
                .qtyMale(req.qtyMale())
                .qtyFemale(req.qtyFemale())
                .priceMaleApplied(applied.male())
                .priceFemaleApplied(applied.female())
                .couponCode(applied.coupon())
                .total(total)
                .txid(txid)
                .emvPayload(emv)
                .status(OrderStatus.PENDENTE)
                .companions(req.companions()==null ? java.util.List.of()
                        : req.companions().stream()
                        .map(c -> new Companion(c.nome(), c.whats()))
                        .collect(java.util.stream.Collectors.toList()))
                .build();

        order = orders.save(order);

        return new CheckoutRes(
                order.getId(), order.getTxid(), order.getEmvPayload(), order.getStatus().name(),
                order.getBuyerName(), order.getBuyerWhats(),
                order.getQtyMale(), order.getQtyFemale(),
                format(order.getPriceMaleApplied()),
                format(order.getPriceFemaleApplied()),
                format(order.getTotal())
        );
    }

    private String format(BigDecimal v){
        return "R$ " + v.setScale(2).toPlainString().replace('.', ',');
    }
}
