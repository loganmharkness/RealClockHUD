package com.loganmharkness.realclock;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ClockConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealClockMod.MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("realclock.json");

    public Boolean visible = true;
    public Corner corner = Corner.TOP_RIGHT;
    public Boolean use24Hour = false;
    public Boolean showSeconds = false;
    public Integer color = 0xFFFFFF;
    public Float scale = 1.0f;
    public Boolean showDate = false;
    public Boolean showGameTime = false;
    public Boolean showSessionTimer = false;
    public Boolean showBackground = true;
    public Boolean textShadow = true;
    public Integer offsetX = 4;
    public Integer offsetY = 4;

    public enum Corner {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static ClockConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            ClockConfig defaults = new ClockConfig();
            defaults.save();
            return defaults;
        }
        try {
            String json = Files.readString(CONFIG_PATH);
            ClockConfig loaded;
            try {
                loaded = GSON.fromJson(json, ClockConfig.class);
            } catch (Exception e) {
                loaded = null;
            }
            if (loaded == null) {
                ClockConfig defaults = new ClockConfig();
                defaults.save();
                return defaults;
            }
            if (loaded.validate()) loaded.save();
            return loaded;
        } catch (IOException e) {
            LOGGER.error("[RealClock] Failed to read config, using defaults", e);
            return new ClockConfig();
        }
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("[RealClock] Failed to save config", e);
        }
    }

    private boolean validate() {
        ClockConfig d = new ClockConfig();
        boolean dirty = false;
        if (visible == null)        { visible = d.visible;           dirty = true; }
        if (corner == null)         { corner = d.corner;             dirty = true; }
        if (use24Hour == null)      { use24Hour = d.use24Hour;       dirty = true; }
        if (showSeconds == null)    { showSeconds = d.showSeconds;   dirty = true; }
        if (showDate == null)         { showDate = d.showDate;             dirty = true; }
        if (showGameTime == null)     { showGameTime = d.showGameTime;         dirty = true; }
        if (showSessionTimer == null) { showSessionTimer = d.showSessionTimer; dirty = true; }
        if (showBackground == null)   { showBackground = d.showBackground;     dirty = true; }
        if (textShadow == null)     { textShadow = d.textShadow;     dirty = true; }
        if (color == null || color < 0x000000 || color > 0xFFFFFF) {
            color = d.color;
            dirty = true;
        }
        if (scale == null || scale <= 0f) {
            scale = d.scale;
            dirty = true;
        }
        if (offsetX == null || offsetX < 0 || offsetX > 32) {
            offsetX = d.offsetX;
            dirty = true;
        }
        if (offsetY == null || offsetY < 0 || offsetY > 32) {
            offsetY = d.offsetY;
            dirty = true;
        }
        return dirty;
    }
}
