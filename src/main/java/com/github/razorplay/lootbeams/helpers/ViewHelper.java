package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.mojang.blaze3d.platform.Window;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
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

   public static void cancelBobView(Vector3f position, float partialTicks) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.options.bobView().get() && mc.getCameraEntity() instanceof Player) {
         Player player = (Player)mc.getCameraEntity();
         float playerStep = player.walkDist - player.walkDistO;
         float stepSize = -(player.walkDist + playerStep * partialTicks);
         float viewBob = Mth.lerp(partialTicks, player.oBob, player.bob);
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
      Camera camera = mc.gameRenderer.getMainCamera();
      Vec3 cameraPosition = camera.getPosition();
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
      float screenSize = window.getGuiScaledHeight()
         / 2.0F
         / position.z()
         / (float)Math.tan(Math.toRadians(mc.gameRenderer.getFov(camera, partialTicks, true) / 2.0));
      position.mul(-screenSize, screenSize, 1.0F);
      position.add(window.getGuiScaledWidth() / 2.0F, window.getGuiScaledHeight() / 2.0F, 0.0F);
      return position;
   }
}
