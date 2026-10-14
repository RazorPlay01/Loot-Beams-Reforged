package com.github.razorplay.lootbeams.platform.fabric;

//? fabric {

import com.github.razorplay.lootbeams.ClientSetup;
import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.VersionCompatHelper;
import com.github.razorplay.lootbeams.listeners.ResourceReloadListener;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.platform.fabric.shaders.FabricShaderRegister;
import com.github.razorplay.lootbeams.screens.LootBeamsPresetManagerScreen;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.github.razorplay.lootbeams.shaders.ShaderRegister;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >= 26.1.2 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
*///?}
//? if <= 1.21.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
*///?}
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.item.ItemEntity;

public final class FabricClientSetup {
    private FabricClientSetup() {}

    public static KeyMapping keyBinding;

    public static void registerAll() {
		ParticleManager.GLOW_PARTICLE = Registry.register(
				BuiltInRegistries.PARTICLE_TYPE,
				ParticleManager.GLOW_TEXTURE_ID,
				ParticleManager.createGlowParticleType()
		);

        // 1. Shaders
        //? if <= 1.21.1 {
        /*CoreShaderRegistrationCallback.EVENT.register(context -> {
            ShaderRegister registrar = new FabricShaderRegister(context);
            LootBeamShaders.registerCoreShaders(registrar);
            LootBeamShaders.registerDroplightCoreShaders(registrar);
            LootBeamShaders.registerCustomCoreShaders(registrar);
        });
        *///?} else {
        // 1.21.11: los shaders se compilan de forma perezosa como parte de su RenderPipeline,
        // por lo que no existe un evento de registro de shaders en Fabric. Lo unico que hay
        // que mantener es el descubrimiento de shaders personalizados.
        LootBeamShaders.registerCustomCoreShaders(new FabricShaderRegister());
        //?}

        // 2. Keybind
        //? if <= 1.21.1 {
        /*keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "lootbeams.keybindings.savePreset",
                InputConstants.Type.KEYSYM,
                325,
                "lootbeams.title"));
        *///?} else {
        //? if >= 26.1.2 {
        keyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "lootbeams.keybindings.savePreset",
                VersionCompatHelper.KEY_TYPE,
                325,
                KeyMapping.Category.register(ModTemplate.id("lootbeams"))));
        //?} else {
        /*keyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "lootbeams.keybindings.savePreset",
                InputConstants.Type.KEYSYM,
                325,
                KeyMapping.Category.register(ModTemplate.id("lootbeams"))));
        *///?}
        //?}

        // 3. Entity load/unload
        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity ie) {
                ClientSetup.onEntityLoad(ie, world);
            }
        });
        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (entity instanceof ItemEntity ie) {
                ClientSetup.onEntityUnload(ie, world);
            }
        });

        // 4. Tick (abrir pantalla con la keybind)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                //? if >= 26.2 {
                Minecraft.getInstance().gui.setScreen(
                        new LootBeamsPresetManagerScreen(Minecraft.getInstance().gui.screen()));
                //?} else {
                /*Minecraft.getInstance().setScreen(
                        new LootBeamsPresetManagerScreen(Minecraft.getInstance().screen));
                *///?}
            }
        });

        // 5. Reload listener
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new ResourceReloadListener());

        // 6. Resource pack de ejemplos
        ResourceManagerHelper.registerBuiltinResourcePack(
                ModTemplate.id("loot_beams_examples"),
                FabricLoader.getInstance().getModContainer(ModTemplate.MOD_ID).orElseThrow(),
                ResourcePackActivationType.NORMAL);

        // 7. Partículas
		ParticleManager.registerParticleProviders(new FabricParticleRegistrar());

        // 8. Comandos
        FabricClientCommands.register();
    }
}
//?}
