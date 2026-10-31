package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

/*import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.compat.modmenu.LootBeamsConfigScreen;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(ModTemplate.MOD_ID)
public class NeoforgeEntrypoint {

	public NeoforgeEntrypoint() {
		ModTemplate.onInitialize();
		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (container, parentScreen) -> LootBeamsConfigScreen.getConfigScreen(parentScreen)
		);
	}
}
*///?}
