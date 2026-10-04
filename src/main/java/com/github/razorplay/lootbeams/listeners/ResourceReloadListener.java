package com.github.razorplay.lootbeams.listeners;

import com.github.razorplay.lootbeams.events.ResourceEvents;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadListener implements PreparableReloadListener {
	public CompletableFuture<Void> reload(
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

	public void reload(PreparationBarrier synchronizer, ResourceManager manager, Executor prepareExecutor, Executor applyExecutor) {
		ResourceEvents.RESOURCE_RELOAD.invoker().onResourceReload(synchronizer, manager, prepareExecutor, applyExecutor);
	}
}
