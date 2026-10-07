package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

/*import com.github.razorplay.lootbeams.ModTemplate;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeKeyMappings {
    private NeoForgeKeyMappings() {}

    public static KeyMapping OPEN_PRESET_SCREEN;

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        OPEN_PRESET_SCREEN = new KeyMapping(
                "lootbeams.keybindings.savePreset",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT, // 325 = LEFT_ALT en el original
                "lootbeams.title");
        event.register(OPEN_PRESET_SCREEN);
    }
}
*///?}
