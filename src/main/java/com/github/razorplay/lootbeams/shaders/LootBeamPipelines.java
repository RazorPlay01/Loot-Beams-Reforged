package com.github.razorplay.lootbeams.shaders;

//? if >= 1.21.11 {

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.StringHelper;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.pipeline.BlendFunction;
*///?} else {
import com.mojang.blaze3d.pipeline.BlendFunction;
//?}
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompareOp;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
*///?} elif >= 26.2 {
/*import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
*///?} elif >= 26.1.2 {
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.pipeline.RenderPipeline;
//?} else {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
*///?}
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.vertex.VertexFormat;
*///?} else {
import com.mojang.blaze3d.vertex.VertexFormat;
//?}
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

	// 26.1.2 es la ultima version que declara los uniformes con {@code withUniform} en el pipeline
	// (26.2+ lo hace con bind group layouts). Vanilla ya expone el snippet del bloque Globals, que es
	// el que aporta {@code GameTime} a los shaders que lo leen, asi que se reutiliza tal cual.
	//? if >= 26.1.2 && < 26.2 {
	private static final RenderPipeline.Snippet GLOBALS_SNIPPET = RenderPipelines.GLOBALS_SNIPPET;
	//?}

	// 26.1.2 es la ultima version con la convencion CLASICA de profundidad: vanilla declara sus
	// pipelines con {@code DepthStencilState.DEFAULT} (menos = mas cerca). A partir de 26.2 el motor
	// invierte la comparacion y el mod usa GEQUAL sin escritura. Con el estado de 26.2 en 26.1.2 la
	// geometria solo pasa el test donde hay algo DELANTE, que es exactamente el sintoma de los
	// efectos que "no se ven" salvo mirando a traves de otra entidad.
	//? if >= 26.1.2 {
	private static DepthStencilState depthState() {
		//? if >= 26.2 {
		/*return new DepthStencilState(CompareOp.GREATER_THAN_OR_EQUAL, false);
		*///?} else {
		return DepthStencilState.DEFAULT;
		//?}
	}
	//?}

	// 26.3 renombro BindGroupLayouts.MATRICES_PROJECTION a PROJECTION.
	//? if >= 26.3 {
	/*private static final BindGroupLayout MATRICES_PROJECTION_LAYOUT = BindGroupLayouts.PROJECTION;
	*///?} elif >= 26.2 {
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
	/*public static final RenderPipeline LOOT_BEAM_LIGHTNING = withMatrixLayouts(RenderPipeline.builder())
			.withLocation(ModTemplate.id("pipeline/loot_beam_lightning"))
			.withVertexShader("core/rendertype_lightning")
			.withFragmentShader("core/rendertype_lightning")
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withVertexBinding(0, DefaultVertexFormat.BLOCK)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(false)
			.withDepthStencilState(depthState())
			.build();
	*///?} else {
	public static final RenderPipeline LOOT_BEAM_LIGHTNING = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_lightning"))
			.withVertexShader("core/rendertype_lightning")
			.withFragmentShader("core/rendertype_lightning")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			//?} else {
			/*.withBlend(BlendFunction.TRANSLUCENT)
			*///?}
			.withCull(false)
			//? if >= 26.1.2 {
			.withDepthStencilState(depthState())
			//?} else {
			/*.withDepthWrite(false)
			*///?}
			.build();
	//?}

	/** Viga con textura (shader del beacon beam de vanilla). */
	//? if >= 26.2 {
	/*public static final RenderPipeline LOOT_BEAM = withMatrixLayouts(RenderPipeline.builder())
			.withLocation(ModTemplate.id("pipeline/loot_beam"))
			.withVertexShader("core/rendertype_beacon_beam")
			.withFragmentShader("core/rendertype_beacon_beam")
			.withBindGroupLayout(BindGroupLayouts.FOG)
			.withBindGroupLayout(BindGroupLayouts.SAMPLER0)
			.withVertexBinding(0, DefaultVertexFormat.BLOCK)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			.withCull(false)
			.withDepthStencilState(depthState())
			.build();
	*///?} else {
	public static final RenderPipeline LOOT_BEAM = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam"))
			.withVertexShader("core/rendertype_beacon_beam")
			.withFragmentShader("core/rendertype_beacon_beam")
			.withSampler("Sampler0")
			.withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
			//?} else {
			/*.withBlend(BlendFunction.TRANSLUCENT)
			*///?}
			.withCull(false)
			//? if >= 26.1.2 {
			.withDepthStencilState(depthState())
			//?} else {
			/*.withDepthWrite(false)
			*///?}
			.build();
	//?}

	/** Nucleo solido de la viga, dibujado sobre el render target del clima. */
	//? if >= 26.2 {
	/*public static final RenderPipeline LOOT_BEAM_CENTER = withMatrixLayouts(RenderPipeline.builder())
			.withLocation(ModTemplate.id("pipeline/loot_beam_center"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withVertexBinding(0, DefaultVertexFormat.POSITION_COLOR)
			.withPrimitiveTopology(PrimitiveTopology.QUADS)
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			.build();
	*///?} else {
	public static final RenderPipeline LOOT_BEAM_CENTER = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
			.withLocation(ModTemplate.id("pipeline/loot_beam_center"))
			.withVertexShader("core/position_color")
			.withFragmentShader("core/position_color")
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			//? if >= 26.1.2 {
			.withColorTargetState(new ColorTargetState(BlendFunction.LIGHTNING))
			//?} else {
			/*.withBlend(BlendFunction.LIGHTNING)
			*///?}
			.build();
	//?}

	//? if >= 26.3 {
	/*/^*
	 * Bind groups de matrices. 26.3 dividio {@code MATRICES_PROJECTION} en
	 * {@code DYNAMIC_TRANSFORMS} + {@code PROJECTION} y, ademas, el motor valida en desarrollo
	 * ({@code STRICT_VALIDATION}) que el pipeline declare todo uniform que usen sus shaders.
	 ^/
	private static RenderPipeline.Builder withMatrixLayouts(RenderPipeline.Builder builder) {
		return builder
				.withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
				.withBindGroupLayout(BindGroupLayouts.PROJECTION);
	}

	/^* 26.3: los shaders que leen {@code GameTime} deben declarar el bind group de globals. ^/
	private static RenderPipeline.Builder withGlobalsLayout(RenderPipeline.Builder builder) {
		return builder.withBindGroupLayout(BindGroupLayouts.GLOBALS);
	}
	*///?} elif >= 26.2 {
	/*private static RenderPipeline.Builder withMatrixLayouts(RenderPipeline.Builder builder) {
		return builder.withBindGroupLayout(MATRICES_PROJECTION_LAYOUT);
	}

	private static RenderPipeline.Builder withGlobalsLayout(RenderPipeline.Builder builder) {
		return builder.withBindGroupLayout(BindGroupLayouts.GLOBALS);
	}
	*///?}

	private static boolean coreUsesFog(String name) {
		return name.endsWith("_add") || name.endsWith("_glow_overlay") || name.endsWith("_particle_overlay")
				|| name.endsWith("_droplight") || name.endsWith("_droplight_animated") || name.endsWith("_droplight_glow");
	}

	/** Shaders del mod que leen {@code GameTime} (droplight animado con ruido simplex). */
	private static boolean coreUsesGlobals(String name) {
		// {@code lootbeams_droplight_animated} solo importa globals.glsl en su fragment shader pero no
		// lee ningun campo suyo (no usa GameTime), y en 26.1.2 declarar un UBO que el shader no usa
		// deja el pipeline sin dibujar nada. Los shaders que si lo necesitan son los personalizados
		// (glow_vortex*), que siempre declaran GLOBALS_SNIPPET.
		return false;
	}

	private static RenderPipeline core(String name, VertexFormat format) {
		//? if >= 26.2 {
		/*RenderPipeline.Builder builder = withMatrixLayouts(RenderPipeline.builder())
				.withLocation(ModTemplate.id("pipeline/" + name))
				.withVertexShader(ModTemplate.id("core/" + name))
				.withFragmentShader(ModTemplate.id("core/" + name));
		if (coreUsesFog(name)) {
			builder = builder.withBindGroupLayout(BindGroupLayouts.FOG);
		}
		if (coreUsesGlobals(name)) {
			builder = withGlobalsLayout(builder);
		}
		// Los shaders del mod solo declaran Sampler0 (ya no muestrean el lightmap) y el RenderSetup
		// solo enlaza esa textura: declarar SAMPLER2 rompe STRICT_VALIDATION en desarrollo.
		builder = builder.withBindGroupLayout(BindGroupLayouts.SAMPLER0);
		return builder
				.withVertexBinding(0, format)
				.withPrimitiveTopology(PrimitiveTopology.QUADS)
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				.withCull(false)
				.withDepthStencilState(depthState())
				.build();
		*///?} else {
		//? if >= 26.1.2 && < 26.2 {
		RenderPipeline.Snippet[] snippets = coreUsesGlobals(name)
				? new RenderPipeline.Snippet[] { RenderPipelines.MATRICES_FOG_SNIPPET, GLOBALS_SNIPPET }
				: new RenderPipeline.Snippet[] { RenderPipelines.MATRICES_FOG_SNIPPET };
		return RenderPipeline.builder(snippets)
				.withLocation(ModTemplate.id("pipeline/" + name))
		//?} else {
		/*return RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
				.withLocation(ModTemplate.id("pipeline/" + name))
		*///?}
				.withVertexShader(ModTemplate.id("core/" + name))
				.withFragmentShader(ModTemplate.id("core/" + name))
				.withSampler("Sampler0")
				.withVertexFormat(format, VertexFormat.Mode.QUADS)
				//? if >= 26.1.2 {
				.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
				//?} else {
				/*.withBlend(BlendFunction.TRANSLUCENT)
				*///?}
				.withCull(false)
				//? if >= 26.1.2 {
				.withDepthStencilState(depthState())
				//?} else {
				/*.withDepthWrite(false)
				*///?}
				.build();
		//?}
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
			String name = StringHelper.getFormattedName(path);
			// Los shaders personalizados solo enlazan Sampler0 en su RenderSetup: declarar
			// Sampler1/Sampler2 rompe STRICT_VALIDATION en desarrollo.
			//? if >= 26.2 {
			/*RenderPipeline.Builder customBuilder = withMatrixLayouts(RenderPipeline.builder())
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
					.withVertexShader(ModTemplate.id(shaderPath))
					.withFragmentShader(ModTemplate.id(shaderPath))
					.withBindGroupLayout(BindGroupLayouts.FOG)
					.withBindGroupLayout(BindGroupLayouts.SAMPLER0);
			CUSTOM_PIPELINES.put(name, withGlobalsLayout(customBuilder)
					.withVertexBinding(0, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER)
					.withPrimitiveTopology(PrimitiveTopology.QUADS)
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					.withCull(false)
					.withDepthStencilState(depthState())
					.build());
			*///?} else {
			//? if >= 26.1.2 && < 26.2 {
			// Los shaders personalizados pueden leer GameTime: declara tambien el bloque Globals.
			CUSTOM_PIPELINES.put(name, RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET, GLOBALS_SNIPPET)
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
			//?} else {
			/*CUSTOM_PIPELINES.put(name, RenderPipeline.builder(RenderPipelines.MATRICES_FOG_SNIPPET)
					.withLocation(ModTemplate.id("pipeline/" + shaderPath))
			*///?}
					.withVertexShader(ModTemplate.id(shaderPath))
					.withFragmentShader(ModTemplate.id(shaderPath))
					.withSampler("Sampler0")
					.withVertexFormat(CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER, VertexFormat.Mode.QUADS)
					//? if >= 26.1.2 {
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
					//?} else {
					/*.withBlend(BlendFunction.TRANSLUCENT)
					*///?}
					.withCull(false)
					//? if >= 26.1.2 {
					.withDepthStencilState(depthState())
					//?} else {
					/*.withDepthWrite(false)
					*///?}
					.build());
			//?}
		}
	}
}

//?}
