package com.loganmharkness.realclock;

import net.fabricmc.api.ClientModInitializer;

public class RealClockMod implements ClientModInitializer {

    public static final String MOD_ID = "realclock";
    private static ClockConfig config;

    @Override
    public void onInitializeClient() {
        config = ClockConfig.load();
        ClockKeybindings.register();
        ClockCommand.register();
        ClockHud.register();
    }

    public static ClockConfig getConfig() {
        return config;
    }
}
