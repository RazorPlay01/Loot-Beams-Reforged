package com.github.razorplay.lootbeams.shaders;

//? if >= 1.21.11 {

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
//? if >= 26.3 {
import com.mojang.renderpearl.api.pipeline.BlendFunction;
//?} else {
/*import com.mojang.blaze3d.pipeline.BlendFunction;
*///?}
//? if >= 26.3 {
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
//?} elif >= 26.2 {
/*import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
*///?} elif >= 26.1.2 {
/*import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?} else {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?}
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//? if >= 26.3 {
import com.mojang.renderpearl.api.vertex.VertexFormat;
//?} else {
/*import com.mojang.blaze3d.vertex.VertexFormat;
*///?}
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Pipelines de render del mod para 1.21.11+.
 * <p>
 * En el motor reescrito ya no existen los ficheros {@code shaders/core/*.json}: cada shader vive
 * dentro de un {@link RenderPipeline} inmutable que se compila de forma perezosa la primera vez que
 * se dibuja, por lo que no hace falta registrarlos en ningun evento de carga.
 */
public final class LootBeamPipelines {
	private LootBeamPipelines() {
	}

	private static final String CUSTOM_SHADERS_DIRECTORY = "shaders/core/custom";
	private static final Map<String, RenderPipeline> CUSTOM_PIPELINES = new HashMap<>();

	// 26.3 renombro BindGroupLayouts.MATRICES_PROJECTION a PROJECTION.
	//? if >= 26.3 {
	private static final BindGroupLayout MATRICES_PROJECTION_LAYOUT = BindGroupLayouts.PROJECTION;
	//?} elif >= 26.2 {
	/*private static final BindGroupLayout MATRICES_PROJECTION_LAYOUT = BindGroupLayouts.MATRICES_PROJECTION;
	*///?}

	/** Capa base de la viga (mismo shader que "add", formato de particula). */
	public static final RenderPipeline ADD = core("lootbeams_add", DefaultVertexFormat.PARTICLE);

	/** Overlay del glow del suelo coloreado. */
	public static final RenderPipeline GLOW_OVERLAY = core("lootbeams_glow_overlay", DefaultVertexFormat.PARTICLE);

	/** Shader por defecto de las particulas de estela. */
	public static final RenderPipeline PARTICLE_OVERLAY = core("lootbeams_particle_overlay", DefaultVertexFormat.PARTICLE);

	/** Droplight con degradado de dos colores. */
	public static final RenderPipeline DROPLIGHT = core("lootbeams_droplight", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);

	/** Droplight animado con ruido simplex. */
	public static final RenderPipeline DROPLIGHT_ANIMATED = core("lootbeams_droplight_animated", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);

	/** Glow radial con degradado. */
	public static final RenderPipeline DROPLIGHT_GLOW = core("lootbeams_droplight_glow", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);

	/** Viga con el shader de rayos de vanilla (beam "glowing"). */
	//? if >= 26.2 {
	public static final RenderPipeline LOOT_BEAM_LIGHTNING = RenderPipeline.builder()
			.withLocation(ModTemplate.id("pipeline/loot_beam_lightning"))
			.withVertexShader("core/rendertype_lightning")
			.withFragmentShader("core/rendertype_lightning")
			.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT)
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withVertexBinding(0, DefaultVertexFormat.BLOCK)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(false)
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.build();
	//?} else {
	/*public static final RenderPipeline LOOT_BEAM_LIGHTNING = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_lightning"))
			.withVertexShader("core/rendertype_lightning")
			.withFragmentShader("core/rendertype_lightning")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			//?} else {
			/^.withBlend(BlendFunction.TRANSLUCENT)
			^///?}
			.withCull(false)
			//? if >= 26.1.2 {
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			//?} else {
			/^.withDepthWrite(false)
			^///?}
			.build();
	*///?}

	/** Viga con textura (shader del beacon beam de vanilla). */
	//? if >= 26.2 {
	public static final RenderPipeline LOOT_BEAM = RenderPipeline.builder()
			.withLocation(ModTemplate.id("pipeline/loot_beam"))
			.withVertexShader("core/rendertype_beacon_beam")
			.withFragmentShader("core/rendertype_beacon_beam")
			.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT)
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withVertexBinding(0, DefaultVertexFormat.BLOCK)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(false)
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			.build();
	//?} else {
	/*public static final RenderPipeline LOOT_BEAM = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam"))
			.withVertexShader("core/rendertype_beacon_beam")
			.withFragmentShader("core/rendertype_beacon_beam")
			.withSampler("Sampler0")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			//?} else {
			/^.withBlend(BlendFunction.TRANSLUCENT)
			^///?}
			.withCull(false)
			//? if >= 26.1.2 {
			.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
			//?} else {
			/^.withDepthWrite(false)
			^///?}
			.build();
	*///?}

	/** Nucleo solido de la viga, dibujado sobre el render target del clima. */
	//? if >= 26.2 {
	public static final RenderPipeline LOOT_BEAM_CENTER = RenderPipeline.builder()
			.withLocation(ModTemplate.id("pipeline/loot_beam_center"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT)
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.build();
	//?} else {
	/*public static final RenderPipeline LOOT_BEAM_CENTER = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_center"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			//?} else {
			/^.withBlend(BlendFunction.LIGHTNING)
			^///?}
			.build();
	*///?}

	/**
	 * Samplers que declara cada shader del mod, necesario para elegir el bind group correcto a
	 * partir de 26.2 (el snippet "matrices+fog" ya no aporta los samplers).
	 */
	private static boolean coreUsesSampler2(String name) {
		return name.endsWith("_add") || name.endsWith("_particle_overlay");
	}

	private static boolean coreUsesFog(String name) {
		return name.endsWith("_add") || name.endsWith("_glow_overlay") || name.endsWith("_particle_overlay");
	}

	private static RenderPipeline core(String name, VertexFormat format) {
		//? if >= 26.2 {
		RenderPipeline.Builder builder = RenderPipeline.builder()
				.withLocation(ModTemplate.id("pipeline/" + name))
				.withVertexShader(ModTemplate.id("core/" + name))
				.withFragmentShader(ModTemplate.id("core/" + name))
				.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT);
		if (coreUsesFog(name)) {
			builder = builder.withBindGroupLayout(BindGroupLayouts.FOG);
		}
		builder = builder.withBindGroupLayout(
				coreUsesSampler2(name) ? BindGroupLayouts.SAMPLER0_SAMPLER2 : BindGroupLayouts.SAMPLER0
		);
		return builder
				.withVertexBinding(0, format)
				.withPrimitiveTopology(PrimitiveTopology.QUADS)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.withCull(false)
				.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
				.build();
		//?} else {
		/*return RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
				.withLocation(ModTemplate.id("pipeline/" + name))
				.withVertexShader(ModTemplate.id("core/" + name))
				.withFragmentShader(ModTemplate.id("core/" + name))
				.withSampler("Sampler0")
				.withVertexFormat(format, VertexFormat.Mode.QUADS)
				//? if >= 26.1.2 {
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				//?} else {
				/^.withBlend(BlendFunction.TRANSLUCENT)
				^///?}
				.withCull(false)
				//? if >= 26.1.2 {
				.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
				//?} else {
				/^.withDepthWrite(false)
				^///?}
				.build();
		*///?}
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

	/**
	 * Descubre los shaders personalizados presentes en {@code assets/<ns>/shaders/core/custom}
	 * y construye un pipeline por cada par {@code .vsh}/{@code .fsh}.
	 */
	public static void reloadCustomShaders(ResourceManager resourceManager) {
		CUSTOM_PIPELINES.clear();
		if (resourceManager == null) {
			return;
		}

		Map<Identifier, Resource> resources = resourceManager.listResources(
				CUSTOM_SHADERS_DIRECTORY, identifier -> identifier.getPath().endsWith(".vsh")
		);

		for (Identifier shaderId : resources.keySet()) {
			String path = shaderId.getPath();
			// "shaders/core/custom/glow_vortex.vsh" -> "core/custom/glow_vortex"
			String shaderPath = path.substring("shaders/".length(), path.length() - ".vsh".length());
			String name = getFormattedName(path);
			//? if >= 26.2 {
			CUSTOM_PIPELINES.put(name, RenderPipeline.builder()
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
					.withVertexShader(ModTemplate.id(shaderPath))
					.withFragmentShader(ModTemplate.id(shaderPath))
					.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT)
					.withBindGroupLayout(BindGroupLayouts.FOG)
					.withBindGroupLayout(BindGroupLayouts.SAMPLER0_SAMPLER1_SAMPLER2)
					.withVertexBinding(0, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER)
					.withPrimitiveTopology(PrimitiveTopology.QUADS)
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withCull(false)
					.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
					.build());
			//?} else {
			/*CUSTOM_PIPELINES.put(name, RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
					.withVertexShader(ModTemplate.id(shaderPath))
					.withFragmentShader(ModTemplate.id(shaderPath))
					.withSampler("Sampler0")
					.withVertexFormat(CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER, VertexFormat.Mode.QUADS)
					//? if >= 26.1.2 {
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					//?} else {
					/^.withBlend(BlendFunction.TRANSLUCENT)
					^///?}
					.withCull(false)
					//? if >= 26.1.2 {
					.withDepthStencilState(new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false))
					//?} else {
					/^.withDepthWrite(false)
					^///?}
					.build());
			*///?}
		}
	}
}

//?}
