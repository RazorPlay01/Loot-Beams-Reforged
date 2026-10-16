package com.github.razorplay.lootbeams.shaders;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.StringHelper;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.vertex.VertexFormat;
*///?} else {
import com.mojang.blaze3d.vertex.VertexFormat;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBeamShaders {
	private static final Map<Shader, ShaderManager> SHADERS_MAP = new EnumMap<>(Shader.class);
	private static final Map<DroplightShader, ShaderManager> DROPLIGHT_SHADERS_MAP = new EnumMap<>(DroplightShader.class);
	private static final Map<String, ShaderManager> CUSTOM_SHADERS_MAP = new HashMap<>();
	private static final String CUSTOM_SHADERS_DIRECTORY = "shaders/core/custom";
	private static final String CUSTOM_SHADERS_EXTENSION = ".json";

	// ---- Registro (agnóstico del loader) ----

	public static void registerShader(ShaderRegister registrar, Shader shader, String name, VertexFormat format) {
		registrar.register(ModTemplate.id(name), format,
				shaderProgram -> SHADERS_MAP.put(shader, shaderProgram));
	}

	public static void registerDroplightShader(ShaderRegister registrar, DroplightShader shader, String name, VertexFormat format) {
		registrar.register(ModTemplate.id(name), format,
				shaderProgram -> DROPLIGHT_SHADERS_MAP.put(shader, shaderProgram));
	}

	public static void registerCustomShader(ShaderRegister registrar, CustomShader shader, String name, VertexFormat format) {
		registrar.register(ModTemplate.id(name), format,
				shaderProgram -> CUSTOM_SHADERS_MAP.put(shader.name(), shaderProgram));
	}

	// ---- Getters (idénticos) ----

	public static ShaderManager getShader(Shader shader) {
		return SHADERS_MAP.get(shader);
	}

	public static ShaderManager getShader(DroplightShader shader) {
		return DROPLIGHT_SHADERS_MAP.get(shader);
	}

	public static ShaderManager getShader(CustomShader shader) {
		return CUSTOM_SHADERS_MAP.get(shader.name());
	}

	public static float getAverageColor(Shader shader) {
		return switch (shader) {
			case ADD -> 1.0F;
			case GLOW_OVERLAY -> 0.5F;
			case PARTICLE_OVERLAY -> 0.5F;
			default -> 0.5F;
		};
	}

	// ---- Registro de grupos ----

	public static void registerCoreShaders(ShaderRegister registrar) {
		for (Shader shader : Shader.values()) {
			String registryShaderName = "lootbeams_" + shader.name().toLowerCase();
			registerShader(registrar, shader, registryShaderName, DefaultVertexFormat.PARTICLE);
		}
	}

	private static void registerDroplightCoreShader(ShaderRegister registrar, DroplightShader shader, VertexFormat vertexFormat) {
		String registryShaderName = "lootbeams_" + shader.name().toLowerCase();
		registerDroplightShader(registrar, shader, registryShaderName, vertexFormat);
	}

	public static void registerDroplightCoreShaders(ShaderRegister registrar) {
		registerDroplightCoreShader(registrar, DroplightShader.DROPLIGHT, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);
		registerDroplightCoreShader(registrar, DroplightShader.DROPLIGHT_ANIMATED, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);
		registerDroplightCoreShader(registrar, DroplightShader.DROPLIGHT_GLOW, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);
	}

	public static void registerCustomCoreShaders(ShaderRegister registrar) {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager != null) {
			onCustomShadersReload(registrar, resourceManager);
		}
	}

	public static void onCustomShadersReload(ShaderRegister registrar, ResourceManager resourceManager) {
		CustomShader.clearDynamicValues();
		//? if <= 1.21.1 {
		/*CUSTOM_SHADERS_MAP.clear();
		if (resourceManager == null) {
			return;
		}

		Map<Identifier, Resource> resourceShaderMap = resourceManager.listResources(
				CUSTOM_SHADERS_DIRECTORY, identifier -> identifier.getPath().endsWith(CUSTOM_SHADERS_EXTENSION)
		);

		for (Identifier shaderId : resourceShaderMap.keySet()) {
			CustomShader newShader = CustomShader.addValue(StringHelper.getFormattedName(shaderId.getPath()));
			String registryShaderName = newShader.name().toLowerCase();
			if (registrar != null) {
				registerCustomShader(registrar, newShader, "custom/" + registryShaderName,
						CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);
			}
		}
		*///?} else {
		// 1.21.11: ya no hay ficheros .json de shader; cada shader personalizado se convierte en
		// un RenderPipeline a partir de su pareja .vsh/.fsh.
		if (resourceManager == null) {
			LootBeamPipelines.reloadCustomShaders(null);
			return;
		}

		Map<Identifier, Resource> resourceShaderMap = resourceManager.listResources(
				CUSTOM_SHADERS_DIRECTORY, identifier -> identifier.getPath().endsWith(".vsh")
		);

		for (Identifier shaderId : resourceShaderMap.keySet()) {
			CustomShader.addValue(StringHelper.getFormattedName(shaderId.getPath()));
		}

		LootBeamPipelines.reloadCustomShaders(resourceManager);
		//?}
	}

	// ---- Clases internas (idénticas en todos los loaders) ----

	public static class CustomShader {
		public static final CustomShader NONE = new CustomShader("NONE");
		private final String name;
		private static final List<CustomShader> dynamicValues = new ArrayList<>();
		private static final List<CustomShader> staticValues = new ArrayList<>();

		private CustomShader(String name) {
			this.name = name;
		}

		public String name() {
			return this.name;
		}

		public static CustomShader addValue(String name) {
			synchronized (dynamicValues) {
				for (CustomShader value : values()) {
					if (value.name.equals(name)) return value;
				}
				CustomShader newValue = new CustomShader(name);
				dynamicValues.add(newValue);
				return newValue;
			}
		}

		public static void clearDynamicValues() {
			synchronized (dynamicValues) {
				dynamicValues.clear();
			}
		}

		public static List<CustomShader> values() {
			synchronized (dynamicValues) {
				List<CustomShader> allValues = new ArrayList<>(staticValues);
				allValues.addAll(dynamicValues);
				return allValues;
			}
		}

		public static CustomShader valueOf(String name) {
			for (CustomShader shader : staticValues) {
				if (shader.name.equals(name)) return shader;
			}
			synchronized (dynamicValues) {
				for (CustomShader shader : dynamicValues) {
					if (shader.name.equals(name)) return shader;
				}
			}
			return addValue(name);
		}

		@Override
		public String toString() {
			return this.name;
		}

		static {
			staticValues.add(NONE);
		}
	}

	public enum DroplightShader {
		DROPLIGHT,
		DROPLIGHT_ANIMATED,
		DROPLIGHT_GLOW
	}

	public enum Shader {
		ADD,
		GLOW_OVERLAY,
		PARTICLE_OVERLAY
	}
}
