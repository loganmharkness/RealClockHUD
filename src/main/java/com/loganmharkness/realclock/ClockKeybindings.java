package com.loganmharkness.realclock;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class ClockKeybindings {

    public static void register() {
        KeyMapping toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.realclock.toggle",
            InputConstants.KEY_K,
            KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                ClockConfig config = RealClockMod.getConfig();
                config.visible = !config.visible;
                config.save();
            }
        });
    }
}
