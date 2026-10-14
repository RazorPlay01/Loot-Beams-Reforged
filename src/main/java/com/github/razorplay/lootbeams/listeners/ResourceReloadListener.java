package com.github.razorplay.lootbeams.listeners;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.events.ResourceEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
//? if >= 1.21.11 {
import net.minecraft.util.profiling.Profiler;
//?}

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadListener implements PreparableReloadListener /*? fabric { */, net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener/*?} */ {
	//? if <= 1.21.1 {
	/*public CompletableFuture<Void> reload(
			PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor
	) {
		return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
			applyProfiler.startTick();
			applyProfiler.push("listener");
			this.reload(synchronizer, manager, prepareExecutor, applyExecutor);
			applyProfiler.pop();
			applyProfiler.endTick();
		}, applyExecutor);
	}
	*///?} else {
	public CompletableFuture<Void> reload(
			SharedState currentReload, Executor taskExecutor, PreparationBarrier synchronizer, Executor reloadExecutor
	) {
		return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() -> {
			ProfilerFiller applyProfiler = Profiler.get();
			applyProfiler.startTick();
			applyProfiler.push("listener");
			this.reload(synchronizer, currentReload.resourceManager(), taskExecutor, reloadExecutor);
			applyProfiler.pop();
			applyProfiler.endTick();
		}, reloadExecutor);
	}
	//?}

	public void reload(PreparationBarrier synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		ResourceEvents.RESOURCE_RELOAD.invoker().onResourceReload(synchronizer, manager, prepareExecutor, applyExecutor);
	}

	/*? fabric { */
	public Identifier getFabricId() {
		return ModTemplate.id("reload_resources");
	}

	public Collection<Identifier> getFabricDependencies() {
		return Collections.emptyList();
	}
	/*?} */
}
