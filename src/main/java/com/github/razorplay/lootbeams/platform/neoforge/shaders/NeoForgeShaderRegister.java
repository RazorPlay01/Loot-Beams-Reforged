package com.github.razorplay.lootbeams.platform.neoforge.shaders;
//? neoforge {

/*import com.github.razorplay.lootbeams.shaders.ShaderRegister;
//? if >= 26.3 {
import com.mojang.renderpearl.api.vertex.VertexFormat;
//?} else {
/^import com.mojang.blaze3d.vertex.VertexFormat;
^///?}
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
//? if <= 1.21.1 {
/^import net.neoforged.neoforge.client.event.RegisterShadersEvent;
^///?}

import java.io.IOException;
import java.util.function.Consumer;

public class NeoForgeShaderRegister implements ShaderRegister {
	// 1.21.1 registra los shaders con RegisterShadersEvent; 1.21.11 los compila de forma perezosa
	// dentro de su RenderPipeline (RegisterRenderPipelinesEvent), asi que aqui la clase es un no-op.
	//? if <= 1.21.1 {
	/^private final RegisterShadersEvent event;

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
	^///?} else {
	public NeoForgeShaderRegister() {
	}

	@Override
	public void register(Identifier id, VertexFormat format, Consumer<ShaderManager> onLoaded) {
	}
	//?}
}
*///?}
