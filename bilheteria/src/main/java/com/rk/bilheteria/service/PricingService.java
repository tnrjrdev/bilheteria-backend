package com.rk.bilheteria.service;

import com.rk.bilheteria.config.PricingProps;
import com.rk.bilheteria.domain.Coupon;
import com.rk.bilheteria.repo.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PricingService {
    private final PricingProps pricingProps;   // <-- trocado
    private final CouponRepository coupons;

    public record AppliedPrice(BigDecimal male, BigDecimal female, String coupon){}

    public AppliedPrice resolve(String couponCode){
        Optional<Coupon> c = (couponCode == null || couponCode.isBlank())
                ? Optional.empty()
                : coupons.findByCodeIgnoreCaseAndActiveTrue(couponCode.trim());

        if (c.isPresent()){
            var cp = c.get();

            // segurança: cupom não pode derrubar para <= 0
            if (cp.getMalePrice() == null || cp.getMalePrice().compareTo(BigDecimal.ZERO) <= 0
                    || cp.getFemalePrice() == null || cp.getFemalePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Cupom com preços inválidos (≤ 0): " + cp.getCode());
            }

            return new AppliedPrice(cp.getMalePrice(), cp.getFemalePrice(), cp.getCode());
        }

        // usa os valores do PricingProps (já validados pelo @DecimalMin)
        return new AppliedPrice(pricingProps.male(), pricingProps.female(), null);
    }
}
