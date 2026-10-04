package com.github.razorplay.lootbeams.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PresetEvents {
   Event<ApplyPreset> APPLY_PRESET = EventFactory.createArrayBacked(ApplyPreset.class, listeners -> presetName -> {
      for (ApplyPreset event : listeners) {
         event.onApplyPreset(presetName);
      }
   });

   interface ApplyPreset {
      void onApplyPreset(String var1);
   }
}
