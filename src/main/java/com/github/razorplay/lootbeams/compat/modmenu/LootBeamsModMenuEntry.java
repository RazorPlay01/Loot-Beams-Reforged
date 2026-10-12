package com.github.razorplay.lootbeams.compat.modmenu;
//? fabric {
/*import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

public class LootBeamsModMenuEntry implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return FabricLoader.getInstance().isModLoaded("cloth-config") ? LootBeamsConfigScreen::getConfigScreen : null;
   }
}
*///?}
