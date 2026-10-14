package com.github.razorplay.lootbeams.shaders;

//? if >= 26.3 {
import com.mojang.renderpearl.api.vertex.VertexFormat;
//?} else {
/*import com.mojang.blaze3d.vertex.VertexFormat;
*///?}
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

/**
 * Abstrae el registro de un shader, independientemente del loader.
 */
public interface ShaderRegister {
	void register(Identifier id, VertexFormat format, Consumer<ShaderManager> onLoaded);
}
