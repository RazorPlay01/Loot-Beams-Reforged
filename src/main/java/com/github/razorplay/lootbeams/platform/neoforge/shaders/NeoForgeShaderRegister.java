package com.github.razorplay.lootbeams.platform.neoforge.shaders;
//? neoforge {

/*import com.github.razorplay.lootbeams.shaders.ShaderRegister;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.function.Consumer;

public class NeoForgeShaderRegister implements ShaderRegister {
	private final RegisterShadersEvent event;

	public NeoForgeShaderRegister(RegisterShadersEvent event) {
		this.event = event;
	}

	@Override
	public void register(Identifier id, VertexFormat format, Consumer<ShaderManager> onLoaded) {
		try {
			ShaderManager instance = new ShaderManager(event.getResourceProvider(), id, format);
			event.registerShader(instance, onLoaded);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
*///?}
