package com.github.razorplay.lootbeams.shaders;

//? if >= 1.21.11 {

/*import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

/^*
 * Pipelines de render del mod para 1.21.11+.
 * <p>
 * En el motor reescrito ya no existen los ficheros {@code shaders/core/^.json}: cada shader vive
 * dentro de un {@link RenderPipeline} inmutable que se compila de forma perezosa la primera vez que
 * se dibuja, por lo que no hace falta registrarlos en ningun evento de carga.
 ^/
public final class LootBeamPipelines {
	private LootBeamPipelines() {
	}

	private static final String CUSTOM_SHADERS_DIRECTORY = "shaders/core/custom";
	private static final Map<String, RenderPipeline> CUSTOM_PIPELINES = new HashMap<>();

	/^* Capa base de la viga (mismo shader que "add", formato de particula). ^/
	public static final RenderPipeline ADD = core("lootbeams_add", DefaultVertexFormat.PARTICLE);

	/^* Overlay del glow del suelo coloreado. ^/
	public static final RenderPipeline GLOW_OVERLAY = core("lootbeams_glow_overlay", DefaultVertexFormat.PARTICLE);

	/^* Shader por defecto de las particulas de estela. ^/
	public static final RenderPipeline PARTICLE_OVERLAY = core("lootbeams_particle_overlay", DefaultVertexFormat.PARTICLE);

	/^* Droplight con degradado de dos colores. ^/
	public static final RenderPipeline DROPLIGHT = core("lootbeams_droplight", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);

	/^* Droplight animado con ruido simplex. ^/
	public static final RenderPipeline DROPLIGHT_ANIMATED = core("lootbeams_droplight_animated", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);

	/^* Glow radial con degradado. ^/
	public static final RenderPipeline DROPLIGHT_GLOW = core("lootbeams_droplight_glow", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);

	/^* Viga con el shader de rayos de vanilla (beam "glowing"). ^/
	public static final RenderPipeline LOOT_BEAM_LIGHTNING = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_lightning"))
			.withVertexShader("core/rendertype_lightning")
			.withFragmentShader("core/rendertype_lightning")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(false)
			.withDepthWrite(false)
			.build();

	/^* Viga con textura (shader del beacon beam de vanilla). ^/
	public static final RenderPipeline LOOT_BEAM = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam"))
			.withVertexShader("core/rendertype_beacon_beam")
			.withFragmentShader("core/rendertype_beacon_beam")
			.withSampler("Sampler0")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withCull(false)
			.withDepthWrite(false)
			.build();

	/^* Nucleo solido de la viga, dibujado sobre el render target del clima. ^/
	public static final RenderPipeline LOOT_BEAM_CENTER = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_center"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.withBlend(BlendFunction.LIGHTNING)
			.build();

	private static RenderPipeline core(String name, VertexFormat format) {
		return RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
				.withLocation(ModTemplate.id("pipeline/" + name))
				.withVertexShader(ModTemplate.id("core/" + name))
				.withFragmentShader(ModTemplate.id("core/" + name))
				.withSampler("Sampler0")
				.withVertexFormat(format, VertexFormat.Mode.QUADS)
				.withBlend(BlendFunction.TRANSLUCENT)
				.withCull(false)
				.withDepthWrite(false)
				.build();
	}

	public static RenderPipeline getShader(LootBeamShaders.Shader shader) {
		return switch (shader) {
			case ADD -> ADD;
			case GLOW_OVERLAY -> GLOW_OVERLAY;
			case PARTICLE_OVERLAY -> PARTICLE_OVERLAY;
		};
	}

	public static RenderPipeline getShader(LootBeamShaders.DroplightShader shader) {
		return switch (shader) {
			case DROPLIGHT -> DROPLIGHT;
			case DROPLIGHT_ANIMATED -> DROPLIGHT_ANIMATED;
			case DROPLIGHT_GLOW -> DROPLIGHT_GLOW;
		};
	}

	public static RenderPipeline getShader(LootBeamShaders.CustomShader shader) {
		return CUSTOM_PIPELINES.get(shader.name());
	}

	public static String getFormattedName(String path) {
		String result = path;
		if (result.contains(".")) {
			result = result.substring(path.lastIndexOf(47) + 1, path.lastIndexOf(46));
		}

		return result.toUpperCase();
	}

	/^*
	 * Descubre los shaders personalizados presentes en {@code assets/<ns>/shaders/core/custom}
	 * y construye un pipeline por cada par {@code .vsh}/{@code .fsh}.
	 ^/
	public static void reloadCustomShaders(ResourceManager resourceManager) {
		CUSTOM_PIPELINES.clear();
		if (resourceManager == null) {
			return;
		}

		Map<ResourceLocation, Resource> resources = resourceManager.listResources(
				CUSTOM_SHADERS_DIRECTORY, identifier -> identifier.getPath().endsWith(".vsh")
		);

		for (ResourceLocation shaderId : resources.keySet()) {
			String path = shaderId.getPath();
			// "shaders/core/custom/glow_vortex.vsh" -> "core/custom/glow_vortex"
			String shaderPath = path.substring("shaders/".length(), path.length() - ".vsh".length());
			String name = getFormattedName(path);
			CUSTOM_PIPELINES.put(name, RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
					.withVertexShader(ModTemplate.id(shaderPath))
					.withFragmentShader(ModTemplate.id(shaderPath))
					.withSampler("Sampler0")
					.withVertexFormat(CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER, VertexFormat.Mode.QUADS)
					.withBlend(BlendFunction.TRANSLUCENT)
					.withCull(false)
					.withDepthWrite(false)
					.build());
		}
	}
}

*///?}
