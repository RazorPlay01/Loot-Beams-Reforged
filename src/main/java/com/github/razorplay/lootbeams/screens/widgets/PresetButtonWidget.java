package com.github.razorplay.lootbeams.screens.widgets;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PresetButtonWidget extends Button {
   private static final WidgetSprites DEFAULT_TEXTURES = new WidgetSprites(
      ModTemplate.id("textures/gui/preset_button/default/normal.png"),
      ModTemplate.id("textures/gui/preset_button/default/disabled.png"),
      ModTemplate.id("textures/gui/preset_button/default/highlighted.png")
   );
   private static final WidgetSprites ADD_TEXTURES = new WidgetSprites(
      ModTemplate.id("textures/gui/preset_button/add/normal.png"),
      ModTemplate.id("textures/gui/preset_button/add/disabled.png"),
      ModTemplate.id("textures/gui/preset_button/add/highlighted.png")
   );
   private static final WidgetSprites CANCEL_TEXTURES = new WidgetSprites(
      ModTemplate.id("textures/gui/preset_button/cancel/normal.png"),
      ModTemplate.id("textures/gui/preset_button/cancel/disabled.png"),
      ModTemplate.id("textures/gui/preset_button/cancel/highlighted.png")
   );
   private Type type;
   private final int TEXT_MARGIN_X = 8;

   protected PresetButtonWidget(
      int x, int y, int width, int height, Component message, OnPress onPress, CreateNarration narrationSupplier, Type type
   ) {
      super(x, y, width, height, message, onPress, narrationSupplier);
      if (type != null) {
         this.type = type;
      } else {
         this.type = Type.Default;
      }
   }

   private WidgetSprites getButtonTextures() {
      if (this.type == Type.Add) {
         return ADD_TEXTURES;
      } else {
         return this.type == Type.Cancel ? CANCEL_TEXTURES : DEFAULT_TEXTURES;
      }
   }

   public void renderString(GuiGraphics context, Font textRenderer, int color) {
      this.renderScrollingString(context, textRenderer, TEXT_MARGIN_X, color);
   }

   protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
      Minecraft minecraftClient = Minecraft.getInstance();
      context.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.enableDepthTest();
      ResourceLocation texture = this.getButtonTextures().get(this.active, this.isHoveredOrFocused());
      RenderHelper.blit(context, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0);
      RenderSystem.setShaderTexture(0, texture);
      context.blit(texture, this.getX(), this.getY(), 0, 0.0F, 0.0F, this.getWidth(), this.getHeight(), 76, 19);
      context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.active ? 16777215 : 10526880;
      this.renderString(context, minecraftClient.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   public static Builder customBuilder(Component message, OnPress onPress) {
      return new Builder(message, onPress);
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final Component message;
      private final OnPress onPress;
      @Nullable
      private Tooltip tooltip;
      private Type type = Type.Default;
      private int x;
      private int y;
      private int width = 150;
      private int height = 20;
      private CreateNarration narrationSupplier = PresetButtonWidget.DEFAULT_NARRATION;

      public Builder(Component message, OnPress onPress) {
         this.message = message;
         this.onPress = onPress;
      }

      public Builder position(int x, int y) {
         this.x = x;
         this.y = y;
         return this;
      }

      public Builder width(int width) {
         this.width = width;
         return this;
      }

      public Builder size(int width, int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public Builder dimensions(int x, int y, int width, int height) {
         return this.position(x, y).size(width, height);
      }

      public Builder tooltip(@Nullable Tooltip tooltip) {
         this.tooltip = tooltip;
         return this;
      }

      public Builder narrationSupplier(CreateNarration narrationSupplier) {
         this.narrationSupplier = narrationSupplier;
         return this;
      }

      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      public PresetButtonWidget build() {
         PresetButtonWidget buttonWidget = new PresetButtonWidget(
            this.x, this.y, this.width, this.height, this.message, this.onPress, this.narrationSupplier, this.type
         );
         buttonWidget.setTooltip(this.tooltip);
         return buttonWidget;
      }
   }

   public enum Type {
      Default,
      Add,
      Cancel;
   }
}
