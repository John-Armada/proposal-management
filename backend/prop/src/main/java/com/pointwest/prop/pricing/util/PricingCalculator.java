package com.pointwest.prop.pricing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PricingCalculator {

        private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
        private static final int MATH_SCALE = 10;
        private static final int MONEY_SCALE = 2;

        private PricingCalculator() {
        }

        /**
         * lineTotal = quantity x unitPrice x (1 - discountPct/100) x (1 + taxPct/100),
         * rounded to 2 decimal places. Null discount/tax are treated as 0.
         */
        public static BigDecimal computeLineTotal(BigDecimal quantity, BigDecimal unitPrice,
                        BigDecimal discountPct, BigDecimal taxPct) {

                BigDecimal safeQuantity = quantity == null ? BigDecimal.ZERO : quantity;
                BigDecimal safeUnitPrice = unitPrice == null ? BigDecimal.ZERO : unitPrice;
                BigDecimal safeDiscount = discountPct == null ? BigDecimal.ZERO : discountPct;
                BigDecimal safeTax = taxPct == null ? BigDecimal.ZERO : taxPct;

                BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                                safeDiscount.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));
                BigDecimal taxMultiplier = BigDecimal.ONE.add(
                                safeTax.divide(HUNDRED, MATH_SCALE, RoundingMode.HALF_UP));

                return safeQuantity.multiply(safeUnitPrice)
                                .multiply(discountMultiplier)
                                .multiply(taxMultiplier)
                                .setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }
}