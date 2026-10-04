package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.dconfig.DynamicConfig;
import com.github.razorplay.lootbeams.features.BeamOpacityOnApproach;
import com.github.razorplay.lootbeams.features.BeamSizeOnApproach;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

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
        for (DynamicConfig.Control.Field field : ModTemplate.configManager.getFieldsByGroup(Configuration.Groups.VISUAL)) {
            Player player = Minecraft.getInstance().player;
            if (player == null) continue;

            MutableComponent fieldText = Component.literal("")
                    .append(Component.literal(field.saveKey).withStyle(s -> s.withColor(ChatFormatting.BLUE)))
                    .append(Component.literal(" "));

            MutableComponent desc = Component.literal("[ ")
                    .withStyle(s -> s.withColor(ChatFormatting.WHITE))
                    .append(Component.literal("type=").withStyle(s -> s.withColor(ChatFormatting.GRAY)));

            if (field.type == List.class) {
                desc = desc.append(Component.literal("List([\"val1\", \"val2\", \"...\"])")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == ParticleManager.ParticleTexture.class) {
                desc = desc.append(Component.literal("String(lootbeams:particle_id)")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == GlowEffectManager.GlowEffectTexture.class) {
                desc = desc.append(Component.literal("String(lootbeams:glow_effect_id)")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == LootBeamShaders.Shader.class) {
                desc = desc.append(Component.literal("String("
                                + String.join(", ", Arrays.stream(LootBeamShaders.Shader.values())
                                        .map(sh -> sh.name().toLowerCase()).toList()) + ")")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == LootBeamShaders.CustomShader.class) {
                desc = desc.append(Component.literal("String("
                                + String.join(", ", LootBeamShaders.CustomShader.values().stream()
                                        .map(sh -> sh.name().toLowerCase()).toList()) + ")")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == BeamOpacityOnApproach.class) {
                desc = desc.append(Component.literal("String("
                                + String.join(", ", Arrays.stream(BeamOpacityOnApproach.values())
                                        .map(o -> o.name().toLowerCase()).toList()) + ")")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else if (field.type == BeamSizeOnApproach.class) {
                desc = desc.append(Component.literal("String("
                                + String.join(", ", Arrays.stream(BeamSizeOnApproach.values())
                                        .map(o -> o.name().toLowerCase()).toList()) + ")")
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            } else {
                desc = desc.append(Component.literal(field.type.toString())
                        .withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            }

            if (field.type == int.class || field.type == float.class
                    || field.type == double.class || field.type == long.class) {
                DecimalFormat fmt = new DecimalFormat("0.#####");
                desc = desc.append(Component.literal(", min=").withStyle(s -> s.withColor(ChatFormatting.GRAY)))
                        .append(Component.literal(fmt.format(field.minValue)).withStyle(s -> s.withColor(ChatFormatting.RED)))
                        .append(Component.literal(", max=").withStyle(s -> s.withColor(ChatFormatting.GRAY)))
                        .append(Component.literal(fmt.format(field.maxValue)).withStyle(s -> s.withColor(ChatFormatting.GREEN)));
            }

            desc = desc.append(Component.literal(" ]").withStyle(s -> s.withColor(ChatFormatting.WHITE)));
            player.sendSystemMessage(fieldText.append(desc));
        }
    }
}
//?}