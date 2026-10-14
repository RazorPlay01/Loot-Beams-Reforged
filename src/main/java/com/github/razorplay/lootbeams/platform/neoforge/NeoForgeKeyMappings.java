package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

/*import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.VersionCompatHelper;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = ModTemplate.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeKeyMappings {
    private NeoForgeKeyMappings() {}

    public static KeyMapping OPEN_PRESET_SCREEN;

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        OPEN_PRESET_SCREEN = VersionCompatHelper.registerKeyMapping(
                "lootbeams.keybindings.savePreset",
                VersionCompatHelper.KEY_TYPE,
                325, // LEFT_ALT (constante literal: en 26.3 org.lwjgl.glfw no esta en el classpath de compilacion)
                "lootbeams.title",
                "lootbeams");
        event.register(OPEN_PRESET_SCREEN);
    }
}
*///?}
