package com.github.razorplay.lootbeams.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.concurrent.Executor;

public interface ResourceEvents {
   Event<ResourceReload> RESOURCE_RELOAD = EventFactory.createArrayBacked(
      ResourceReload.class, listeners -> (synchronizer, resourceManager, prepareExecutor, applyExecutor) -> {
         for (ResourceReload event : listeners) {
            event.onResourceReload(synchronizer, resourceManager, prepareExecutor, applyExecutor);
         }
      }
   );

   interface ResourceReload {
      void onResourceReload(PreparableReloadListener.PreparationBarrier var1, ResourceManager var2, Executor var3, Executor var4);
   }
}
