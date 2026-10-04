package com.github.razorplay.lootbeams.events;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import java.util.concurrent.Executor;

public interface ResourceEvents {
	SimpleEvent<ResourceReload> RESOURCE_RELOAD = new SimpleEvent<>(listeners ->
			(barrier, rm, prepareExec, applyExec) -> {
				for (ResourceReload l : listeners) {
					l.onResourceReload(barrier, rm, prepareExec, applyExec);
				}
			}
	);

	interface ResourceReload {
		void onResourceReload(PreparableReloadListener.PreparationBarrier barrier,
							  ResourceManager rm, Executor prepareExecutor, Executor applyExecutor);
	}
}
