package com.github.razorplay.lootbeams.shaders;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

/**
 * Abstrae el registro de un shader, independientemente del loader.
 */
public interface ShaderRegister {
	void register(ResourceLocation id, VertexFormat format, Consumer<ShaderInstance> onLoaded);
}
