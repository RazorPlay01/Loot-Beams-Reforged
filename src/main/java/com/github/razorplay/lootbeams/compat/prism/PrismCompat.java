package com.github.razorplay.lootbeams.compat.prism;

import com.github.razorplay.lootbeams.ModTemplate;
import net.minecraft.network.chat.TextColor;

import java.util.List;

public class PrismCompat {
   public static boolean isPrismLoaded() {
      return ModTemplate.xplat().isModLoaded("prism");
   }

   public static TextColor parseColor(Object value) {
      try {
         return (TextColor)Class.forName("com.anthonyhilyard.prism.util.ConfigHelper").getMethod("parseColor", Object.class).invoke(null, value);
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }
   }

   public static TextColor applyModifiers(List<String> modifiers, TextColor value) {
      try {
         return (TextColor)Class.forName("com.anthonyhilyard.prism.util.ConfigHelper")
            .getMethod("applyModifiers", List.class, TextColor.class)
            .invoke(null, modifiers, value);
      } catch (Exception ex) {
         ex.printStackTrace();
         return null;
      }
   }
}
