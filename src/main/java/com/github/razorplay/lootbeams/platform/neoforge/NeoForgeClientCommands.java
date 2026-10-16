package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

/*import com.github.razorplay.lootbeams.helpers.CommandHelper;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public final class NeoForgeClientCommands {
    private NeoForgeClientCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("lootbeams")
                .then(Commands.literal("save-preset")
                        .then(Commands.argument("presetName", StringArgumentType.word())
                                .executes(ctx -> {
                                    PresetManager.savePreset(StringArgumentType.getString(ctx, "presetName"));
                                    return 1;
                                })))
                .then(Commands.literal("item-custom-config")
                        .then(Commands.literal("available")
                                .executes(ctx -> {
                                    printAvailableFields();
                                    return 1;
                                })))
        );
    }

    private static void printAvailableFields() {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        CommandHelper.printAvailableFields(player);
    }
}
*///?}