package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.helpers.ItemHelper;
import com.github.razorplay.lootbeams.helpers.RarityHelper;
import com.github.razorplay.lootbeams.helpers.ViewHelper;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.github.razorplay.lootbeams.render.BeamRender;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
   @Shadow
   @Final
   protected EntityRenderDispatcher entityRenderDispatcher;
   @Unique
   ItemEntity entity;

   @Inject(method = "render", at = @At("HEAD"))
   private void attemptRenderBeams(
           Entity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci
   ) {
      if (entity instanceof ItemEntity itemEntity) {
         this.entity = itemEntity;
         Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemEntity.getItem());
         float renderDistance = itemConfig.renderDistance;
         if (Minecraft.getInstance().player == null || Minecraft.getInstance().player.distanceTo(itemEntity) > renderDistance) {
            return;
         }

         boolean shouldRender = ViewHelper.shouldRenderOnItem(itemEntity.getItem()) && itemEntity.onGround();
         if (shouldRender) {
            RenderManager.addRenderAfterWeather((stack, consumer) -> {
               MultiBufferSource.BufferSource consumerProvider = Minecraft.getInstance().renderBuffers().bufferSource();
               stack.pushPose();
               stack.translate(itemEntity.position().x(), itemEntity.position().y(), itemEntity.position().z());
               BeamRender.render(stack, consumerProvider, itemEntity, itemEntity.level().getGameTime(), partialTick);
               stack.popPose();
            });
         }
      }
   }

   @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
   private void modifyRenderDistance(T entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof ItemEntity itemEntity) {
         ItemStack itemStack = itemEntity.getItem();
         Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
         double maxDistance = itemConfig.renderDistance;
         double distanceSquared = this.entityRenderDispatcher.distanceToSqr(entity);
         if (distanceSquared < maxDistance * maxDistance) {
            cir.setReturnValue(true);
         }
      }
   }

   @Inject(at = @At("HEAD"), method = "renderNameTag(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IF)V", cancellable = true)
   protected void renderLabelIfPresent(T entity, Component text, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float tickDelta, CallbackInfo ci) {
      if (entity instanceof ItemFrame itemFrameEntity) {
         Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemFrameEntity.getItem());
         if (itemConfig.advancedTooltips && itemConfig.itemFrameTooltips) {
            ci.cancel();
         }
      }
   }

   @ModifyVariable(at = @At("HEAD"), method = "render", ordinal = 0, argsOnly = true)
   public int render(int light) {
      if (this.entity != null
         && (
			  ModTemplate.config.allItems
               || ModTemplate.config.onlyEquipment && ItemHelper.isEquipmentItem(this.entity.getItem())
               || ModTemplate.config.onlyRare && RarityHelper.rarityCheck(this.entity.getItem(), false)
               || ItemHelper.isItemInRegistryList(ModTemplate.config.whitelist, this.entity.getItem().getItem())
         )) {
         light = 15728640;
      }

      return light;
   }
}
