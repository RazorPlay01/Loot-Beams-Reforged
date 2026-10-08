package com.github.razorplay.lootbeams.platform.fabric.shaders;
//? fabric {

import com.github.razorplay.lootbeams.shaders.ShaderRegister;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.RegistrationContext;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;

import java.io.IOException;
import java.util.function.Consumer;

public class FabricShaderRegister implements ShaderRegister {
	private final RegistrationContext context;

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
}
//?}
