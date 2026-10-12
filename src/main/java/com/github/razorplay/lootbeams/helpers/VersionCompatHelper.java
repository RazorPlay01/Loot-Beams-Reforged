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
    * 1.21.11 cambio la categoria del keybind de {@code String} a {@link KeyMapping.Category}.
    */
   public static KeyMapping registerKeyMapping(String translationKey, InputConstants.Type type, int keyCode, String legacyCategory, String categoryPath) {
      //? if <= 1.21.1 {
      return new KeyMapping(translationKey, type, keyCode, legacyCategory);
      //?} else {
      /*return new KeyMapping(translationKey, type, keyCode, KeyMapping.Category.register(ModTemplate.id(categoryPath)));
      *///?}
   }

   /**
    * 1.21.11 elimino {@code Player#sendSystemMessage}; el equivalente local es
    * {@link Player#displayClientMessage(Component, boolean)}.
    */
   public static void sendMessage(Player player, Component message) {
      //? if <= 1.21.1 {
      player.sendSystemMessage(message);
      //?} else {
      /*player.displayClientMessage(message, false);
      *///?}
   }
}
