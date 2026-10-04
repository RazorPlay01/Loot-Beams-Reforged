package com.github.razorplay.lootbeams.renderers;

import com.github.razorplay.lootbeams.compat.iceberg.IcebergCompat;
import com.github.razorplay.lootbeams.compat.legendarytooltips.LegendaryTooltipsCompat;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.features.CustomRarity;
import com.github.razorplay.lootbeams.helpers.RarityHelper;
import com.github.razorplay.lootbeams.helpers.TextHelper;
import com.github.razorplay.lootbeams.helpers.ViewHelper;
import com.github.razorplay.lootbeams.managers.TooltipManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public class TooltipRenderer {
   public static void renderItemTooltip(GuiGraphics context, Font font, ItemStack itemStack, int x, int y) {
      Minecraft client = Minecraft.getInstance();
      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
      Player player = client.player;
      Font textRenderer = client.font;
      CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
      List<Component> tooltipLines = TooltipManager.getTooltipWithStackSize(itemStack);
      Optional<TooltipComponent> tooltipData = itemStack.getTooltipImage();
      Component longestLine = TooltipManager.getTooltipLongestLine(itemStack, tooltipLines);
      int halfWidth = textRenderer.width(longestLine) / 2;
      boolean alwaysRenderRarityOnItem = RarityHelper.alwaysHasRarity(itemStack);
      boolean legendaryTooltipsLoaded = LegendaryTooltipsCompat.isLegendaryTooltipsLoaded();
      x -= 12;
      if (itemConfig.screenTooltipsRequireCrouch && !player.isCrouching()) {
         String rarity = RarityHelper.getRarity(itemStack);
         Style textStyle = itemStack.getDisplayName().getStyle();
         Component t1 = tooltipLines.get(0);
         Component t2 = null;
         tooltipLines = List.of(t1);
         if (itemConfig.renderItemRarity || alwaysRenderRarityOnItem) {
            t2 = Component.literal(rarity).setStyle(textStyle);
            tooltipLines = List.of(t1, t2);
         }

         TooltipManager.OffsetContainer tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
         halfWidth = tooltipOffset.getHalfTooltipWidth();
         if ((itemConfig.renderItemRarity || alwaysRenderRarityOnItem) && t2 != null) {
            if (customRarity != null) {
               t2 = CustomRarity.toText(customRarity);
               tooltipLines = List.of(t1, t2);
            }

            if (legendaryTooltipsLoaded && textRenderer.width(t2) < halfWidth * 2) {
               tooltipLines = List.of(t1, TextHelper.centeredLine(t2, textRenderer, halfWidth * 2));
            }
         }

         tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
         halfWidth = tooltipOffset.getHalfTooltipWidth();
         x -= tooltipOffset.getOffset();
         if ((itemConfig.renderItemRarity || alwaysRenderRarityOnItem) && !itemConfig.combineNameAndRarity && !legendaryTooltipsLoaded) {
            int rarityLineHalfWidth = textRenderer.width(rarity) / 2;
            context.renderTooltip(textRenderer, List.of(tooltipLines.get(0)), tooltipData, x, y);
            context.renderTooltip(textRenderer, List.of(tooltipLines.get(1)), tooltipData, x + halfWidth - rarityLineHalfWidth, y + 9 * 2);
         } else {
            context.renderTooltip(textRenderer, tooltipLines, tooltipData, x, y);
         }
      } else {
         TooltipManager.OffsetContainer tooltipOffset = alignTooltipHorizontal(itemStack, tooltipLines, tooltipData, context, x, y, textRenderer, halfWidth);
         x -= tooltipOffset.getOffset();
         context.renderTooltip(textRenderer, tooltipLines, tooltipData, x, y);
      }
   }

   public static void renderWorldPositionTooltip(GuiGraphics drawContext, Entity entity, ItemStack itemStack, float tickDelta) {
      if (itemStack != null && !itemStack.isEmpty()) {
         if (!TooltipManager.canRenderTooltips(itemStack)) {
            return;
         }

         Minecraft client = Minecraft.getInstance();
         Player player = client.player;
         Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
         int windowScaledWidth = drawContext.guiWidth();
         int windowScaledHeight = drawContext.guiHeight();
         int x = windowScaledWidth / 2;
         int y = windowScaledHeight / 2;
         Font textRenderer = client.font;
         List<Component> tooltipLines = TooltipManager.getTooltipWithStackSize(itemStack);
         if (itemConfig.worldspaceTooltips) {
            Vec3 tooltipShiftedPosition = new Vec3(
               0.0, Math.min(1.0, player.distanceToSqr(entity) * 0.025) + itemConfig.nametagYOffset + tooltipLines.size() / 100.0F, 0.0
            );
            Vec3 tooltipWorldPos = entity.position().add(tooltipShiftedPosition);
            Vector3f desiredScreenSpacePos = ViewHelper.worldToScreenSpace(tooltipWorldPos, tickDelta);
            desiredScreenSpacePos = new Vector3f(
               Mth.clamp(desiredScreenSpacePos.x(), 0.0F, windowScaledWidth),
               Mth.clamp(desiredScreenSpacePos.y(), 0.0F, windowScaledHeight - 9 * tooltipLines.size()),
               desiredScreenSpacePos.z()
            );
            x = (int)desiredScreenSpacePos.x();
            y = (int)desiredScreenSpacePos.y();
         }

         int guiScale = client.options.guiScale().get();
         drawContext.renderTooltip(textRenderer, itemStack, x, y);
         client.options.guiScale().set(guiScale);
      }
   }

   private static TooltipManager.OffsetContainer alignTooltipHorizontal(
      ItemStack itemStack,
      List<Component> tooltipLines,
      Optional<TooltipComponent> tooltipData,
      GuiGraphics context,
      int x,
      int y,
      Font textRenderer,
      int halfWidth
   ) {
      boolean legendaryTooltipsLoaded = LegendaryTooltipsCompat.isLegendaryTooltipsLoaded();
      if (legendaryTooltipsLoaded) {
         List<ClientTooltipComponent> tooltipComponents = IcebergCompat.getTooltipComponents(
            itemStack, tooltipLines, tooltipData, x, context.guiWidth(), context.guiHeight(), null, textRenderer, context.guiWidth()
         );
         Rect2i tooltipRect = IcebergCompat.getTooltipRect(
            itemStack,
            context,
            DefaultTooltipPositioner.INSTANCE,
            tooltipComponents,
            x,
            y,
            context.guiWidth(),
            context.guiHeight(),
            context.guiWidth(),
            textRenderer,
            0,
            true
         );
         int halfTooltipWidth = tooltipRect.getWidth() / 2;
         int offset = halfTooltipWidth - 1;
         return new TooltipManager.OffsetContainer(offset, halfTooltipWidth);
      } else {
         return new TooltipManager.OffsetContainer(halfWidth, halfWidth);
      }
   }
}
