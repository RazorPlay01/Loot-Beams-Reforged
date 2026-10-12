package com.github.razorplay.lootbeams.mixin;

import org.spongepowered.asm.mixin.Mixin;

//? <= 1.21.1 {
import net.minecraft.client.renderer.RenderType;
//?} else {
/*import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.spongepowered.asm.mixin.gen.Invoker;
*///?}

/**
 * Da acceso a RenderType#create.
 * <p>
 * En 1.21.1 el metodo equivalente ({@code RenderType.create(String, VertexFormat, Mode, int, boolean,
 * boolean, CompositeState)}) es publico, pero en Fabric 1.21.11
 * {@code RenderType.create(String, RenderSetup)} paso a ser package-private; NeoForge lo parchea a
 * publico, por lo que este {@code @Invoker} es la unica forma portable de seguir creando tipos de
 * render propios en ambos loaders.
 */
@Mixin(RenderType.class)
public interface RenderTypeMixin {
	//? if <= 1.21.1 {
	//?} else {
	/*@Invoker("create")
	static RenderType lootbeams$create(String name, RenderSetup setup) {
		throw new AssertionError();
	}
	*///?}
}
