package com.github.razorplay.lootbeams.listeners;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.events.ResourceEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceReloadListener implements IdentifiableResourceReloadListener, PreparableReloadListener {
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

   public ResourceLocation getFabricId() {
      return ModTemplate.id("reload_resources");
   }

   public Collection<ResourceLocation> getFabricDependencies() {
      return Collections.emptyList();
   }
}
