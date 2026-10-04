package com.github.razorplay.lootbeams.events;

import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

public interface RenderEvents {
   Event<HudRender> HUD = EventFactory.createArrayBacked(HudRender.class, listeners -> (drawContext, tickCounter) -> {
      for (HudRender listener : listeners) {
         listener.onHud(drawContext, tickCounter);
      }
   });
   Event<BeforeParticles> BEFORE_PARTICLES = EventFactory.createArrayBacked(
      BeforeParticles.class, listeners -> worldRendererContext -> {
         for (BeforeParticles listener : listeners) {
            listener.beforeParticles(worldRendererContext);
         }
      }
   );
   Event<AfterTranslucent> AFTER_TRANSLUCENT = EventFactory.createArrayBacked(
      AfterTranslucent.class, listeners -> worldRendererContext -> {
         for (AfterTranslucent listener : listeners) {
            listener.afterTranslucent(worldRendererContext);
         }
      }
   );
   Event<AfterWeather> AFTER_WEATHER = EventFactory.createArrayBacked(AfterWeather.class, listeners -> worldRendererContext -> {
      for (AfterWeather listener : listeners) {
         listener.afterWeather(worldRendererContext);
      }
   });
   Event<BeforeEnd> BEFORE_END = EventFactory.createArrayBacked(BeforeEnd.class, listeners -> worldRendererContext -> {
      for (BeforeEnd listener : listeners) {
         listener.beforeEnd(worldRendererContext);
      }
   });
   Event<End> END = EventFactory.createArrayBacked(End.class, listeners -> worldRendererContext -> {
      for (End listener : listeners) {
         listener.onEnd(worldRendererContext);
      }
   });

   interface AfterTranslucent {
      void afterTranslucent(WorldRendererContext var1);
   }

   interface AfterWeather {
      void afterWeather(WorldRendererContext var1);
   }

   interface BeforeEnd {
      void beforeEnd(WorldRendererContext var1);
   }

   interface BeforeParticles {
      void beforeParticles(WorldRendererContext var1);
   }

   interface End {
      void onEnd(WorldRendererContext var1);
   }

   interface HudRender {
      void onHud(GuiGraphics var1, DeltaTracker var2);
   }
}
