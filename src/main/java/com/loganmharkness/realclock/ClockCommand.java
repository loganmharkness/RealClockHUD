package com.loganmharkness.realclock;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class ClockCommand {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(
                literal("realclock")
                    .then(literal("corner")
                        .then(argument("value", StringArgumentType.word())
                            .suggests((ctx, builder) -> {
                                for (ClockConfig.Corner c : ClockConfig.Corner.values()) {
                                    builder.suggest(c.name());
                                }
                                return builder.buildFuture();
                            })
                            .executes(ctx -> {
                                String val = StringArgumentType.getString(ctx, "value");
                                try {
                                    ClockConfig config = RealClockMod.getConfig();
                                    config.corner = ClockConfig.Corner.valueOf(val.toUpperCase());
                                    config.save();
                                    ctx.getSource().sendFeedback(Component.literal("Corner set to " + config.corner.name()));
                                } catch (IllegalArgumentException e) {
                                    ctx.getSource().sendError(Component.literal("Invalid corner: " + val + ". Use TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, or BOTTOM_RIGHT"));
                                }
                                return 1;
                            })
                        )
                    )
                    .then(literal("color")
                        .then(argument("hex", StringArgumentType.word())
                            .executes(ctx -> {
                                String hex = StringArgumentType.getString(ctx, "hex").replace("#", "");
                                try {
                                    int color = Integer.parseInt(hex, 16) & 0xFFFFFF;
                                    ClockConfig config = RealClockMod.getConfig();
                                    config.color = color;
                                    config.save();
                                    ctx.getSource().sendFeedback(Component.literal("Color set to #" + String.format("%06X", color)));
                                } catch (NumberFormatException e) {
                                    ctx.getSource().sendError(Component.literal("Invalid hex color: " + hex));
                                }
                                return 1;
                            })
                        )
                    )
                    .then(literal("24h")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.use24Hour = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("24-hour format: " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("seconds")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.showSeconds = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Show seconds: " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("scale")
                        .then(argument("value", FloatArgumentType.floatArg(0.25f, 4.0f))
                            .executes(ctx -> {
                                float val = FloatArgumentType.getFloat(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.scale = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Scale set to " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("offsetx")
                        .then(argument("value", IntegerArgumentType.integer(-500, 500))
                            .executes(ctx -> {
                                int val = IntegerArgumentType.getInteger(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.offsetX = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("X offset set to " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("offsety")
                        .then(argument("value", IntegerArgumentType.integer(-500, 500))
                            .executes(ctx -> {
                                int val = IntegerArgumentType.getInteger(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.offsetY = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Y offset set to " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("shadow")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.textShadow = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Text shadow " + (val ? "enabled" : "disabled")));
                                return 1;
                            })
                        )
                    )
                    .then(literal("background")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.showBackground = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Background " + (val ? "enabled" : "disabled")));
                                return 1;
                            })
                        )
                    )
                    .then(literal("sessiontimer")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.showSessionTimer = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Show session timer: " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("gametime")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.showGameTime = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Show game time: " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("date")
                        .then(argument("value", BoolArgumentType.bool())
                            .executes(ctx -> {
                                boolean val = BoolArgumentType.getBool(ctx, "value");
                                ClockConfig config = RealClockMod.getConfig();
                                config.showDate = val;
                                config.save();
                                ctx.getSource().sendFeedback(Component.literal("Show date: " + val));
                                return 1;
                            })
                        )
                    )
                    .then(literal("toggle")
                        .executes(ctx -> {
                            ClockConfig config = RealClockMod.getConfig();
                            config.visible = !config.visible;
                            config.save();
                            ctx.getSource().sendFeedback(Component.literal("Clock " + (config.visible ? "shown" : "hidden")));
                            return 1;
                        })
                    )
            )
        );
    }
}
