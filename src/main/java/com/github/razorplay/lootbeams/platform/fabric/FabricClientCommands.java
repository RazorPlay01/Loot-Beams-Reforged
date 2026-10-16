package com.github.razorplay.lootbeams.platform.fabric;

//? fabric {

import com.github.razorplay.lootbeams.helpers.CommandHelper;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
//? if >= 26.1.2 {
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
//?} else {
/*import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
*///?}
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class FabricClientCommands {
    private FabricClientCommands() {}

    private static LiteralArgumentBuilder<FabricClientCommandSource> literal(String name) {
        //? if >= 26.1.2 {
        return ClientCommands.literal(name);
        //?} else {
        /*return ClientCommandManager.literal(name);
        *///?}
    }

    private static <T> RequiredArgumentBuilder<FabricClientCommandSource, T> argument(String name, ArgumentType<T> type) {
        //? if >= 26.1.2 {
        return ClientCommands.argument(name, type);
        //?} else {
        /*return ClientCommandManager.argument(name, type);
        *///?}
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) ->
                dispatcher.register(literal("lootbeams")
                        .then(literal("save-preset")
                                .then(argument("presetName", StringArgumentType.word())
                                        .executes(ctx -> {
                                            PresetManager.savePreset(StringArgumentType.getString(ctx, "presetName"));
                                            return 1;
                                        })))
                        .then(literal("item-custom-config")
                                .then(literal("available")
                                        .executes(ctx -> {
                                            printAvailableFields();
                                            return 1;
                                        })))
                ));
    }

    private static void printAvailableFields() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        CommandHelper.printAvailableFields(player);
    }
}
//?}
