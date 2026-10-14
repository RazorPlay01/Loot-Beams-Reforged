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
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
//? if <= 1.21.1 {
/^import net.neoforged.neoforge.client.event.RegisterShadersEvent;
^///?}
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public class NeoforgeClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(final FMLClientSetupEvent event) {
		ModTemplate.onInitializeClient();
	}

	// 1.21.11 compila los shaders de forma perezosa dentro de su RenderPipeline, asi que alli no
	// existe evento de registro (los shaders de packs los descubre ResourceReloadListener).
	//? if <= 1.21.1 {
	/^@SubscribeEvent
	public static void onRegisterShaders(RegisterShadersEvent event) {
		ShaderRegister registrar = new NeoForgeShaderRegister(event);
		LootBeamShaders.registerCoreShaders(registrar);
		LootBeamShaders.registerDroplightCoreShaders(registrar);
		LootBeamShaders.registerCustomCoreShaders(registrar);
	}
	^///?}

	// El pack de ejemplos vive en `resourcepacks/loot_beams_examples` dentro del mod. Fabric API lo
	// resuelve solo; en NeoForge hay que publicarlo como pack finder (misma API en 1.21.1 y 1.21.11).
	@SubscribeEvent
	public static void onAddPackFinders(AddPackFindersEvent event) {
		if (event.getPackType() != PackType.CLIENT_RESOURCES) {
			return;
		}
		event.addPackFinders(
				ModTemplate.id("resourcepacks/loot_beams_examples"),
				PackType.CLIENT_RESOURCES,
				Component.literal("Loot beams examples"),
				PackSource.BUILT_IN,
				false,
				Pack.Position.TOP);
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
	public static void onAddReloadListeners(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new ResourceReloadListener());
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
			ClientSetup.openPresetManagerScreen();
		}
	}

	@SubscribeEvent
	public static void onRegisterCommands(RegisterClientCommandsEvent event) {
		NeoForgeClientCommands.register(event.getDispatcher());
	}
}
*///?}
