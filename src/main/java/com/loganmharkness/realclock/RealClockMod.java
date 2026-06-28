package com.loganmharkness.realclock;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.time.Instant;

public class RealClockMod implements ClientModInitializer {

    public static final String MOD_ID = "realclock";
    private static ClockConfig config;
    private static Instant sessionStart = null;

    @Override
    public void onInitializeClient() {
        config = ClockConfig.load();
        ClockKeybindings.register();
        ClockCommand.register();
        ClockHud.register();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, mc) -> sessionStart = Instant.now());
    }

    public static ClockConfig getConfig() { return config; }
    public static Instant getSessionStart() { return sessionStart; }
}
