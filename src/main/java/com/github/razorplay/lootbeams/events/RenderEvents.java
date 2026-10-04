package com.github.razorplay.lootbeams.events;

import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface RenderEvents {
	SimpleEvent<HudRender> HUD = new SimpleEvent<>(listeners -> (drawContext, tickCounter) -> {
		for (HudRender listener : listeners) listener.onHud(drawContext, tickCounter);
	});

	SimpleEvent<BeforeParticles> BEFORE_PARTICLES = new SimpleEvent<>(listeners -> ctx -> {
		for (BeforeParticles listener : listeners) listener.beforeParticles(ctx);
	});

	SimpleEvent<AfterTranslucent> AFTER_TRANSLUCENT = new SimpleEvent<>(listeners -> ctx -> {
		for (AfterTranslucent listener : listeners) listener.afterTranslucent(ctx);
	});

	SimpleEvent<AfterWeather> AFTER_WEATHER = new SimpleEvent<>(listeners -> ctx -> {
		for (AfterWeather listener : listeners) listener.afterWeather(ctx);
	});

	SimpleEvent<BeforeEnd> BEFORE_END = new SimpleEvent<>(listeners -> ctx -> {
		for (BeforeEnd listener : listeners) listener.beforeEnd(ctx);
	});

	SimpleEvent<End> END = new SimpleEvent<>(listeners -> ctx -> {
		for (End listener : listeners) listener.onEnd(ctx);
	});

	interface AfterTranslucent { void afterTranslucent(WorldRendererContext ctx); }
	interface AfterWeather     { void afterWeather(WorldRendererContext ctx); }
	interface BeforeEnd        { void beforeEnd(WorldRendererContext ctx); }
	interface BeforeParticles  { void beforeParticles(WorldRendererContext ctx); }
	interface End              { void onEnd(WorldRendererContext ctx); }
	interface HudRender        { void onHud(GuiGraphics g, DeltaTracker t); }
}
