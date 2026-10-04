package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class RenderHelper {
   public static void bindTexture(ResourceLocation texture) {
      Minecraft minecraft = Minecraft.getInstance();
      AbstractTexture abstractTexture = minecraft.getTextureManager().getTexture(texture);
      abstractTexture.bind();
   }

   public static Rect2i getCurrentTextureSize() {
      int textureWidth = GlStateManager._getTexLevelParameter(3553, 0, 4096);
      int textureHeight = GlStateManager._getTexLevelParameter(3553, 0, 4097);
      return new Rect2i(0, 0, textureWidth, textureHeight);
   }

   public static Rect2i getSpritePositionAndSize(TextureAtlasSprite sprite) {
      return new Rect2i(sprite.getX(), sprite.getY(), sprite.contents().width(), sprite.contents().height());
   }

   public static void blit(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, int texX, int texY, int regionWidth, int regionHeight) {
      bindTexture(texture);
      Rect2i textureSize = getCurrentTextureSize();
      blit(context, texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureSize.getWidth(), textureSize.getHeight());
   }

   public static void blit(GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, int texX, int texY) {
      blit(context, texture, x, y, width, height, texX, texY, width, height);
   }

   public static void blit(GuiGraphics context, ResourceLocation texture, int x, int y) {
      bindTexture(texture);
      Rect2i textureSize = getCurrentTextureSize();
      blit(context, texture, x, y, textureSize.getWidth(), textureSize.getHeight(), 0, 0);
   }

   public static void blit(
      GuiGraphics context,
      ResourceLocation texture,
      int x,
      int y,
      int width,
      int height,
      float texX,
      float texY,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight
   ) {
      context.blit(texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureWidth, textureHeight);
   }

   public static void drawColored(
      GuiGraphics context,
      ResourceLocation texture,
      int x,
      int y,
      int width,
      int height,
      float u,
      float v,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight,
      String color
   ) {
      drawColored(context, texture, x, x + width, y, y + height, 0, regionWidth, regionHeight, u, v, textureWidth, textureHeight, color);
   }

   public static void drawColored(
      GuiGraphics context, ResourceLocation texture, int x, int y, int width, int height, int texX, int texY, int regionWidth, int regionHeight, String color
   ) {
      bindTexture(texture);
      Rect2i textureSize = getCurrentTextureSize();
      drawColored(context, texture, x, y, width, height, texX, texY, regionWidth, regionHeight, textureSize.getWidth(), textureSize.getHeight(), color);
   }

   public static void drawColored(
      GuiGraphics context,
      ResourceLocation texture,
      int x0,
      int x1,
      int y0,
      int y1,
      int z,
      int regionWidth,
      int regionHeight,
      float u,
      float v,
      int textureWidth,
      int textureHeight,
      String color
   ) {
      drawColoredBlit(
         context,
         texture,
         x0,
         x1,
         y0,
         y1,
         z,
         (u + 0.0F) / textureWidth,
         (u + regionWidth) / textureWidth,
         (v + 0.0F) / textureHeight,
         (v + regionHeight) / textureHeight,
         color
      );
   }

   public static void drawColoredBlit(
      GuiGraphics context, ResourceLocation texture, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1, String color
   ) {
      RenderSystem.setShaderTexture(0, texture);
      RenderSystem.setShader(() -> LootBeamShaders.getShader(LootBeamShaders.Shader.PARTICLE_OVERLAY));
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      Matrix4f matrix4f = context.pose().last().pose();
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
      if (color.isEmpty()) {
         bufferBuilder.addVertex(matrix4f, x0, y0, z).setUv(u0, v0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x0, y1, z).setUv(u0, v1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x1, y0, z).setUv(u1, v0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(15728880);
      } else {
         int colorValue = ColorHelper.parseColor(color);
         float R = ColorHelper.getRed(colorValue) / 255.0F;
         float G = ColorHelper.getGreen(colorValue) / 255.0F;
         float B = ColorHelper.getBlue(colorValue) / 255.0F;
         float A = ColorHelper.getAlpha(colorValue) / 255.0F;
         bufferBuilder.addVertex(matrix4f, x0, y0, z).setUv(u0, v0).setColor(R, G, B, A).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x0, y1, z).setUv(u0, v1).setColor(R, G, B, A).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1).setColor(R, G, B, A).setLight(15728880);
         bufferBuilder.addVertex(matrix4f, x1, y0, z).setUv(u1, v0).setColor(R, G, B, A).setLight(15728880);
      }

      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
   }

   public static void blit(
      PoseStack poseStack, int x, int y, int width, int height, float texX, float texY, int texWidth, int texHeight, int fullWidth, int fullHeight
   ) {
      blit(poseStack, x, x + width, y, y + height, 0, texWidth, texHeight, texX, texY, fullWidth, fullHeight);
   }

   public static void blit(
      PoseStack poseStack, int x0, int x1, int y0, int y1, int z, int texWidth, int texHeight, float texX, float texY, int fullWidth, int fullHeight
   ) {
      innerBlit(
         poseStack, x0, x1, y0, y1, z, (texX + 0.0F) / fullWidth, (texX + texWidth) / fullWidth, (texY + 0.0F) / fullHeight, (texY + texHeight) / fullHeight
      );
   }

   private static void innerBlit(PoseStack poseStack, int x0, int x1, int y0, int y1, int z, float u0, float u1, float v0, float v1) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      Matrix4f matrix4f = poseStack.last().pose();
      BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
      bufferBuilder.addVertex(matrix4f, x0, y0, z).setUv(u0, v0);
      bufferBuilder.addVertex(matrix4f, x0, y1, z).setUv(u0, v1);
      bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1);
      bufferBuilder.addVertex(matrix4f, x1, y0, z).setUv(u1, v0);
      BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
   }

   public static void drawTextureAt(GuiGraphics context, ResourceLocation texture, int x, int y) {
      blit(context, texture, x, y);
   }
}
