package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

/*import com.github.razorplay.lootbeams.ClientSetup;
import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.listeners.ResourceReloadListener;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.platform.neoforge.shaders.NeoForgeShaderRegister;
import com.github.razorplay.lootbeams.screens.LootBeamsPresetManagerScreen;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.github.razorplay.lootbeams.shaders.ShaderRegister;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModTemplate.onInitializeClient();
	}

	@SubscribeEvent
	public static void onRegisterShaders(RegisterShadersEvent event) {
		ShaderRegister registrar = new NeoForgeShaderRegister(event);
		LootBeamShaders.registerCoreShaders(registrar);
		LootBeamShaders.registerDroplightCoreShaders(registrar);
		LootBeamShaders.registerCustomCoreShaders(registrar);
	}

	@SubscribeEvent
	public static void onRegister(RegisterEvent event) {
		event.register(BuiltInRegistries.PARTICLE_TYPE.key(), helper -> {
			ParticleManager.GLOW_PARTICLE = Registry.register(
					BuiltInRegistries.PARTICLE_TYPE,
					ParticleManager.GLOW_TEXTURE_ID,
					ParticleManager.createGlowParticleType()
			);
		});
	}

	@SubscribeEvent
	public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
		ParticleManager.registerParticleProviders(new NeoForgeParticleRegistrar(event));
	}

	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new ResourceReloadListener());
	}

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			ClientSetup.onEntityLoad(ie, event.getLevel());
		}
	}

	@SubscribeEvent
	public static void onEntityLeave(EntityLeaveLevelEvent event) {
		if (event.getEntity() instanceof ItemEntity ie) {
			ClientSetup.onEntityUnload(ie, event.getLevel());
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event) {
		if (NeoForgeKeyMappings.OPEN_PRESET_SCREEN == null) return;
		while (NeoForgeKeyMappings.OPEN_PRESET_SCREEN.consumeClick()) {
			Minecraft.getInstance().setScreen(new LootBeamsPresetManagerScreen(Minecraft.getInstance().screen));
		}
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterClientCommandsEvent event) {
		NeoForgeClientCommands.register(event.getDispatcher());
	}
}
*///?}
