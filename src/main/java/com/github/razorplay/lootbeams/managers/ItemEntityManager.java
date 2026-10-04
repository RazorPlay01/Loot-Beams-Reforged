package com.github.razorplay.lootbeams.managers;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

public class ItemEntityManager {
   private static final Map<ItemStack, ItemEntity> stackToEntityMap = new WeakHashMap<>();

   public static void track(ItemEntity entity) {
      stackToEntityMap.put(entity.getItem(), entity);
   }

   public static ItemEntity getEntityForStack(ItemStack stack) {
      return stackToEntityMap.get(stack);
   }

   public static void untrack(ItemEntity entity) {
      stackToEntityMap.values().removeIf(e -> e == entity);
   }
}
