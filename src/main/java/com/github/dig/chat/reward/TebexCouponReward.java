package com.github.dig.chat.reward;

import com.github.dig.chat.ChatEvents;
import com.github.dig.chat.tebex.Coupon;
import com.github.dig.chat.tebex.CreateCoupon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Date;

public class TebexCouponReward implements Reward {

    @Override
    public void run(ConfigurationSection config, Player player) {
        ConfigurationSection apiConfig = config.getConfigurationSection("api");

        String code = randomAlphaNumeric(config.getInt("code-length", 8));
        config.getStringList("on-create.messages").stream()
                .map(s -> String.format(s, code))
                .forEach(s -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', s)));

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

        boolean restrictToUsername = apiConfig.getBoolean("restrict_to_username");
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
                .username(player.getName())
                .note(note)
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(ChatEvents.getInstance(), new CreateCoupon(coupon));
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
