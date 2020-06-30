package com.github.dig.chat.tebex;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.java.Log;

import java.text.SimpleDateFormat;
import java.util.function.Supplier;
import java.util.logging.Level;

@Log
public class CreateCoupon implements Supplier<Boolean> {

    private final static String API_COUPON_CREATE = "https://plugin.tebex.io/coupons";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final String apiSecret;
    private final Coupon coupon;

    public CreateCoupon(String apiSecret, Coupon coupon) {
        this.apiSecret = apiSecret;
        this.coupon = coupon;
    }

    @Override
    public Boolean get() {
        try {
            HttpResponse<String> response = Unirest.post(API_COUPON_CREATE)
                    .header("X-Tebex-Secret", apiSecret)
                    .field("code", coupon.getCode())
                    .field("effective_on", coupon.getEffectiveOn().getName())
                    .field("packages", coupon.getPackages())
                    .field("categories", coupon.getCategories())
                    .field("discount_type", coupon.getDiscountType().getName())
                    .field("discount_amount", coupon.getDiscountAmount())
                    .field("discount_percentage", coupon.getDiscountPercentage())
                    .field("redeem_unlimited", coupon.isRedeemUnlimited())
                    .field("expire_never", coupon.isExpireNever())
                    .field("expire_limit", coupon.getExpireLimit())
                    .field("start_date", dateFormat.format(coupon.getStart()))
                    .field("basket_type", coupon.getBasketType().getName())
                    .field("minimum", coupon.getMinimum())
                    .field("discount_application_method", coupon.getDiscountMethod().getType())
                    .field("username", coupon.getUsername())
                    .field("note", coupon.getNote())
                    .asString();
            if (response.getStatus() != 200) {
                log.log(Level.SEVERE, String.format("Unable to create coupon: %s", response.getBody()));
            }
            return response.getStatus() == 200;
        } catch (UnirestException e) {
            log.log(Level.SEVERE, "Unable to create coupon", e);
        }

        return false;
    }
}
