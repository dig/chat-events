package com.github.dig.chat.event;

import com.github.dig.chat.ChatEvents;
import com.github.dig.chat.MessageParser;
import com.github.dig.chat.tebex.Coupon;
import com.github.dig.chat.tebex.CreateCoupon;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

@Log
public class CouponEvent implements BaseEvent {

    private static final String COUPON_KEY = "coupon";

    private final ChatEvents chatEvents = ChatEvents.getInstance();
    private final ConfigurationSection config = chatEvents.getConfig().getConfigurationSection("events." + COUPON_KEY);
    private final ConfigurationSection msgConfig = chatEvents.getConfig().getConfigurationSection("messages." + COUPON_KEY);

    @Override
    public boolean canRun() {
        return config.getBoolean("enabled");
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {
        ConfigurationSection apiConfig = config.getConfigurationSection("api");

        String secret = apiConfig.getString("secret");
        String code = randomAlphaNumeric(config.getInt("code-length", 8));

        Coupon.ItemType itemType = Coupon.ItemType.valueOf(apiConfig.getString("effective_on"));
        int[] packages = apiConfig.getIntegerList("packages").stream().mapToInt(i->i).toArray();
        int[] categories = apiConfig.getIntegerList("categories").stream().mapToInt(i->i).toArray();

        Coupon.DiscountType discountType = Coupon.DiscountType.valueOf(apiConfig.getString("discount_type"));
        double discountAmount = apiConfig.getDouble("discount_amount");
        double discountPercentage = apiConfig.getDouble("discount_percentage");

        boolean redeemUnlimited = apiConfig.getBoolean("redeem_unlimited");
        boolean expireNever = apiConfig.getBoolean("expire_never");
        int expireLimit = apiConfig.getInt("expire_limit");

        Coupon.BasketType basketType = Coupon.BasketType.valueOf(apiConfig.getString("basket_type"));
        double minimum = apiConfig.getDouble("minimum");
        Coupon.DiscountMethod discountMethod = Coupon.DiscountMethod.valueOf(apiConfig.getString("discount_application_method"));
        String note = apiConfig.getString("note");

        Coupon coupon = Coupon.builder()
                .code(code)
                .effectiveOn(itemType)
                .packages(packages)
                .categories(categories)
                .discountType(discountType)
                .discountAmount(discountAmount)
                .discountPercentage(discountPercentage)
                .redeemUnlimited(redeemUnlimited)
                .expireNever(expireNever)
                .expireLimit(expireLimit)
                .start(new Date())
                .basketType(basketType)
                .minimum(minimum)
                .discountMethod(discountMethod)
                .note(note)
                .build();

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(new CreateCoupon(secret, coupon));
        Bukkit.getScheduler().runTaskAsynchronously(ChatEvents.getInstance(), () -> {
            try {
                if (future.get()) {
                    Bukkit.getScheduler().runTask(ChatEvents.getInstance(), () -> {
                        String announceMsg = msgConfig.getString("announce")
                                .replaceAll("%code", code);
                        Bukkit.broadcastMessage(MessageParser.parse(announceMsg));
                    });
                }
            } catch (InterruptedException | ExecutionException e) {
                log.log(Level.SEVERE, "Unable to create coupon", e);
            }
        });
    }

    @Override
    public void stop() {
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
