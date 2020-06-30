package com.github.dig.chat.tebex;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class Coupon {

    private final String code;
    private final ItemType effectiveOn;

    private final int[] packages;
    private final int[] categories;

    private final DiscountType discountType;
    private final double discountAmount;
    private final double discountPercentage;

    private final boolean redeemUnlimited;
    private final boolean expireNever;

    private int expireLimit;
    private final Date start;

    private final BasketType basketType;
    private final double minimum;
    private final DiscountMethod discountMethod;

    private String username;
    private final String note;

    public enum ItemType {
        PACKAGE("package"),
        CATEGORY("category"),
        CART("cart");

        @Getter
        private String name;
        ItemType(String name) {
            this.name = name;
        }
    }

    public enum DiscountType {
        PERCENTAGE("percentage"),
        VALUE("value");

        @Getter
        private String name;
        DiscountType(String name) {
            this.name = name;
        }
    }

    public enum BasketType {
        SINGLE("single"),
        SUBSCRIPTION("subscription"),
        BOTH("both");

        @Getter
        private String name;
        BasketType(String name) {
            this.name = name;
        }
    }

    public enum DiscountMethod {
        EACH_PACKAGE(0),
        BASKET_BEFORE_SALES(1),
        BASKET_AFTER_SALES(2);

        @Getter
        private int type;
        DiscountMethod(int type) {
            this.type = type;
        }

        public DiscountMethod from(int value) {
            DiscountMethod method = null;
            for (DiscountMethod x : values()) {
                if (x.getType() == value) {
                    method = x;
                }
            }
            return method;
        }
    }
}
