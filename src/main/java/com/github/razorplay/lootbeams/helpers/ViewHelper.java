package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Axis;
import com.github.razorplay.lootbeams.mixin.GameRendererMixin;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {
import net.minecraft.client.entity.ClientAvatarState;
//?}
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ViewHelper {
   public static boolean shouldRenderOnItem(ItemStack itemStack) {
      boolean shouldRender = false;
      if (ModTemplate.config.allItems) {
         shouldRender = true;
      } else {
         if (ModTemplate.config.onlyEquipment) {
            shouldRender = ItemHelper.isEquipmentItem(itemStack);
         }

         if (ModTemplate.config.onlyRare) {
            shouldRender = RarityHelper.rarityCheck(itemStack, shouldRender);
         }

         if (ItemHelper.isItemInRegistryList(ModTemplate.config.whitelist, itemStack.getItem())) {
            shouldRender = true;
         }
      }

      if (ItemHelper.isItemInRegistryList(ModTemplate.config.blacklist, itemStack.getItem())) {
         shouldRender = false;
      }

      return shouldRender;
   }

   //? if >= 26.2 {
   /*public static boolean shouldRenderCrosshair(Minecraft client) {
      return ModTemplate.config.renderTooltipsInThirdPersonView
         ? (
               client.options.getCameraType() == CameraType.THIRD_PERSON_FRONT
                  || client.options.getCameraType() == CameraType.THIRD_PERSON_BACK
                  || client.options.getCameraType().isFirstPerson()
            )
            && !client.gui.hud.isHidden()
         : client.options.getCameraType().isFirstPerson() && !client.gui.hud.isHidden();
   }
   *///?} else {
   public static boolean shouldRenderCrosshair(Minecraft client) {
      return ModTemplate.config.renderTooltipsInThirdPersonView
         ? (
               client.options.getCameraType() == CameraType.THIRD_PERSON_FRONT
                  || client.options.getCameraType() == CameraType.THIRD_PERSON_BACK
                  || client.options.getCameraType().isFirstPerson()
            )
            && !client.options.hideGui
         : client.options.getCameraType().isFirstPerson() && !client.options.hideGui;
   }
   //?}

   public static void cancelBobView(Vector3f position, float partialTicks) {
      Minecraft mc = Minecraft.getInstance();
      //? if <= 1.21.1 {
      /*if (mc.options.bobView().get() && mc.getCameraEntity() instanceof Player) {
         Player player = (Player)mc.getCameraEntity();
         float playerStep = player.walkDist - player.walkDistO;
         float stepSize = -(player.walkDist + playerStep * partialTicks);
         float viewBob = Mth.lerp(partialTicks, player.oBob, player.bob);
      *///?} else {
      if (mc.options.bobView().get() && mc.getCameraEntity() instanceof AbstractClientPlayer player) {
         ClientAvatarState avatar = player.avatarState();
         float stepSize = avatar.getBackwardsInterpolatedWalkDistance(partialTicks);
         float viewBob = avatar.getInterpolatedBob(partialTicks);
      //?}
         Quaternionf bobXRotation = Axis.XP
            .rotationDegrees(Math.abs(Mth.cos(stepSize * (float) Math.PI - 0.2F) * viewBob) * 5.0F);
         Quaternionf bobZRotation = Axis.ZP.rotationDegrees(Mth.sin(stepSize * (float) Math.PI) * viewBob * 3.0F);
         bobXRotation.conjugate();
         bobZRotation.conjugate();
         bobXRotation.transform(position);
         bobZRotation.transform(position);
         position.add(
            -Mth.sin(stepSize * (float) Math.PI) * viewBob * 0.5F,
            Math.abs(Mth.cos(stepSize * (float) Math.PI) * viewBob),
            0.0F
         );
      }
   }

   public static Vector3f worldToScreenSpace(Vec3 pos, float partialTicks) {
      Minecraft mc = Minecraft.getInstance();
      //? if >= 26.2 {
      /*Camera camera = mc.gameRenderer.mainCamera();
      *///?} else {
      Camera camera = mc.gameRenderer.getMainCamera();
      //?}
      //? if <= 1.21.1 {
      /*Vec3 cameraPosition = camera.getPosition();
      *///?} else {
      Vec3 cameraPosition = camera.position();
      //?}
      Vector3f position = new Vector3f(
         (float)(cameraPosition.x - pos.x),
         (float)(cameraPosition.y - pos.y),
         (float)(cameraPosition.z - pos.z)
      );
      Quaternionf cameraRotation = camera.rotation();
      cameraRotation.conjugate();
      cameraRotation.transform(position);
      cancelBobView(position, partialTicks);
      Window window = mc.getWindow();
      //? if <= 1.21.1 {
      /*double fov = mc.gameRenderer.getFov(camera, partialTicks, true);
      *///?} elif >= 26.1.2 {
      // 26.1.2 elimino GameRenderer#getFov (el efectivo vive en CameraRenderState#hudFov, que
      // no es accesible desde aqui); se usa el ajuste de FOV como aproximacion.
      float fov = (float)mc.options.fov().get();
      //?} else {
      /*float fov = ((GameRendererMixin)(Object) mc.gameRenderer).lootbeams$getFov(camera, partialTicks, true);
      *///?}
      float screenSize = window.getGuiScaledHeight()
         / 2.0F
         / position.z()
         / (float)Math.tan(Math.toRadians(fov / 2.0));
      position.mul(-screenSize, screenSize, 1.0F);
      position.add(window.getGuiScaledWidth() / 2.0F, window.getGuiScaledHeight() / 2.0F, 0.0F);
      return position;
   }
}
