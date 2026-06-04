package com.loganmharkness.realclock;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockHud {

    private static final int PADDING = 2;

    private static final DateTimeFormatter FMT_24H_S = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_24H   = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_12H_S = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter FMT_12H   = DateTimeFormatter.ofPattern("hh:mm a");

    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("realclock", "clock"), (graphics, deltaTracker) -> {
            Minecraft client = Minecraft.getInstance();
            ClockConfig config = RealClockMod.getConfig();
            if (!config.visible || client.options.hideGui) return;
            String timeText = LocalTime.now().format(pickFormatter(config));
            int textWidth = client.font.width(timeText);
            int screenWidth = client.getWindow().getGuiScaledWidth();
            int screenHeight = client.getWindow().getGuiScaledHeight();

            float scale = config.scale;
            int effectiveWidth = (int)(screenWidth / scale);
            int effectiveHeight = (int)(screenHeight / scale);

            int x = switch (config.corner) {
                case TOP_LEFT, BOTTOM_LEFT -> PADDING + config.offsetX;
                case TOP_RIGHT, BOTTOM_RIGHT -> effectiveWidth - textWidth - config.offsetX;
            };

            int y = switch (config.corner) {
                case TOP_LEFT, TOP_RIGHT -> PADDING + config.offsetY;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> effectiveHeight - client.font.lineHeight - config.offsetY;
            };

            int argb = 0xFF000000 | (config.color & 0xFFFFFF);
            graphics.pose().pushMatrix();
            graphics.pose().scale(scale, scale);
            if (config.showBackground) {
                graphics.fill(x - PADDING, y - PADDING, x + textWidth + PADDING, y + client.font.lineHeight + PADDING, 0x80000000);
            }
            graphics.text(client.font, timeText, x, y, argb, config.textShadow);
            graphics.pose().popMatrix();
        });
    }

    private static DateTimeFormatter pickFormatter(ClockConfig config) {
        if (config.use24Hour) return config.showSeconds ? FMT_24H_S : FMT_24H;
        return config.showSeconds ? FMT_12H_S : FMT_12H;
    }
}
