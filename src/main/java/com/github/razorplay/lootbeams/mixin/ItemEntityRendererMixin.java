package com.github.razorplay.lootbeams.mixin;

//? if <= 1.21.1 {
/*import com.github.razorplay.lootbeams.managers.ItemEntityManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
   @Inject(
      method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At("HEAD")
   )
   private void onRenderItemEntity(ItemEntity itemEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci) {
      ItemEntityManager.track(itemEntity);
   }

   @Inject(
      method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
      at = @At("RETURN")
   )
   private void onRenderItemEntityEnd(
      ItemEntity itemEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, CallbackInfo ci
   ) {
      ItemEntityManager.untrack(itemEntity);
   }
}
*///?} else {
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.helpers.TextColorHelper;
import com.github.razorplay.lootbeams.helpers.ViewHelper;
import com.github.razorplay.lootbeams.managers.ItemEntityManager;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.github.razorplay.lootbeams.render.BeamRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.21.11: el render de entidades se divide en {@code extractRenderState} (con acceso a la entidad) y
 * {@code submit}, y el contorno coloreado es nativo ({@code EntityRenderState#outlineColor}), asi que
 * el resplandor del item ya no necesita {@code OutlineBufferSource} + {@code ItemRenderer#render}.
 */
@Mixin(ItemEntityRenderer.class)
public class ItemEntityRendererMixin {
   /** 0xF000F0 = bloque + cielo a maxima intensidad, el mismo valor que usa vanilla para el brillo. */
   @Unique
   private static final int LOOTBEAMS$FULLBRIGHT = 15728640;

   @Inject(method = "extractRenderState", at = @At("TAIL"))
   private void lootbeams$extractItemGlow(ItemEntity itemEntity, ItemEntityRenderState state, float partialTick, CallbackInfo ci) {
      // Registro stack -> entidad; el untrack ya no es necesario (WeakHashMap purga solo).
      ItemEntityManager.track(itemEntity);

      ItemStack itemStack = itemEntity.getItem();
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      Player player = Minecraft.getInstance().player;
      if (player == null || player.distanceTo(itemEntity) > itemConfig.renderDistance) {
         return;
      }

      if (!ViewHelper.shouldRenderOnItem(itemStack)) {
         return;
      }

      // Resplandor del item: contorno con el color de la rareza + luz a tope.
      if (itemConfig.itemsGlow) {
         TextColor textColor = TextColorHelper.getItemColor(itemStack);
         if (textColor != null) {
            // Si no hay color de rareza no se toca el outline vanilla (equipos, efecto Glowing...).
            state.outlineColor = ARGB.opaque(textColor.getValue());
         }

         state.lightCoords = LOOTBEAMS$FULLBRIGHT;
      }

      // Haz de luz: se registra aqui (fase de extraccion) y se dibuja en la pasada AFTER_WEATHER.
      if (itemEntity.onGround()) {
         RenderManager.addRenderAfterWeather((stack, consumer) -> {
            stack.pushPose();
            stack.translate(itemEntity.position().x(), itemEntity.position().y(), itemEntity.position().z());
            BeamRender.render(stack, consumer, itemEntity, itemEntity.level().getGameTime(), partialTick);
            stack.popPose();
         });
      }
   }
}
//?}
