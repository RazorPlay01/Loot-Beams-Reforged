package com.github.razorplay.lootbeams.screens;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.RenderHelper;
import com.github.razorplay.lootbeams.managers.PresetManager;
import com.github.razorplay.lootbeams.screens.widgets.PresetButtonWidget;
import com.github.razorplay.lootbeams.screens.widgets.PresetListWidget;
import com.github.razorplay.lootbeams.screens.widgets.PresetTextFieldWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class LootBeamsPresetManagerScreen extends Screen {
   private static final Identifier MODAL_TEXTURE = ModTemplate.id("textures/gui/screen/modal.png");
   private static final int BACKGROUND_WIDTH = 184;
   private static final int BACKGROUND_HEIGHT = 152;
   private final Screen parent;
   private PresetTextFieldWidget presetNameField;
   private PresetButtonWidget savePresetButton;
   private PresetButtonWidget loadPresetButton;
   private PresetListWidget presetListWidget;
   private PresetListWidget.PresetEntry addEntry;

   public LootBeamsPresetManagerScreen(Screen parent) {
      super(Component.translatable("lootbeams.screen.presets.title"));
      this.parent = parent;
   }

   private void reinitPresetListWidget() {
      PresetManager.getPresetFiles();
      this.presetListWidget.clearEntries();
      this.presetListWidget.addEntries(PresetManager.getPresetNames());
      this.addEntry = this.presetListWidget.addEntry("+", PresetButtonWidget.Type.Add);
   }

   protected void init() {
      int topPos = this.height / 2 - BACKGROUND_HEIGHT / 2;
      int bottomPos = this.height / 2 + BACKGROUND_HEIGHT / 2;
      int leftPos = this.width / 2 - BACKGROUND_WIDTH / 2;
      int rightPos = this.width / 2 + BACKGROUND_WIDTH / 2;
      int topHeaderPos = topPos + 29;
      this.presetListWidget = new PresetListWidget(this.minecraft, 84, 118, topHeaderPos + 2, 20);
      this.presetListWidget.setPosition(leftPos + 5, topHeaderPos + 2);
      this.reinitPresetListWidget();
      this.addRenderableWidget(this.presetListWidget);
      this.presetNameField = new PresetTextFieldWidget(
         this.font, rightPos - 84, topHeaderPos + 10, 78, 22, Component.translatable("lootbeams.screen.presets.presetNameTextField")
      );
      this.presetNameField.setPlaceholder(Component.translatable("lootbeams.screen.presets.presetNameTextField"));
      this.presetNameField.setMaxLength(64);
      this.addWidget(this.presetNameField);
      this.presetNameField.setChangedListener(value -> {
         if (this.savePresetButton != null) {
            this.savePresetButton.active = !value.trim().isEmpty();
         }
      });
      this.savePresetButton = PresetButtonWidget.customBuilder(
            Component.translatable("lootbeams.screen.presets.button.save"),
            btn -> {
               String presetName = this.presetNameField.getText().trim();
               if (presetName.isEmpty()) {
                  try {
                     showToast(Component.translatable("lootbeams.notification.savePreset.notEnteredPresetName"));
                  } catch (Exception e) {
                     e.printStackTrace();
                  }
               } else {
                  PresetManager.savePreset(PresetManager.getPresetFileName(presetName));
                  this.reinitPresetListWidget();
                  btn.active = false;
               }
            }
         )
         .position(rightPos - 83, topHeaderPos + 47)
         .size(76, 19)
         .build();
      this.savePresetButton.active = false;
      this.loadPresetButton = PresetButtonWidget.customBuilder(Component.translatable("lootbeams.screen.presets.button.load"), btn -> {
         PresetListWidget.PresetEntry presetEntry = this.presetListWidget.getSelectedEntry();
         if (presetEntry != null) {
            String presetName = presetEntry.getPresetName();
            PresetManager.loadAndApplyPreset(presetName);
         }
      }).position(rightPos - 83, topHeaderPos + 47).size(76, 19).build();
      this.loadPresetButton.active = false;
      PresetButtonWidget cancelButton = PresetButtonWidget.customBuilder(
            Component.translatable("lootbeams.screen.presets.button.cancel"), btn -> {
               //? if >= 26.2 {
               /*Minecraft.getInstance().gui.setScreen(this.parent);
               *///?} else {
               Minecraft.getInstance().setScreen(this.parent);
               //?}
            })
         .position(rightPos - 83, bottomPos - 27)
         .size(76, 19)
         .type(PresetButtonWidget.Type.Cancel)
         .build();
      this.addWidget(this.savePresetButton);
      this.addWidget(this.loadPresetButton);
      this.addRenderableWidget(cancelButton);
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void showToast(Component message) {
      //? if >= 26.2 {
      /*SystemToast.add(
         this.minecraft.gui.toastManager(), SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.nullToEmpty("LootBeams"), message
      );
      *///?} else {
      //? if <= 1.21.1 {
      /*this.minecraft
         .getToasts()
         .addToast(
            SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.nullToEmpty("LootBeams"), message)
         );
      *///?} else {
      this.minecraft
         .getToastManager()
         .addToast(
            SystemToast.multiline(this.minecraft, SystemToast.SystemToastId.WORLD_ACCESS_FAILURE, Component.nullToEmpty("LootBeams"), message)
         );
      //?}
      //?}
   }

   //? if <= 1.21.1 {
   /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      return keyCode != 258 && keyCode != 264 && keyCode != 265 ? super.keyPressed(keyCode, scanCode, modifiers) : false;
   }
   *///?} else {
   public boolean keyPressed(net.minecraft.client.input.KeyEvent event) {
      return event.input() != 258 && event.input() != 264 && event.input() != 265 ? super.keyPressed(event) : false;
   }
   //?}

   //? if >= 26.1.2 {
   public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      super.extractRenderState(context, mouseX, mouseY, delta);
      if (this.presetListWidget.getSelectedEntry() == this.addEntry) {
         this.presetNameField.extractRenderState(context, mouseX, mouseY, delta);
         this.savePresetButton.extractRenderState(context, mouseX, mouseY, delta);
      } else {
         PresetListWidget.PresetEntry presetEntry = this.presetListWidget.getSelectedEntry();
         this.loadPresetButton.active = presetEntry != null && !presetEntry.getPresetName().equals(ModTemplate.config.selectedPreset);
         this.loadPresetButton.extractRenderState(context, mouseX, mouseY, delta);
      }
   }

   public void extractBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      super.extractBackground(context, mouseX, mouseY, delta);
      int i = (this.width - BACKGROUND_WIDTH) / 2;
      int j = (this.height - BACKGROUND_HEIGHT) / 2;
      RenderHelper.blit(context, MODAL_TEXTURE, i, j, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0, 0);
   }
   //?} else {
   /*public void render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      this.renderBackground(context, mouseX, mouseY, delta);
      super.render(context, mouseX, mouseY, delta);
      if (this.presetListWidget.getSelectedEntry() == this.addEntry) {
         this.presetNameField.render(context, mouseX, mouseY, delta);
         this.savePresetButton.render(context, mouseX, mouseY, delta);
      } else {
         PresetListWidget.PresetEntry presetEntry = this.presetListWidget.getSelectedEntry();
         this.loadPresetButton.active = presetEntry != null && !presetEntry.getPresetName().equals(ModTemplate.config.selectedPreset);
         this.loadPresetButton.render(context, mouseX, mouseY, delta);
      }
   }

   public void renderBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
      super.renderBackground(context, mouseX, mouseY, delta);
      int i = (this.width - BACKGROUND_WIDTH) / 2;
      int j = (this.height - BACKGROUND_HEIGHT) / 2;
      RenderHelper.blit(context, MODAL_TEXTURE, i, j, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 0, 0);
   }
   *///?}

   public void onClose() {
      //? if >= 26.2 {
      /*this.minecraft.gui.setScreen(this.parent);
      *///?} else {
      this.minecraft.setScreen(this.parent);
      //?}
   }
}
