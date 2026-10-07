package com.github.razorplay.lootbeams.managers;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TooltipManager {
   public static final Map<ItemStack, List<Component>> TOOLTIP_CACHE = new ConcurrentHashMap<>();

   public static void onResourcesReload() {
      TOOLTIP_CACHE.clear();
   }

   public static void onEntityLoad(ItemEntity item, Level world) {
      ItemStack itemStack = item.getItem();
      if (!TOOLTIP_CACHE.containsKey(itemStack)) {
         TOOLTIP_CACHE.put(itemStack, getItemStackTooltip(item.getItem()));
      }
   }

   public static void onEntityUnload(ItemEntity item, Level world) {
      TOOLTIP_CACHE.remove(item.getItem());
   }

   public static List<Component> getTooltipFromCache(ItemStack itemStack) {
      if (!TOOLTIP_CACHE.containsKey(itemStack)) {
         List<Component> tooltip = getItemStackTooltip(itemStack);
         TOOLTIP_CACHE.put(itemStack, tooltip);
         return tooltip;
      } else {
         return TOOLTIP_CACHE.get(itemStack);
      }
   }

   public static List<Component> getItemStackTooltip(ItemStack itemStack) {
      return Screen.getTooltipFromItem(Minecraft.getInstance(), itemStack);
   }

   public static Component getTooltipLongestLine(ItemStack itemStack, List<Component> tooltipLines) {
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      Minecraft client = Minecraft.getInstance();
      Font textRenderer = client.font;
      Component longestLine = tooltipLines.get(0);
      if (itemConfig.screenTooltipsRequireCrouch && client.player.isCrouching()) {
         longestLine = tooltipLines.stream().max((a, b) -> textRenderer.width(a) - textRenderer.width(b)).orElse(tooltipLines.get(0));
      }

      return longestLine;
   }

   public static List<Component> getTooltipWithStackSize(ItemStack itemStack) {
      List<Component> tooltipLines = getTooltipFromCache(itemStack);
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      int count = itemStack.getCount();
      if (itemConfig.renderStackcount && count > 1) {
         String countString = " x" + count;
         Component firstLine = tooltipLines.get(0);
         String itemNameString = firstLine.getString();
         if (!itemNameString.contains(countString)) {
            Component newLineWithCount = Component.literal(itemNameString + countString).setStyle(firstLine.getStyle());
            tooltipLines.set(0, newLineWithCount);
         }
      }

      return tooltipLines;
   }

   public static boolean canRenderTooltips(ItemStack itemStack) {
      Minecraft client = Minecraft.getInstance();
      boolean isChatScreen = client.screen instanceof ChatScreen;
      boolean inGame = client.screen == null;
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      return itemConfig.advancedTooltips && (inGame || isChatScreen);
   }

   public static class OffsetContainer {
      private final int offset;
      private final int halfTooltipWidth;

      public OffsetContainer(int offset, int halfTooltipWidth) {
         this.offset = offset;
         this.halfTooltipWidth = halfTooltipWidth;
      }

      public int getOffset() {
         return this.offset;
      }

      public int getHalfTooltipWidth() {
         return this.halfTooltipWidth;
      }

      public int getTooltipWidth() {
         return this.halfTooltipWidth * 2;
      }
   }
}
