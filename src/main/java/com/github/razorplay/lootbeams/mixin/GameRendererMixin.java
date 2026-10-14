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
	// 26.1.2 elimino el metodo por completo (el FOV efectivo pasa a CameraRenderState#hudFov),
	// asi que a partir de ahi la interfaz se queda sin miembros.
	//? if <= 1.21.1 {
	/*@Invoker("getFov")
	double lootbeams$getFov(Camera camera, float partialTick, boolean useFovSetting);
	*///?} elif <= 1.21.11 {
	/*@Invoker("getFov")
	float lootbeams$getFov(Camera camera, float partialTick, boolean useFovSetting);
	*///?}
}
