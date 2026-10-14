package com.github.razorplay.lootbeams.renderers;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.helpers.TargetHelper;
import com.github.razorplay.lootbeams.helpers.ViewHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class HudRenderer {
   public static void onHudRender(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
      float tickDelta = tickCounter.getGameTimeDeltaPartialTick(false);
      Minecraft client = Minecraft.getInstance();
      if (ViewHelper.shouldRenderCrosshair(client)) {
         if (client.hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)client.hitResult).getEntity();
            if (entity instanceof ItemFrame itemFrameEntity) {
               ItemStack itemStack = itemFrameEntity.getItem();
               Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
               boolean hasLabel = !itemStack.isEmpty() && itemStack.has(DataComponents.CUSTOM_NAME);
               if (hasLabel && itemConfig.itemFrameTooltips) {
                  TooltipRenderer.renderWorldPositionTooltip(drawContext, entity, itemStack, tickDelta);
                  return;
               }

               return;
            }
         }

         Player player = client.player;
         HitResult result = TargetHelper.getEntityItem(player);
         if (result != null && result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult)result).getEntity();
            if (entity instanceof ItemEntity itemEntity) {
               ItemStack itemStack = itemEntity.getItem();
               Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
               boolean shouldRender = ViewHelper.shouldRenderOnItem(itemStack);
               if (!shouldRender || itemConfig.requireOnGround && !entity.onGround()) {
                  return;
               }

               TooltipRenderer.renderWorldPositionTooltip(drawContext, entity, itemStack, tickDelta);
            }
         }
      }
   }
}
