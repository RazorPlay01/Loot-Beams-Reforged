package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.helpers.TextColorHelper;
import com.github.razorplay.lootbeams.managers.ItemEntityManager;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
   @Inject(
      method = "render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void onRenderItem(
      ItemStack itemStack,
      ItemDisplayContext transformationMode,
      boolean leftHanded,
      PoseStack matrices,
      MultiBufferSource vertexConsumers,
      int light,
      int overlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      if (itemConfig.itemsGlow) {
         ItemEntity itemEntity = ItemEntityManager.getEntityForStack(itemStack);
         if (!RenderManager.isRendering(itemEntity)) {
            RenderManager.setRendering(itemEntity, true);
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            ItemRenderer itemRenderer = client.getItemRenderer();
            if (itemEntity != null && itemConfig.itemsGlow) {
               HitResult hitResult = client.level
                  .clip(new ClipContext(player.getEyePosition(1.0F), itemEntity.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
               if (!itemStack.isEmpty()
                  && hitResult.getType() != HitResult.Type.BLOCK
                  && !(player.distanceToSqr(itemEntity) > itemConfig.renderDistance)) {
                  OutlineBufferSource outlineProvider = client.renderBuffers().outlineBufferSource();
                  TextColor textColor = TextColorHelper.getItemColor(itemStack);
                  if (textColor != null) {
                     int color = textColor.getValue();
                     int r = color >> 16 & 0xFF;
                     int g = color >> 8 & 0xFF;
                     int b = color & 0xFF;
                     outlineProvider.setColor(r, g, b, 255);
                  }

                  itemRenderer.render(itemStack, transformationMode, leftHanded, matrices, outlineProvider, light, overlay, model);
                  RenderManager.addOnRenderEnd(this.getClass(), ctx -> {
                     if (itemConfig.itemsGlow) {
                        Minecraft.getInstance().levelRenderer.renderBuffers.outlineBufferSource().endOutlineBatch();
                        if (Minecraft.getInstance().levelRenderer.entityEffect != null) {
                           Minecraft.getInstance().levelRenderer.entityEffect.process((float)Minecraft.getInstance().getFrameTimeNs());
                        }

                        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                     }
                  });
               } else {
                  itemRenderer.render(itemStack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model);
               }

               RenderManager.setRendering(itemEntity, false);
               ci.cancel();
            }
         }
      }
   }
}
