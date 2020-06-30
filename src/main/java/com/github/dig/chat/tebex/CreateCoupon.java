package com.github.dig.chat.tebex;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.java.Log;

import java.text.SimpleDateFormat;
import java.util.logging.Level;

@Log
public class CreateCoupon implements Runnable {

    private final static String API_COUPON_CREATE = "https://plugin.tebex.io/coupons";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final Coupon coupon;

    public CreateCoupon(Coupon coupon) {
        this.coupon = coupon;
    }

    @Override
    public void run() {
        try {
            HttpResponse<String> response = Unirest.post(API_COUPON_CREATE)
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

            log.log(Level.INFO, response.getStatus() + "");
            log.log(Level.INFO, response.getStatusText());
            log.log(Level.INFO, response.getBody());
        } catch (UnirestException e) {
            log.log(Level.SEVERE, "Unable to create coupon", e);
        }
    }
}
