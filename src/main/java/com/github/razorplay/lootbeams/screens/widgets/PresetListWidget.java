package com.github.razorplay.lootbeams.screens.widgets;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PresetListWidget extends ContainerObjectSelectionList<PresetListWidget.PresetEntry> {
   private PresetEntry selectedEntry;
   private boolean isScrolling = false;
   private double dragOffsetY;
   @Nullable
   private PresetEntry hoveredEntry;
   private static final ResourceLocation SCROLLER_TEXTURE = ModTemplate.id("textures/gui/scroller/scroller.png");
   private static final ResourceLocation SCROLLER_BACKGROUND_TEXTURE = ModTemplate.id("textures/gui/scroller/scroller_background.png");

   public PresetListWidget(Minecraft client, int width, int height, int y, int itemHeight) {
      super(client, width, height, y, itemHeight);
   }

   public void addEntries(Iterable<String> presetNames) {
      for (String presetName : presetNames) {
         PresetEntry entry = new PresetEntry(presetName, this, PresetButtonWidget.Type.Default);
         this.addEntry(entry);
         if (presetName.equals(ModTemplate.config.selectedPreset)) {
            this.selectedEntry = entry;
         }
      }
   }

   public void clearEntries() {
      super.clearEntries();
   }

   public PresetEntry addEntry(String name, PresetButtonWidget.Type buttonType) {
      PresetEntry entry = new PresetEntry(name, this, buttonType);
      this.addEntry(entry);
      return entry;
   }

   private int getScrollbarWidth() {
      return 6;
   }

   protected int getScrollbarPosition() {
      return this.getX() + this.getWidth() - this.getScrollbarWidth() + 4;
   }

   public int getRowWidth() {
      return this.getWidth() - this.getScrollbarWidth() - 3;
   }

   public int getRowLeft() {
      return this.getX() + 3;
   }

   protected int getRowTop(int index) {
      return this.getY() + 2 - (int)this.getScrollAmount() + index * this.itemHeight + this.headerHeight;
   }

   public int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - this.height + 2);
   }

   protected void renderListItems(GuiGraphics context, int mouseX, int mouseY, float delta) {
      int i = this.getRowLeft();
      int j = this.getRowWidth();
      int k = this.itemHeight - 2;
      int l = this.getItemCount();

      for (int m = 0; m < l; m++) {
         int n = this.getRowTop(m);
         int o = this.getRowBottom(m);
         if (o >= this.getY() && n <= this.getBottom()) {
            this.renderItem(context, mouseX, mouseY, delta, m, i, n, j, k);
         }
      }
   }

   protected void renderItem(GuiGraphics context, int mouseX, int mouseY, float delta, int index, int x, int y, int entryWidth, int entryHeight) {
      PresetEntry entry = (PresetEntry)this.getEntry(index);
      entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
      if (this.isSelectedItem(index)) {
         int i = this.isFocused() ? -1 : -8355712;
         this.renderSelection(context, y, entryWidth, entryHeight, i, -16777216);
      }

      entry.render(context, index, y, x, entryWidth, entryHeight, mouseX, mouseY, Objects.equals(this.hoveredEntry, entry), delta);
   }

   private void renderScroller(GuiGraphics context, int x, int y, int width, int height) {
      int topHeight = 6;
      int bottomHeight = 6;
      int middleHeight = 18;
      RenderHelper.blit(context, SCROLLER_TEXTURE, x, y, width, topHeight, 0, 0, width, topHeight);
      int remainingHeight = height - topHeight - bottomHeight;
      int middleRepeats = remainingHeight / middleHeight;
      int middleRemainder = remainingHeight % middleHeight;

      for (int i = 0; i < middleRepeats; i++) {
         RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + topHeight + i * middleHeight, width, middleHeight, 0, 7, width, middleHeight);
      }

      if (middleRemainder > 0) {
         RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + topHeight + middleRepeats * middleHeight, width, middleRemainder, 0, 7, width, middleRemainder);
      }

      RenderHelper.blit(context, SCROLLER_TEXTURE, x, y + height - bottomHeight, width, bottomHeight, 0, 26, width, bottomHeight);
   }

   public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
      this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? (PresetEntry)this.getEntryAtPosition(mouseX, mouseY) : null;
      this.renderListBackground(context);
      this.enableScissor(context);
      this.renderListItems(context, mouseX, mouseY, delta);
      context.disableScissor();
      this.renderListSeparators(context);
      if (this.scrollbarVisible()) {
         int i = this.getScrollbarPosition();
         int j = (int)((float)(this.height * this.height) / this.getMaxPosition());
         j = Mth.clamp(j, 32, this.height - 8);
         int k = (int)this.getScrollAmount() * (this.height - j) / this.getMaxScroll() + this.getY();
         if (k < this.getY()) {
            k = this.getY();
         }

         RenderSystem.enableBlend();
         RenderHelper.blit(context, SCROLLER_BACKGROUND_TEXTURE, i, this.getY() - 1, 6, this.getHeight() + 2, 0, 0);
         this.renderScroller(context, i, k - 1, 6, j + 2);
         RenderSystem.disableBlend();
      }

      this.renderDecorations(context, mouseX, mouseY);
      RenderSystem.disableBlend();
   }

   protected void renderListBackground(GuiGraphics context) {
   }

   protected void renderListSeparators(GuiGraphics context) {
   }

   public PresetEntry getSelectedEntry() {
      return this.selectedEntry;
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (!this.isScrolling) {
         return false;
      } else if (mouseY >= this.getY() && mouseY <= this.getBottom()) {
         double scrollRange = this.getMaxScroll();
         double scrollbarRange = this.getHeight();
         double scrollPercentage = (mouseY - this.getY() - this.dragOffsetY) / (scrollbarRange - this.getScrollbarHeight());
         this.setScrollAmount(scrollRange * scrollPercentage);
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         int scrollbarY = this.getScrollbarY();
         int scrollbarHeight = this.getScrollbarHeight();
         if (mouseX >= this.getScrollbarPosition()
            && mouseX <= this.getScrollbarPosition() + this.getScrollbarWidth()
            && mouseY >= scrollbarY
            && mouseY <= scrollbarY + scrollbarHeight) {
            this.dragOffsetY = mouseY - scrollbarY;
            this.isScrolling = true;
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.isScrolling = false;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   private int getScrollbarY() {
      int scrollbarHeight = this.getScrollbarHeight();
      int maxScroll = this.getMaxScroll();
      if (maxScroll <= 0) {
         return this.getY();
      }

      int scrollbarPositionY = (int)this.getScrollAmount() * (this.height - scrollbarHeight) / maxScroll + this.getY();
      if (scrollbarPositionY < this.getY()) {
         scrollbarPositionY = this.getY();
      }

      return scrollbarPositionY;
   }

   private int getScrollbarHeight() {
      int j = (int)((float)(this.height * this.height) / this.getMaxPosition());
      return Mth.clamp(j, 32, this.height - 8);
   }

   public static class PresetEntry extends Entry<PresetEntry> {
      private final String presetName;
      private final PresetButtonWidget button;
      private final PresetListWidget parentWidget;

      public PresetEntry(String presetName, PresetListWidget parentWidget, PresetButtonWidget.Type buttonType) {
         this.presetName = presetName;
         this.parentWidget = parentWidget;
         this.button = PresetButtonWidget.customBuilder(Component.literal(presetName), btn -> {}).type(buttonType).build();
      }

      public List<? extends NarratableEntry> narratables() {
         return Collections.emptyList();
      }

      public boolean mouseClicked(double mouseX, double mouseY, int button) {
         if (button == 0) {
            if (this.parentWidget.selectedEntry != this) {
               this.parentWidget.selectedEntry = this;
            } else {
               this.parentWidget.selectedEntry = null;
            }

            return true;
         } else {
            return false;
         }
      }

      public String getPresetName() {
         return this.presetName;
      }

      public List<? extends GuiEventListener> children() {
         return List.of();
      }

      public void render(
         GuiGraphics context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta
      ) {
         boolean currentEntrySelected = this.parentWidget.getSelectedEntry() == this;
         this.button.setRectangle(entryWidth, entryHeight, x, y);
         this.button.active = currentEntrySelected;
         this.button.render(context, mouseX, mouseY, tickDelta);
      }
   }
}
