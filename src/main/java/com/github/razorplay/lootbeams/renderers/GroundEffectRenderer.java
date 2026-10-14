package com.github.razorplay.lootbeams.renderers;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.extensions.LootbeamsBufferBuilder;
import com.github.razorplay.lootbeams.helpers.ColorHelper;
import com.github.razorplay.lootbeams.helpers.NumberHelper;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.render.LootBeamRenderLayers;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.item.ItemEntity;
import org.joml.Matrix4f;

import java.util.List;

//? <= 1.21.1 {
/*import net.minecraft.client.renderer.RenderType;
*///?} else {
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

public class GroundEffectRenderer {
   public static void renderGroundEffect(
      LootBeamBuffer buffer,
      PoseStack matrixStack,
      ItemEntity itemEntity,
      Configuration itemConfig,
      TextColor color,
      float sizeMultiplier,
      float fadeAlpha,
      float currentGroundTime,
      long worldtime,
      float pticks
   ) {
      ColorHelper.Color effectColor = ColorHelper.Color.of(color);
      if (itemConfig.glowEffect) {
         matrixStack.pushPose();
         matrixStack.translate(0.0F, 0.001F, 0.0F);
         if (itemConfig.rotateGlow) {
            float rotationSpeed = itemConfig.glowRotationSpeed;
            float rotation = currentGroundTime * rotationSpeed % 360.0F;
            float rotationDirection = itemConfig.glowRotateClockwise ? -1.0F : 1.0F;
            //? if >= 26.3 {
            matrixStack.rotate(Axis.YP.rotationDegrees(rotation * rotationDirection));
            //?} else {
            /*matrixStack.mulPose(Axis.YP.rotationDegrees(rotation * rotationDirection));
            *///?}
         }

         TextureAtlasSprite glowEffectSprite = itemConfig.glowEffectTexture.getSprite();
         if (IrisCompat.isShaderPackInUse() && itemConfig.glowEffectTexture == GlowEffectManager.GLOW_TEXTURE) {
            glowEffectSprite = GlowEffectManager.GLOW_SHADER_TEXTURE.getSprite();
         }

         if (!IrisCompat.isShaderPackInUse() && itemConfig.glowEffectTexture == GlowEffectManager.GLOW_SHADER_TEXTURE) {
            glowEffectSprite = GlowEffectManager.GLOW_TEXTURE.getSprite();
         }

         RenderType glowLayer = LootBeamRenderLayers.groundGlowEffect(
            glowEffectSprite.atlasLocation(), itemConfig.glowEffectTexture.isColored(), itemConfig.useGlowGradient, itemConfig.glowCustomShader
         );
         float radius = itemConfig.glowEffectRadius;
         float glowEffectAlpha = fadeAlpha * itemConfig.glowEffectAlpha;
         if (itemConfig.pulseGlow) {
            float pulseSpeed = itemConfig.pulseGlowSpeed / 10.0F;
            float pulseMinAlpha = itemConfig.pulseGlowMinAlpha;
            float pulseMaxAlpha = itemConfig.pulseGlowMaxAlpha;
            float pulseMinRadius = itemConfig.pulseGlowMinRadius;
            float pulseMaxRadius = itemConfig.pulseGlowMaxRadius;
            float cosineFactor = (float)Math.cos(currentGroundTime * pulseSpeed);
            float normalizedCosine = cosineFactor * 0.5F + 0.5F;
            float pulseAlpha = (float)NumberHelper.clampedMapRange(normalizedCosine, 0, 1, pulseMinAlpha, pulseMaxAlpha);
            float pulseRadius = (float)NumberHelper.clampedMapRange(normalizedCosine, 0, 1, pulseMinRadius, pulseMaxRadius);
            glowEffectAlpha = pulseAlpha * fadeAlpha;
            radius = pulseRadius;
         }

         radius *= sizeMultiplier;
         if (radius < itemConfig.pulseGlowMinRadius) {
            radius = itemConfig.pulseGlowMinRadius;
         }

         float glowR = effectColor.fR;
         float glowG = effectColor.fG;
         float glowB = effectColor.fB;
         if (itemConfig.glowEffectTexture.isColored()) {
            float averageColor = LootBeamShaders.getAverageColor(LootBeamShaders.Shader.GLOW_OVERLAY);
            glowR = averageColor;
            glowG = averageColor;
            glowB = averageColor;
         }

         if (itemConfig.smoothGlowEffectRadius) {
            radius = NumberHelper.smoothValue(radius, currentGroundTime, itemConfig.smoothDuration);
         }

         if (itemConfig.smoothGlowEffectAlpha) {
            glowEffectAlpha = NumberHelper.smoothValue(glowEffectAlpha, currentGroundTime, itemConfig.smoothDuration);
         }

         boolean useGlowGradient = !itemConfig.glowEffectTexture.isColored() && itemConfig.useGlowGradient
            || itemConfig.glowCustomShader != LootBeamShaders.CustomShader.NONE;
         final float glowRSnapshot = glowR;
         final float glowGSnapshot = glowG;
         final float glowBSnapshot = glowB;
         final float glowAlphaSnapshot = glowEffectAlpha;
         final float glowRadiusSnapshot = radius;
         final TextureAtlasSprite glowSpriteSnapshot = glowEffectSprite;
         buffer.submit(
            glowLayer,
            matrixStack,
            (builder, poseStack) -> renderGlow(
               poseStack,
               builder,
               glowRSnapshot,
               glowGSnapshot,
               glowBSnapshot,
               glowAlphaSnapshot,
               glowRadiusSnapshot,
               glowSpriteSnapshot,
               useGlowGradient,
               itemConfig.glowGradientModifiers,
               itemConfig.glowGradientStart / 100.0F,
               itemConfig.glowGradientEnd / 100.0F
            )
         );
         buffer.endBatch();
         matrixStack.popPose();
      }
   }

   private static LootbeamsBufferBuilder addGlowVertex(
      VertexConsumer builder, PoseStack.Pose matrixEntry, float x, float y, float z, float red, float green, float blue, float alpha, float u, float v
   ) {
      Matrix4f poseMatrix = matrixEntry.pose();
      return ((LootbeamsBufferBuilder)builder.addVertex(poseMatrix, x, y, z)
         .setColor(red, green, blue, alpha)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(matrixEntry, 0.0F, 1.0F, 0.0F))
         .color0((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
   }

   private static void modifyGlowVertex(
      LootbeamsBufferBuilder builder,
      ColorHelper.Color color1,
      float centerU,
      float centerV,
      float uvWidth,
      float uvHeight,
      float gradientStart,
      float gradientEnd,
      boolean useGlowGradient
   ) {
      if (useGlowGradient) {
         builder.uvCenter(centerU, centerV)
            .uvSize(uvWidth, uvHeight)
            .shortCustomData(gradientStart, gradientEnd)
            .color1(color1.R, color1.G, color1.B, color1.A);
      }
   }

   private static void renderGlow(
      PoseStack stack,
      VertexConsumer builder,
      float red,
      float green,
      float blue,
      float alpha,
      float radius,
      TextureAtlasSprite glowSprite,
      boolean useGlowGradient,
      List<String> gradientModifiers,
      float gradientStart,
      float gradientEnd
   ) {
      PoseStack.Pose matrixentry = stack.last();
      boolean shadersLoaded = IrisCompat.isShaderPackInUse();
      float minX = glowSprite.getU0();
      float maxX = glowSprite.getU1();
      float minY = glowSprite.getV0();
      float maxY = glowSprite.getV1();
      float centerU = (minX + maxX) / 2.0F;
      float centerV = (minY + maxY) / 2.0F;
      float uvWidth = maxX - minX;
      float uvHeight = maxY - minY;
      ColorHelper.Color secondColor = ColorHelper.Color.of(DroplightRenderer.getSecondColor(red, green, blue, alpha, gradientModifiers));
      modifyGlowVertex(
         addGlowVertex(builder, matrixentry, -radius, 0.0F, -radius, red, green, blue, alpha, minX, minY),
         secondColor,
         centerU,
         centerV,
         uvWidth,
         uvHeight,
         gradientStart,
         gradientEnd,
         useGlowGradient && !shadersLoaded
      );
      modifyGlowVertex(
         addGlowVertex(builder, matrixentry, -radius, 0.0F, radius, red, green, blue, alpha, minX, maxY),
         secondColor,
         centerU,
         centerV,
         uvWidth,
         uvHeight,
         gradientStart,
         gradientEnd,
         useGlowGradient && !shadersLoaded
      );
      modifyGlowVertex(
         addGlowVertex(builder, matrixentry, radius, 0.0F, radius, red, green, blue, alpha, maxX, maxY),
         secondColor,
         centerU,
         centerV,
         uvWidth,
         uvHeight,
         gradientStart,
         gradientEnd,
         useGlowGradient && !shadersLoaded
      );
      modifyGlowVertex(
         addGlowVertex(builder, matrixentry, radius, 0.0F, -radius, red, green, blue, alpha, maxX, minY),
         secondColor,
         centerU,
         centerV,
         uvWidth,
         uvHeight,
         gradientStart,
         gradientEnd,
         useGlowGradient && !shadersLoaded
      );
   }
}
