package com.github.razorplay.lootbeams.platform.neoforge.shaders;
//? neoforge {

import com.github.razorplay.lootbeams.shaders.ShaderRegister;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
//? if <= 1.21.1 {
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
//?}

import java.io.IOException;
import java.util.function.Consumer;

public class NeoForgeShaderRegister implements ShaderRegister {
	// 1.21.1 registra los shaders con RegisterShadersEvent; 1.21.11 los compila de forma perezosa
	// dentro de su RenderPipeline (RegisterRenderPipelinesEvent), asi que aqui la clase es un no-op.
	//? if <= 1.21.1 {
	private final RegisterShadersEvent event;

	public NeoForgeShaderRegister(RegisterShadersEvent event) {
		this.event = event;
	}

	@Override
	public void register(ResourceLocation id, VertexFormat format, Consumer<ShaderInstance> onLoaded) {
		try {
			ShaderInstance instance = new ShaderInstance(event.getResourceProvider(), id, format);
			event.registerShader(instance, onLoaded);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	//?} else {
	/*public NeoForgeShaderRegister() {
	}

	@Override
	public void register(ResourceLocation id, VertexFormat format, Consumer<ShaderInstance> onLoaded) {
	}
	*///?}
}
//?}
