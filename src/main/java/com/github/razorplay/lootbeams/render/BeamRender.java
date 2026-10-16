package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.BeamOpacityOnApproach;
import com.github.razorplay.lootbeams.features.BeamSizeOnApproach;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.helpers.NumberHelper;
import com.github.razorplay.lootbeams.helpers.TextColorHelper;
import com.github.razorplay.lootbeams.renderers.DroplightRenderer;
import com.github.razorplay.lootbeams.renderers.GroundEffectRenderer;
import com.github.razorplay.lootbeams.renderers.IBeamRenderer;
import com.github.razorplay.lootbeams.renderers.LootBeamRenderer;
import com.github.razorplay.lootbeams.renderers.NameTagRenderer;
import com.github.razorplay.lootbeams.utils.ParticleEmitter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.item.ItemEntity;

import java.util.HashMap;
import java.util.Map;

public class BeamRender {
   private BeamRender() {
   }

   private static final Map<ItemEntity, Integer> ITEM_GROUND_START_TIMES = new HashMap<>();

   private static float fadeDistanceAlpha(float distance, Configuration itemConfig) {
      float defaultMultiplier = 1.0F;
      if (itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.DISABLED) {
         return defaultMultiplier;
      } else if (itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.FADE_IN) {
         return (float)NumberHelper.clampedMapRange(distance, itemConfig.changeOffset, itemConfig.changeDistance, 1.0F, 0.0F);
      } else {
         return itemConfig.beamOpacityOnApproach == BeamOpacityOnApproach.FADE_OUT
            ? (float)NumberHelper.clampedMapRange(distance, itemConfig.changeOffset, itemConfig.changeDistance, 0.0F, 1.0F)
            : defaultMultiplier;
      }
   }

   private static float getBeamSizeMultiplier(float distance, Configuration itemConfig) {
      float defaultMultiplier = 1.0F;
      if (itemConfig.beamSizeOnApproach == BeamSizeOnApproach.DISABLED) {
         return defaultMultiplier;
      } else if (itemConfig.beamSizeOnApproach == BeamSizeOnApproach.SHRINK) {
         return (float)NumberHelper.clampedMapRange(distance, itemConfig.changeOffset, itemConfig.changeDistance, 0.0F, 1.0F);
      } else {
         return itemConfig.beamSizeOnApproach == BeamSizeOnApproach.GROW
            ? (float)NumberHelper.clampedMapRange(distance, itemConfig.changeOffset, itemConfig.changeDistance, 1.0F, 0.0F)
            : defaultMultiplier;
      }
   }

   public static boolean canRenderBeam(Configuration itemConfig, float fadeAlpha) {
      return !(itemConfig.beamAlpha * fadeAlpha < 0.01F);
   }

	public static void render(PoseStack stack, LootBeamBuffer buffer, ItemEntity item, long worldtime, float pticks) {
		if (!ITEM_GROUND_START_TIMES.containsKey(item)) {
			ITEM_GROUND_START_TIMES.put(item, item.getAge());
		}

		float itemGroundStartTime = ITEM_GROUND_START_TIMES.getOrDefault(item, 0).intValue();
		float currentGroundTime = item.getAge() - itemGroundStartTime + pticks;
		Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(item.getItem());
		LocalPlayer player = Minecraft.getInstance().player;
		float distance = player != null ? player.distanceTo(item) : 0.0F;
		float sizeMultiplier = getBeamSizeMultiplier(distance, itemConfig);
		float fadeAlpha = fadeDistanceAlpha(distance, itemConfig);

		if (canRenderBeam(itemConfig, fadeAlpha)) {
			//? if <= 1.21.4 {
			/*RenderSystem.enableDepthTest();
			 *///?}

			TextColor color = TextColorHelper.getItemColor(item.getItem());

			if (item.onGround()) {
				stack.pushPose();
				GroundEffectRenderer.renderGroundEffect(buffer, stack, item, itemConfig, color, sizeMultiplier, fadeAlpha, currentGroundTime, worldtime, pticks);
				IBeamRenderer iBeamRenderer = itemConfig.renderDroplightBeam ? DroplightRenderer::renderBeam : LootBeamRenderer::renderBeam;
				iBeamRenderer.renderBeam(buffer, stack, item, itemConfig, color, sizeMultiplier, fadeAlpha, currentGroundTime, worldtime, pticks);
				stack.popPose();
				NameTagRenderer.renderNameTags(buffer, stack, item, itemConfig, color, fadeAlpha, currentGroundTime, worldtime, pticks);
				ParticleEmitter.createParticlesForItem(item, itemConfig, item.getAge(), color, fadeAlpha, pticks);
			}

			//? if <= 1.21.4 {
			/*RenderSystem.disableDepthTest();
			 *///?}

			ITEM_GROUND_START_TIMES.keySet().removeIf(itemEntity -> !itemEntity.onGround());
		}
	}
}
