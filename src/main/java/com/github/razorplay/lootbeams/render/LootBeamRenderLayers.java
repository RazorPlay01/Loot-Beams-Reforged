package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class LootBeamRenderLayers {
   private LootBeamRenderLayers() {
      /* This utility class should not be instantiated */
   }

   private static final RenderStateShard.TransparencyStateShard LOOTBEAM_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("loot_beam_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });

   public static RenderType lootBeamLightning() {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setTransparencyState(LOOTBEAM_TRANSPARENCY)
         .setCullState(RenderStateShard.NO_CULL)
         .createCompositeState(false);
      return RenderType.create("loot_beam_lightning", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, state);
   }

   public static RenderType lootBeam(ResourceLocation texture) {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .setShaderState(RenderStateShard.RENDERTYPE_BEACON_BEAM_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .createCompositeState(false);
      return RenderType.create("loot_beam", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 1536, false, true, state);
   }

   public static RenderType lootBeamCenter() {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
         .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
         .setOutputState(RenderStateShard.WEATHER_TARGET)
         .createCompositeState(false);
      return RenderType.create("loot_beam_center", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, state);
   }

   public static RenderType droplight(ResourceLocation texture) {
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
   }

   public static RenderType droplightAnimated(ResourceLocation texture) {
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
   }

   public static RenderType groundGlowEffect(ResourceLocation texture, boolean isColored, boolean useGlowGradient, LootBeamShaders.CustomShader customShader) {
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
   }

   public static RenderType translucentNoCull(ResourceLocation texture) {
      RenderType.CompositeState state = RenderType.CompositeState.builder()
         .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
         .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
         .setWriteMaskState(RenderStateShard.COLOR_WRITE)
         .setCullState(RenderStateShard.NO_CULL)
         .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
         .createCompositeState(false);
      return RenderType.create("loot_beam_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true, state);
   }
}
