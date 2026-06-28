package com.loganmharkness.realclock;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClockHud {

    private static final int PADDING = 2;

    private static final DateTimeFormatter FMT_24H_S = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_24H   = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FMT_12H_S = DateTimeFormatter.ofPattern("hh:mm:ss a");
    private static final DateTimeFormatter FMT_12H   = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter FMT_DATE  = DateTimeFormatter.ofPattern("MMM d, yyyy");

    public static void register() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("realclock", "clock"), (graphics, deltaTracker) -> {
            Minecraft client = Minecraft.getInstance();
            ClockConfig config = RealClockMod.getConfig();
            if (!config.visible) return;

            String timeText = LocalTime.now().format(pickFormatter(config));
            String dateText = config.showDate ? LocalDate.now().format(FMT_DATE) : null;
            String gameTimeText = buildGameTimeText(client, config);
            String sessionText = buildSessionText(config);

            int lineHeight = client.font.lineHeight;
            int blockWidth = client.font.width(timeText);
            if (dateText != null)     blockWidth = Math.max(blockWidth, client.font.width(dateText));
            if (gameTimeText != null) blockWidth = Math.max(blockWidth, client.font.width(gameTimeText));
            if (sessionText != null)  blockWidth = Math.max(blockWidth, client.font.width(sessionText));

            int lineCount = 1 + (dateText != null ? 1 : 0) + (gameTimeText != null ? 1 : 0) + (sessionText != null ? 1 : 0);
            int blockHeight = lineHeight * lineCount;

            int screenWidth = client.getWindow().getGuiScaledWidth();
            int screenHeight = client.getWindow().getGuiScaledHeight();
            float scale = config.scale;
            int effectiveWidth  = (int)(screenWidth  / scale);
            int effectiveHeight = (int)(screenHeight / scale);

            int x = switch (config.corner) {
                case TOP_LEFT, BOTTOM_LEFT   -> PADDING + config.offsetX;
                case TOP_RIGHT, BOTTOM_RIGHT -> effectiveWidth - blockWidth - config.offsetX;
            };
            int y = switch (config.corner) {
                case TOP_LEFT, TOP_RIGHT     -> PADDING + config.offsetY;
                case BOTTOM_LEFT, BOTTOM_RIGHT -> effectiveHeight - blockHeight - config.offsetY;
            };

            int argb = 0xFF000000 | (config.color & 0xFFFFFF);
            graphics.pose().pushMatrix();
            graphics.pose().scale(scale, scale);
            if (config.showBackground) {
                graphics.fill(x - PADDING, y - PADDING, x + blockWidth + PADDING, y + blockHeight + PADDING, 0x80000000);
            }
            int currentY = y;
            graphics.text(client.font, timeText, x, currentY, argb, config.textShadow);
            if (dateText != null) {
                currentY += lineHeight;
                graphics.text(client.font, dateText, x, currentY, argb, config.textShadow);
            }
            if (gameTimeText != null) {
                currentY += lineHeight;
                graphics.text(client.font, gameTimeText, x, currentY, argb, config.textShadow);
            }
            if (sessionText != null) {
                currentY += lineHeight;
                graphics.text(client.font, sessionText, x, currentY, argb, config.textShadow);
            }
            graphics.pose().popMatrix();
        });
    }

    private static String buildSessionText(ClockConfig config) {
        Instant start = RealClockMod.getSessionStart();
        if (!config.showSessionTimer || start == null) return null;
        long secs = Duration.between(start, Instant.now()).getSeconds();
        long h = secs / 3600;
        long m = (secs % 3600) / 60;
        return h > 0 ? String.format("Session: %dh %dm", h, m) : String.format("Session: %dm", m);
    }

    private static String buildGameTimeText(Minecraft client, ClockConfig config) {
        if (!config.showGameTime || client.level == null) return null;
        long totalTicks = client.level.getOverworldClockTime();
        long day = totalTicks / 24000 + 1;
        // tick 0 = 6am; shift so 0 = midnight
        long todayTicks = (totalTicks % 24000 + 6000) % 24000;
        int hours   = (int)(todayTicks / 1000);
        int minutes = (int)((todayTicks % 1000) * 60 / 1000);
        String timePart;
        if (config.use24Hour) {
            timePart = String.format("%d:%02d", hours, minutes);
        } else {
            int h12 = hours % 12;
            if (h12 == 0) h12 = 12;
            timePart = String.format("%d:%02d%s", h12, minutes, hours < 12 ? "am" : "pm");
        }
        return String.format("Day %d - %s", day, timePart);
    }

    private static DateTimeFormatter pickFormatter(ClockConfig config) {
        if (config.use24Hour) return config.showSeconds ? FMT_24H_S : FMT_24H;
        return config.showSeconds ? FMT_12H_S : FMT_12H;
    }
}
