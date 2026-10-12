package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;

//? <= 1.21.1 {
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
//?} else {
/*import com.github.razorplay.lootbeams.mixin.RenderTypeMixin;
import com.github.razorplay.lootbeams.shaders.LootBeamPipelines;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
*///?}

public class LootBeamRenderLayers {
   private LootBeamRenderLayers() {
      /* This utility class should not be instantiated */
   }

   //? if <= 1.21.1 {
   private static final RenderStateShard.TransparencyStateShard LOOTBEAM_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("loot_beam_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   //?}

   public static RenderType lootBeamLightning() {
      //? if <= 1.21.1 {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setTransparencyState(LOOTBEAM_TRANSPARENCY)
         .setCullState(RenderStateShard.NO_CULL)
         .createCompositeState(false);
      return RenderType.create("loot_beam_lightning", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, state);
      //?} else {
      /*return RenderTypeMixin.lootbeams$create(
         "loot_beam_lightning",
         RenderSetup.builder(LootBeamPipelines.LOOT_BEAM_LIGHTNING).createRenderSetup()
      );
      *///?}
   }

   public static RenderType lootBeam(ResourceLocation texture) {
      //? if <= 1.21.1 {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setShaderState(RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .createCompositeState(false);
      return RenderType.create("loot_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, state);
      //?} else {
      /*return RenderTypeMixin.lootbeams$create(
         "loot_beam",
         RenderSetup.builder(LootBeamPipelines.LOOT_BEAM).withTexture("Sampler0", texture).createRenderSetup()
      );
      *///?}
   }

   public static RenderType lootBeamCenter() {
      //? if <= 1.21.1 {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
         .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
         .setOutputState(RenderStateShard.WEATHER_TARGET)
         .createCompositeState(false);
      return RenderType.create("loot_beam_center", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, state);
      //?} else {
      /*return RenderTypeMixin.lootbeams$create(
         "loot_beam_center",
         RenderSetup.builder(RenderPipelines.LIGHTNING).setOutputTarget(OutputTarget.WEATHER_TARGET).createRenderSetup()
      );
      *///?}
   }

   public static RenderType droplight(ResourceLocation texture) {
      //? if <= 1.21.1 {
      if (IrisCompat.isShaderPackInUse()) {
         return RenderType.entityTranslucentEmissive(texture, false);
      }

      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setShaderState(new RenderStateShard.ShaderStateShard(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT)))
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      return RenderType.create(
         "loot_beam_droplight", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM, VertexFormat.Mode.QUADS, 1536, false, true, state
      );
      //?} else {
      /*if (IrisCompat.isShaderPackInUse()) {
         return RenderTypes.entityTranslucentEmissive(texture, false);
      }

      return RenderTypeMixin.lootbeams$create(
         "loot_beam_droplight",
         RenderSetup.builder(LootBeamPipelines.DROPLIGHT).withTexture("Sampler0", texture).createRenderSetup()
      );
      *///?}
   }

   public static RenderType droplightAnimated(ResourceLocation texture) {
      //? if <= 1.21.1 {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setShaderState(new RenderStateShard.ShaderStateShard(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT_ANIMATED)))
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      return RenderType.create(
         "loot_beam_droplight_animated", CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM, VertexFormat.Mode.QUADS, 1536, false, true, state
      );
      //?} else {
      /*return RenderTypeMixin.lootbeams$create(
         "loot_beam_droplight_animated",
         RenderSetup.builder(LootBeamPipelines.DROPLIGHT_ANIMATED).withTexture("Sampler0", texture).createRenderSetup()
      );
      *///?}
   }

   public static RenderType groundGlowEffect(ResourceLocation texture, boolean isColored, boolean useGlowGradient, LootBeamShaders.CustomShader customShader) {
      //? if <= 1.21.1 {
      if (IrisCompat.isShaderPackInUse()) {
         return RenderType.entityTranslucentEmissive(texture, false);
      }

      ShaderInstance customShaderProgram = LootBeamShaders.getShader(customShader);
      RenderStateShard.ShaderStateShard shaderProgram;
      VertexFormat vertexFormat;
      if (customShader != LootBeamShaders.CustomShader.NONE && customShaderProgram != null) {
         shaderProgram = new RenderStateShard.ShaderStateShard(() -> customShaderProgram);
         vertexFormat = CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER;
      } else if (isColored) {
         shaderProgram = new RenderStateShard.ShaderStateShard(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.GLOW_OVERLAY));
         vertexFormat = DefaultVertexFormat.POSITION_TEX_COLOR;
      } else if (useGlowGradient) {
         shaderProgram = new RenderStateShard.ShaderStateShard(() -> LootBeamShaders.getShader(LootBeamShaders.DroplightShader.DROPLIGHT_GLOW));
         vertexFormat = CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER;
      } else {
         shaderProgram = new RenderStateShard.ShaderStateShard(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.ADD));
         vertexFormat = DefaultVertexFormat.POSITION_TEX_COLOR;
      }

      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setShaderState(shaderProgram)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      return RenderType.create("loot_beam_ground_glow", vertexFormat, VertexFormat.Mode.QUADS, 1536, false, true, state);
      //?} else {
      /*if (IrisCompat.isShaderPackInUse()) {
         return RenderTypes.entityTranslucentEmissive(texture, false);
      }

      RenderPipeline pipeline;
      if (customShader != LootBeamShaders.CustomShader.NONE && LootBeamPipelines.getShader(customShader) != null) {
         pipeline = LootBeamPipelines.getShader(customShader);
      } else if (isColored) {
         pipeline = LootBeamPipelines.GLOW_OVERLAY;
      } else if (useGlowGradient) {
         pipeline = LootBeamPipelines.DROPLIGHT_GLOW;
      } else {
         pipeline = LootBeamPipelines.ADD;
      }

      return RenderTypeMixin.lootbeams$create(
         "loot_beam_ground_glow",
         RenderSetup.builder(pipeline).withTexture("Sampler0", texture).createRenderSetup()
      );
      *///?}
   }

   public static RenderType translucentNoCull(ResourceLocation texture) {
      //? if <= 1.21.1 {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      return RenderType.create("loot_beam_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, state);
      //?} else {
      /*return RenderTypes.entityTranslucentEmissive(texture, false);
      *///?}
   }
}
