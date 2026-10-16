package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

/**
 * Adaptadores para APIs de cliente que cambiaron de firma entre 1.21.1 y 1.21.11.
 * <p>
 * Existe porque el arbol {@code src/} se compila directamente para la version activa, asi que los
 * bloques de loader que no coinciden con ella siguen comentados con comentario de bloque y no
 * admiten condicionales de version anidados (Stonecutter no los ve dentro del comentario). Sacando
 * el split de version a una clase compartida, los archivos de loader solo ven una API estable.
 */
public final class VersionCompatHelper {
   private VersionCompatHelper() {
   }

   /**
    * {@code Camera#getYRot()} (1.21.1) paso a {@code Camera#yRot()} en 1.21.11.
    */
   public static float cameraYaw(net.minecraft.client.Camera camera) {
      //? if <= 1.21.1 {
      /*return camera.getYRot();
      *///?} else {
      return camera.yRot();
      //?}
   }

   /**
    * 26.2 renombro {@code GameRenderer#getMainCamera()} a {@code GameRenderer#mainCamera()}.
    */
   public static net.minecraft.client.Camera mainCamera(net.minecraft.client.renderer.GameRenderer gameRenderer) {
      //? if >= 26.2 {
      /*return gameRenderer.mainCamera();
      *///?} else {
      return gameRenderer.getMainCamera();
      //?}
   }

   /**
    * 26.3 renombro {@code InputConstants.Type.KEYSYM} a {@code KEYBOARD} (solo quedan KEYBOARD y MOUSE).
    * Se centraliza aqui para no necesitar condicionales anidados en los call sites de cada loader.
    */
   //? if >= 26.3 {
   /*public static final InputConstants.Type KEY_TYPE = InputConstants.Type.KEYBOARD;
   *///?} else {
   public static final InputConstants.Type KEY_TYPE = InputConstants.Type.KEYSYM;
   //?}

   /**
    * 1.21.11 cambio la categoria del keybind de {@code String} a {@code KeyMapping.Category}.
    */
   public static KeyMapping registerKeyMapping(String translationKey, InputConstants.Type type, int keyCode, String legacyCategory, String categoryPath) {
      //? if <= 1.21.1 {
      /*return new KeyMapping(translationKey, type, keyCode, legacyCategory);
      *///?} else {
      return new KeyMapping(translationKey, type, keyCode, KeyMapping.Category.register(ModTemplate.id(categoryPath)));
      //?}
   }

   /**
    * 1.21.11 elimino {@code Player#sendSystemMessage} (equivalente local:
    * {@code Player#displayClientMessage(Component, boolean)}), pero 26.1.2 lo volvio a anadir.
    */
   public static void sendMessage(Player player, Component message) {
      //? if <= 1.21.1 {
      /*player.sendSystemMessage(message);
      *///?} else {
      //? if >= 26.1.2 {
      player.sendSystemMessage(message);
      //?} else {
      /*player.displayClientMessage(message, false);
      *///?}
      //?}
   }
}
