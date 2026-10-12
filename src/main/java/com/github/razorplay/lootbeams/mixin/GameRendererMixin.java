package com.github.razorplay.lootbeams.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Da acceso a GameRenderer#getFov.
 * En 1.21.1 ese metodo es publico, pero a partir de 1.21.11 paso a ser privado,
 * por lo que se necesita un @Invoker para poder invocarlo desde el mod.
 */
@Mixin(GameRenderer.class)
public interface GameRendererMixin {
	// En 1.21.1 GameRenderer#getFov devuelve double; a partir de 1.21.11 devuelve float.
	//? if <= 1.21.1 {
	@Invoker("getFov")
	double lootbeams$getFov(Camera camera, float partialTick, boolean useFovSetting);
	//?} else {
	/*@Invoker("getFov")
	float lootbeams$getFov(Camera camera, float partialTick, boolean useFovSetting);
	*///?}
}
