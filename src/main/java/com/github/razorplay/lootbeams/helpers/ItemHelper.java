package com.github.razorplay.lootbeams.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

import java.util.Arrays;
import java.util.List;

public class ItemHelper {
   public static boolean isItemInRegistryList(List<String> registryNames, Item item) {
      if (registryNames.size() > 0) {
         for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
            if (!id.contains(":") && BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(id)) {
               return true;
            }

            Identifier itemResource = Identifier.tryParse(id);
            if (itemResource != null && BuiltInRegistries.ITEM.get(itemResource).asItem() == item.asItem()) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isEquipmentItem(ItemStack itemStack) {
      for (Class<? extends Item> item : Arrays.asList(
         SwordItem.class,
         DiggerItem.class,
         ArmorItem.class,
         ShieldItem.class,
         ProjectileWeaponItem.class,
         TridentItem.class,
         ArrowItem.class,
         FishingRodItem.class,
         FlintAndSteelItem.class,
         ShearsItem.class,
         BrushItem.class,
         ElytraItem.class
      )) {
         if (item.isAssignableFrom(itemStack.getItem().getClass())) {
            return true;
         }
      }

      return false;
   }
}
