package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import org.lwjgl.opengl.GL11;

public class ParticleRenderType implements net.minecraft.client.particle.ParticleRenderType {
   private Identifier texture;
   private LootBeamShaders.Shader shader = LootBeamShaders.Shader.PARTICLE_OVERLAY;

   public ParticleRenderType setTexture(Identifier texture) {
      this.texture = texture;
      return this;
   }

   public ParticleRenderType setShader(LootBeamShaders.Shader shader) {
      this.shader = shader;
      return this;
   }

   public BufferBuilder begin(Tesselator builder, TextureManager textureManager) {
      RenderSystem.enableBlend();
      RenderSystem.enableCull();
      RenderSystem.depthFunc(515);
      GL11.glEnable(3042);
      RenderSystem.depthMask(false);
      RenderSystem.colorMask(true, true, true, true);
      boolean shadersLoaded = IrisCompat.isShaderPackInUse();
      RenderSystem.setShaderTexture(0, this.texture);
      if (shadersLoaded) {
         RenderSystem.setShader(GameRenderer::getRendertypeEntityTranslucentEmissiveShader);
      } else {
         RenderSystem.setShader(() -> LootBeamShaders.getShader(this.shader));
      }

      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      return shadersLoaded
         ? builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY)
         : builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
   }

   public void end(Tesselator tessellator) {
      tessellator.clear();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.disableBlend();
      RenderSystem.depthMask(true);
   }
}
