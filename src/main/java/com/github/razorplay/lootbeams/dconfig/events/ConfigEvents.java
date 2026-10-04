package com.github.razorplay.lootbeams.dconfig.events;

public interface ConfigEvents {
   EventFactory.Event<ConfigSave> SAVE = EventFactory.createArrayBacked(ConfigSave.class, listeners -> () -> {
      for (ConfigSave event : listeners) {
         event.onConfigSave();
      }
   });
   EventFactory.Event<ConfigChange> CHANGE = EventFactory.createArrayBacked(ConfigChange.class, listeners -> () -> {
      for (ConfigChange event : listeners) {
         event.onConfigChange();
      }
   });

   interface ConfigChange {
      void onConfigChange();
   }

   interface ConfigSave {
      void onConfigSave();
   }
}
