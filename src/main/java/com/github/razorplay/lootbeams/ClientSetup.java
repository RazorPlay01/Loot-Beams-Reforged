package com.github.razorplay.lootbeams;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.dconfig.DynamicConfig;
import com.github.razorplay.lootbeams.dconfig.events.ConfigEvents;
import com.github.razorplay.lootbeams.events.PresetEvents;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.events.ResourceEvents;
import com.github.razorplay.lootbeams.features.BeamOpacityOnApproach;
import com.github.razorplay.lootbeams.features.BeamSizeOnApproach;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.features.CustomRarity;
import com.github.razorplay.lootbeams.listeners.ResourceReloadListener;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.github.razorplay.lootbeams.managers.TooltipManager;
import com.github.razorplay.lootbeams.renderers.HudRenderer;
import com.github.razorplay.lootbeams.renderers.LootBeamRenderer;
import com.github.razorplay.lootbeams.screens.LootBeamsPresetManagerScreen;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class ClientSetup {
    private ClientSetup() {
        /* This utility class should not be instantiated */
    }

    public static KeyMapping keyBinding;

    public static void registerKeyBindings() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("lootbeams.keybindings.savePreset", InputConstants.Type.KEYSYM, 325, "lootbeams.title"));
    }

    public static void registerCoreShaderRegistrationEvents() {
        CoreShaderRegistrationCallback.EVENT.register(shaderRegistry -> {
            LootBeamShaders.registerCoreShaders(shaderRegistry);
            LootBeamShaders.registerDroplightCoreShaders(shaderRegistry);
            LootBeamShaders.registerCustomCoreShaders(shaderRegistry);
        });
    }

    public static void registerHudRenderEvents() {
        RenderEvents.HUD.register(HudRenderer::onHudRender);
    }

    public static void registerWorldRenderEvents() {
        RenderEvents.BEFORE_PARTICLES.register(RenderManager::onWorldRenderBeforeParticles);
        RenderEvents.AFTER_TRANSLUCENT.register(RenderManager::onWorldRenderAfterTranslucent);
        RenderEvents.AFTER_WEATHER.register(RenderManager::onWorldRenderAfterWeather);
        RenderEvents.BEFORE_END.register(RenderManager::onWorldRenderBeforeEnd);
        RenderEvents.END.register(RenderManager::onWorldRenderEnd);
    }

    public static void registerEntityEvents() {
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity itemEntity) {
                TooltipManager.onEntityLoad(itemEntity, world);
                RenderManager.onEntityLoad(itemEntity, world);
            }
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity itemEntity) {
                TooltipManager.onEntityUnload(itemEntity, world);
                RenderManager.onEntityUnload(itemEntity, world);
            }
        });
    }

    public static void registerClientEvents() {
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListener());
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                Minecraft.getInstance().setScreen(new LootBeamsPresetManagerScreen(Minecraft.getInstance().screen));
            }
        });
    }

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT
                .register(
                        (dispatcher, dedicated) -> dispatcher.register(
                                ClientCommandManager.literal("lootbeams")
                                        .then(
                                                ClientCommandManager.literal("save-preset")
                                                        .then(ClientCommandManager.argument("presetName", StringArgumentType.word()).executes(context -> {
                                                            String presetName = StringArgumentType.getString(context, "presetName");
                                                            PresetManager.savePreset(presetName);
                                                            return 1;
                                                        }))
                                        )
                                        .then(
                                                ClientCommandManager.literal("item-custom-config")
                                                        .then(
                                                                ClientCommandManager.literal("available")
                                                                        .executes(
                                                                                context -> {
                                                                                    for (DynamicConfig.Control.Field field : ModTemplate.configManager.getFieldsByGroup(Configuration.Groups.VISUAL)) {
                                                                                        Player player = Minecraft.getInstance().player;
                                                                                        if (player != null) {
                                                                                            MutableComponent fieldText = Component.literal("")
                                                                                                    .append(
                                                                                                            Component.literal(field.saveKey).withStyle(style -> style.withColor(ChatFormatting.BLUE))
                                                                                                    )
                                                                                                    .append(Component.literal(" "));
                                                                                            MutableComponent fieldDescriptionText = Component.literal("[ ")
                                                                                                    .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                    .append(Component.literal("type=").withStyle(style -> style.withColor(ChatFormatting.GRAY)));
                                                                                            if (field.type == List.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal("List([\"val1\", \"val2\", \"...\"])")
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == ParticleManager.ParticleTexture.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal("String(lootbeams:particle_id)")
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == GlowEffectManager.GlowEffectTexture.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal("String(lootbeams:glow_effect_id)")
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == LootBeamShaders.Shader.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal(
                                                                                                                        "String("
                                                                                                                                + String.join(
                                                                                                                                ", ",
                                                                                                                                Arrays.stream(LootBeamShaders.Shader.values()).map(shader -> shader.name().toLowerCase()).toList()
                                                                                                                        )
                                                                                                                                + ")"
                                                                                                                )
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == LootBeamShaders.CustomShader.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal(
                                                                                                                        "String("
                                                                                                                                + String.join(
                                                                                                                                ", ",
                                                                                                                                LootBeamShaders.CustomShader.values().stream().map(shader -> shader.name().toLowerCase()).toList()
                                                                                                                        )
                                                                                                                                + ")"
                                                                                                                )
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == BeamSizeOnApproach.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal(
                                                                                                                        "String("
                                                                                                                                + String.join(
                                                                                                                                ", ",
                                                                                                                                Arrays.stream(BeamOpacityOnApproach.values()).map(option -> option.name().toLowerCase()).toList()
                                                                                                                        )
                                                                                                                                + ")"
                                                                                                                )
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else if (field.type == BeamSizeOnApproach.class) {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal(
                                                                                                                        "String("
                                                                                                                                + String.join(
                                                                                                                                ", ",
                                                                                                                                Arrays.stream(BeamSizeOnApproach.values()).map(option -> option.name().toLowerCase()).toList()
                                                                                                                        )
                                                                                                                                + ")"
                                                                                                                )
                                                                                                                .withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            } else {
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                        Component.literal(field.type.toString()).withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                                );
                                                                                            }

                                                                                            if (field.type == int.class || field.type == float.class || field.type == double.class || field.type == long.class) {
                                                                                                DecimalFormat format = new DecimalFormat("0.#####");
                                                                                                fieldDescriptionText = fieldDescriptionText.append(
                                                                                                                Component.literal(", min=").withStyle(style -> style.withColor(ChatFormatting.GRAY))
                                                                                                        )
                                                                                                        .append(
                                                                                                                Component.literal(format.format(field.minValue))
                                                                                                                        .withStyle(style -> style.withColor(ChatFormatting.RED))
                                                                                                        )
                                                                                                        .append(Component.literal(", max=").withStyle(style -> style.withColor(ChatFormatting.GRAY)))
                                                                                                        .append(
                                                                                                                Component.literal(format.format(field.maxValue))
                                                                                                                        .withStyle(style -> style.withColor(ChatFormatting.GREEN))
                                                                                                        );
                                                                                            }

                                                                                            fieldDescriptionText = fieldDescriptionText.append(
                                                                                                    Component.literal(" ]").withStyle(style -> style.withColor(ChatFormatting.WHITE))
                                                                                            );
                                                                                            player.sendSystemMessage(fieldText.append(fieldDescriptionText));
                                                                                        }
                                                                                    }

                                                                                    return 1;
                                                                                }
                                                                        )
                                                        )
                                        )
                        )
                );
    }

    public static void registerCustomEvents() {
        ConfigEvents.SAVE.register(() -> {
            CustomLootBeamsConfig.onConfigurationChange();
            LootBeamRenderer.onConfigurationChange();
        });
        PresetEvents.APPLY_PRESET.register(presetName -> {
            CustomLootBeamsConfig.onConfigurationChange();
            LootBeamRenderer.onConfigurationChange();
        });
        ResourceEvents.RESOURCE_RELOAD.register((synchronizer, resourceManager, prepareExecutor, applyExecutor) -> {
            TooltipManager.onResourcesReload();
            CustomLootBeamsConfig.onResourcesReload();
            CustomRarity.onResourcesReload(resourceManager);
            PresetManager.onResourcesReload(resourceManager);
            GlowEffectManager.onResourceManagerReload(resourceManager, prepareExecutor);
        });
		ResourceManagerHelper.registerBuiltinResourcePack(
				ModTemplate.id("loot_beams_examples"),
				FabricLoader.getInstance().getModContainer(ModTemplate.MOD_ID).get(),
				ResourcePackActivationType.NORMAL
		);
    }
}
