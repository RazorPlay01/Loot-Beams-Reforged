package com.github.razorplay.lootbeams.compat.legendarytooltips;

import com.github.razorplay.lootbeams.ModTemplate;
import net.minecraft.world.item.ItemStack;

public class LegendaryTooltipsCompat {
   public static boolean isLegendaryTooltipsLoaded() {
      return ModTemplate.xplat().isModLoaded("legendarytooltips");
   }

   public static boolean showModelForItem(ItemStack itemStack) {
      try {
         return (Boolean)Class.forName("com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig")
            .getMethod("showModelForItem", ItemStack.class)
            .invoke(null, itemStack);
      } catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }
   }
}
