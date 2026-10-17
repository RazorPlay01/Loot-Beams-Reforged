package com.github.razorplay.lootbeams.platform.fabric.shaders;
//? fabric {

import com.github.razorplay.lootbeams.shaders.ShaderRegister;
//? if >= 26.3 {
import com.mojang.renderpearl.api.vertex.VertexFormat;
//?} else {
/*import com.mojang.blaze3d.vertex.VertexFormat;
*///?}
//? if <= 1.21.1 {
/*import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.RegistrationContext;
*///?}
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.util.function.Consumer;

public class FabricShaderRegister implements ShaderRegister {
	//? if <= 1.21.1 {
	/*private final RegistrationContext context;

	public FabricShaderRegister(RegistrationContext context) {
		this.context = context;
	}

	@Override
	public void register(Identifier id, VertexFormat format, Consumer<ShaderManager> onLoaded) {
		try {
			context.register(id, format, onLoaded);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	*///?} else {
	public FabricShaderRegister() {
	}

	@Override
	public void register(Identifier id, VertexFormat format, Consumer<ShaderManager> onLoaded) {
		// 1.21.11: los shaders se compilan de forma perezosa como parte de un RenderPipeline,
		// por lo que Fabric no expone (ni necesita) un registro explicito de shaders.
	}
	//?}
}
//?}
