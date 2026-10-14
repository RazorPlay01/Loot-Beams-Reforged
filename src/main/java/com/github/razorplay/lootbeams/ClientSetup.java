package com.github.razorplay.lootbeams;

import com.github.razorplay.lootbeams.dconfig.events.ConfigEvents;
import com.github.razorplay.lootbeams.events.PresetEvents;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.events.ResourceEvents;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.features.CustomRarity;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.github.razorplay.lootbeams.managers.TooltipManager;
import com.github.razorplay.lootbeams.renderers.HudRenderer;
import com.github.razorplay.lootbeams.renderers.LootBeamRenderer;
import com.github.razorplay.lootbeams.screens.LootBeamsPresetManagerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

public class ClientSetup {
	private ClientSetup() {
		/* utility class */
	}

	/**
	 * Lógica común: registrar listeners en los SimpleEvent propios.
	 */
	public static void registerCommonEvents() {
		RenderEvents.HUD.register(HudRenderer::onHudRender);
		RenderEvents.BEFORE_PARTICLES.register(RenderManager::onWorldRenderBeforeParticles);
		RenderEvents.AFTER_TRANSLUCENT.register(RenderManager::onWorldRenderAfterTranslucent);
		RenderEvents.AFTER_WEATHER.register(RenderManager::onWorldRenderAfterWeather);
		RenderEvents.BEFORE_END.register(RenderManager::onWorldRenderBeforeEnd);
		RenderEvents.END.register(RenderManager::onWorldRenderEnd);

		ConfigEvents.SAVE.register(() -> {
			CustomLootBeamsConfig.onConfigurationChange();
			LootBeamRenderer.onConfigurationChange();
		});
		PresetEvents.APPLY_PRESET.register(_ -> {
			CustomLootBeamsConfig.onConfigurationChange();
			LootBeamRenderer.onConfigurationChange();
		});
		ResourceEvents.RESOURCE_RELOAD.register((synchronizer, rm, prep, apply) -> {
			TooltipManager.onResourcesReload();
			CustomLootBeamsConfig.onResourcesReload();
			CustomRarity.onResourcesReload(rm);
			PresetManager.onResourcesReload(rm);
			GlowEffectManager.onResourceManagerReload(rm, prep);
			//? if >= 1.21.11 {
			com.github.razorplay.lootbeams.shaders.LootBeamShaders.onCustomShadersReload(null, rm);
			//?}
		});
	}

	/**
	 * Helpers comunes que cada loader dispara desde su hook nativo.
	 */
	public static void onEntityLoad(ItemEntity itemEntity, Level world) {
		TooltipManager.onEntityLoad(itemEntity, world);
		RenderManager.onEntityLoad(itemEntity, world);
	}

	public static void onEntityUnload(ItemEntity itemEntity, Level world) {
		TooltipManager.onEntityUnload(itemEntity, world);
		RenderManager.onEntityUnload(itemEntity, world);
	}

	/**
	 * Abre la pantalla de presets. A partir de 26.2 la pantalla activa vive en {@code Gui}.
	 */
	public static void openPresetManagerScreen() {
		//? if >= 26.2 {
		Minecraft.getInstance().gui.setScreen(new LootBeamsPresetManagerScreen(Minecraft.getInstance().gui.screen()));
		//?} else {
		/*Minecraft.getInstance().setScreen(new LootBeamsPresetManagerScreen(Minecraft.getInstance().screen));
		*///?}
	}
}
