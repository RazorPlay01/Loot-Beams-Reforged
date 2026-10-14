package com.github.razorplay.lootbeams.screens.widgets;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.helpers.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import org.jetbrains.annotations.Nullable;
//? if >= 1.21.11 {
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
//?}

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

//? if <= 1.21.1 {
/*import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
*///?} else {
import net.minecraft.util.Util;
//?}

public class PresetTextFieldWidget extends AbstractWidget implements Renderable {
   private static final WidgetSprites TEXTURES = new WidgetSprites(
      ModTemplate.id("textures/gui/text_field/normal.png"), ModTemplate.id("textures/gui/text_field/highlighted.png")
   );
   private int verticalPadding = 4;
   private int horizontalPadding = 8;
   private final Font textRenderer;
   private String text = "";
   private int maxLength = 32;
   private boolean drawsBackground = true;
   private boolean focusUnlocked = true;
   private boolean editable = true;
   private int firstCharacterIndex;
   private int selectionStart;
   private int selectionEnd;
   private int editableColor = 14737632;
   private int uneditableColor = 7368816;
   @Nullable
   private String suggestion;
   @Nullable
   private Consumer<String> changedListener;
   private Predicate<String> textPredicate = Objects::nonNull;
   private BiFunction<String, Integer, FormattedCharSequence> renderTextProvider = (string, firstCharacterIndex) -> FormattedCharSequence.forward(string, Style.EMPTY);
   @Nullable
   private Component placeholder;
   private long lastSwitchFocusTime = Util.getMillis();

   public PresetTextFieldWidget(Font textRenderer, int width, int height, Component text) {
      this(textRenderer, 0, 0, width, height, text);
   }

   public PresetTextFieldWidget(Font textRenderer, int x, int y, int width, int height, Component text) {
      this(textRenderer, x, y, width, height, null, text);
   }

   public PresetTextFieldWidget(Font textRenderer, int x, int y, int width, int height, @Nullable EditBox copyFrom, Component text) {
      super(x, y, width, height, text);
      this.textRenderer = textRenderer;
      if (copyFrom != null) {
         this.setText(copyFrom.getValue());
      }
   }

   public void setChangedListener(Consumer<String> changedListener) {
      this.changedListener = changedListener;
   }

   public void setRenderTextProvider(BiFunction<String, Integer, FormattedCharSequence> renderTextProvider) {
      this.renderTextProvider = renderTextProvider;
   }

   protected MutableComponent createNarrationMessage() {
      Component text = this.getMessage();
      return Component.translatable("gui.narrate.editBox", new Object[]{text, this.text});
   }

   public void setText(String text) {
      if (this.textPredicate.test(text)) {
         if (text.length() > this.maxLength) {
            this.text = text.substring(0, this.maxLength);
         } else {
            this.text = text;
         }

         this.setCursorToEnd(false);
         this.setSelectionEnd(this.selectionStart);
         this.onChanged(text);
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int i = Math.min(this.selectionStart, this.selectionEnd);
      int j = Math.max(this.selectionStart, this.selectionEnd);
      return this.text.substring(i, j);
   }

   public void setTextPredicate(Predicate<String> textPredicate) {
      this.textPredicate = textPredicate;
   }

   public void write(String text) {
      int i = Math.min(this.selectionStart, this.selectionEnd);
      int j = Math.max(this.selectionStart, this.selectionEnd);
      int k = this.maxLength - this.text.length() - (i - j);
      if (k > 0) {
         String string = StringUtil.filterText(text);
         int l = string.length();
         if (k < l) {
            if (Character.isHighSurrogate(string.charAt(k - 1))) {
               k--;
            }

            string = string.substring(0, k);
            l = k;
         }

         String string2 = new StringBuilder(this.text).replace(i, j, string).toString();
         if (this.textPredicate.test(string2)) {
            this.text = string2;
            this.setSelectionStart(i + l);
            this.setSelectionEnd(this.selectionStart);
            this.onChanged(this.text);
         }
      }
   }

   private void onChanged(String newText) {
      if (this.changedListener != null) {
         this.changedListener.accept(newText);
      }
   }

   //? if <= 1.21.1 {
   /*private static boolean isControlDown() {
      return Screen.hasControlDown();
   }

   private static boolean isShiftDown() {
      return Screen.hasShiftDown();
   }
   *///?} else {
   private static boolean isControlDown() {
      return Minecraft.getInstance().hasControlDown();
   }

   private static boolean isShiftDown() {
      return Minecraft.getInstance().hasShiftDown();
   }
   //?}

   private void erase(int offset) {
      if (isControlDown()) {
         this.eraseWords(offset);
      } else {
         this.eraseCharacters(offset);
      }
   }

   public void eraseWords(int wordOffset) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.selectionStart) {
            this.write("");
         } else {
            this.eraseCharactersTo(this.getWordSkipPosition(wordOffset));
         }
      }
   }

   public void eraseCharacters(int characterOffset) {
      this.eraseCharactersTo(this.getCursorPosWithOffset(characterOffset));
   }

   public void eraseCharactersTo(int position) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.selectionStart) {
            this.write("");
         } else {
            int i = Math.min(position, this.selectionStart);
            int j = Math.max(position, this.selectionStart);
            if (i != j) {
               String string = new StringBuilder(this.text).delete(i, j).toString();
               if (this.textPredicate.test(string)) {
                  this.text = string;
                  this.setCursor(i, false);
               }
            }
         }
      }
   }

   public int getWordSkipPosition(int wordOffset) {
      return this.getWordSkipPosition(wordOffset, this.getCursor());
   }

   private int getWordSkipPosition(int wordOffset, int cursorPosition) {
      return this.getWordSkipPosition(wordOffset, cursorPosition, true);
   }

   private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
      int i = cursorPosition;
      boolean bl = wordOffset < 0;
      int j = Math.abs(wordOffset);

      for (int k = 0; k < j; k++) {
         if (!bl) {
            int l = this.text.length();
            i = this.text.indexOf(32, i);
            if (i == -1) {
               i = l;
            } else {
               while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
                  i++;
               }
            }
         } else {
            while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
               i--;
            }

            while (i > 0 && this.text.charAt(i - 1) != ' ') {
               i--;
            }
         }
      }

      return i;
   }

   public void moveCursor(int offset, boolean shiftKeyPressed) {
      this.setCursor(this.getCursorPosWithOffset(offset), shiftKeyPressed);
   }

   private int getCursorPosWithOffset(int offset) {
      return Util.offsetByCodepoints(this.text, this.selectionStart, offset);
   }

   public void setCursor(int cursor, boolean shiftKeyPressed) {
      this.setSelectionStart(cursor);
      if (!shiftKeyPressed) {
         this.setSelectionEnd(this.selectionStart);
      }

      this.onChanged(this.text);
   }

   public void setSelectionStart(int cursor) {
      this.selectionStart = Mth.clamp(cursor, 0, this.text.length());
      this.updateFirstCharacterIndex(this.selectionStart);
   }

   public void setCursorToStart(boolean shiftKeyPressed) {
      this.setCursor(0, shiftKeyPressed);
   }

   public void setCursorToEnd(boolean shiftKeyPressed) {
      this.setCursor(this.text.length(), shiftKeyPressed);
   }

   //? if <= 1.21.1 {
   /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.isActive() && this.isFocused()) {
         switch (keyCode) {
            case 259:
               if (this.editable) {
                  this.erase(-1);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (Screen.isSelectAll(keyCode)) {
                  this.setCursorToEnd(false);
                  this.setSelectionEnd(0);
                  return true;
               } else if (Screen.isCopy(keyCode)) {
                  Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                  return true;
               } else if (Screen.isPaste(keyCode)) {
                  if (this.isEditable()) {
                     this.write(Minecraft.getInstance().keyboardHandler.getClipboard());
                  }

                  return true;
               } else {
                  if (Screen.isCut(keyCode)) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                     if (this.isEditable()) {
                        this.write("");
                     }

                     return true;
                  }

                  return false;
               }
            case 261:
               if (this.editable) {
                  this.erase(1);
               }

               return true;
            case 262:
               if (Screen.hasControlDown()) {
                  this.setCursor(this.getWordSkipPosition(1), Screen.hasShiftDown());
               } else {
                  this.moveCursor(1, Screen.hasShiftDown());
               }

               return true;
            case 263:
               if (Screen.hasControlDown()) {
                  this.setCursor(this.getWordSkipPosition(-1), Screen.hasShiftDown());
               } else {
                  this.moveCursor(-1, Screen.hasShiftDown());
               }

               return true;
            case 268:
               this.setCursorToStart(Screen.hasShiftDown());
               return true;
            case 269:
               this.setCursorToEnd(Screen.hasShiftDown());
               return true;
         }
      } else {
         return false;
      }
   }
   *///?} else {
   public boolean keyPressed(KeyEvent event) {
      if (this.isActive() && this.isFocused()) {
         switch (event.input()) {
            case 259:
               if (this.editable) {
                  this.erase(-1);
               }

               return true;
            case 260:
            case 264:
            case 265:
            case 266:
            case 267:
            default:
               if (event.isSelectAll()) {
                  this.setCursorToEnd(false);
                  this.setSelectionEnd(0);
                  return true;
               } else if (event.isCopy()) {
                  Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                  return true;
               } else if (event.isPaste()) {
                  if (this.isEditable()) {
                     this.write(Minecraft.getInstance().keyboardHandler.getClipboard());
                  }

                  return true;
               } else {
                  if (event.isCut()) {
                     Minecraft.getInstance().keyboardHandler.setClipboard(this.getSelectedText());
                     if (this.isEditable()) {
                        this.write("");
                     }

                     return true;
                  }

                  return false;
               }
            case 261:
               if (this.editable) {
                  this.erase(1);
               }

               return true;
            case 262:
               if (isControlDown()) {
                  this.setCursor(this.getWordSkipPosition(1), isShiftDown());
               } else {
                  this.moveCursor(1, isShiftDown());
               }

               return true;
            case 263:
               if (isControlDown()) {
                  this.setCursor(this.getWordSkipPosition(-1), isShiftDown());
               } else {
                  this.moveCursor(-1, isShiftDown());
               }

               return true;
            case 268:
               this.setCursorToStart(isShiftDown());
               return true;
            case 269:
               this.setCursorToEnd(isShiftDown());
               return true;
         }
      }

      return false;
   }
   //?}

   /**
    * NOTA: el original llamaba a this.isActive() de forma recursiva (StackOverflowError en runtime),
    * por eso ahora delega en AbstractWidget.isActive().
    */
   public boolean isActive() {
      return super.isActive() && this.isFocused() && this.isEditable();
   }

   //? if <= 1.21.1 {
   /*public boolean charTyped(char chr, int modifiers) {
      if (!this.isActive()) {
         return false;
      }

      if (StringUtil.isAllowedChatCharacter(chr)) {
         if (this.editable) {
            this.write(Character.toString(chr));
         }

         return true;
      } else {
         return false;
      }
   }
   *///?} else {
   public boolean charTyped(CharacterEvent event) {
      if (!this.isActive()) {
         return false;
      }

      if (event.isAllowedChatCharacter()) {
         if (this.editable) {
            this.write(event.codepointAsString());
         }

         return true;
      } else {
         return false;
      }
   }
   //?}

   public void onClick(double mouseX, double mouseY) {
      int i = Mth.floor(mouseX) - this.getX();
      if (this.drawsBackground) {
         i -= this.horizontalPadding;
      }

      String string = this.textRenderer.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
      this.setCursor(this.textRenderer.plainSubstrByWidth(string, i).length() + this.firstCharacterIndex, isShiftDown());
   }

   public void playDownSound(SoundManager soundManager) {
   }

   //? if >= 26.1.2 {
   public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
   //?} else {
   /*public void renderWidget(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
   *///?}
      if (this.isVisible()) {
         if (this.drawsBackground()) {
            Identifier texture = TEXTURES.get(this.isActive(), this.isFocused());
            //? if <= 1.21.1 {
            /*RenderSystem.setShaderTexture(0, texture);
            *///?}
            RenderHelper.blit(context, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0, 0);
         }

         int i = this.editable ? this.editableColor : this.uneditableColor;
         int j = this.selectionStart - this.firstCharacterIndex;
         String string = this.textRenderer.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
         boolean bl = j >= 0 && j <= string.length();
         boolean bl2 = this.isFocused() && (Util.getMillis() - this.lastSwitchFocusTime) / 300L % 2L == 0L && bl;
         int k = this.drawsBackground ? this.getX() + this.horizontalPadding : this.getX();
         int l = this.drawsBackground ? this.getY() + (this.height - this.verticalPadding * 2) / 2 : this.getY();
         int m = k;
         int n = Mth.clamp(this.selectionEnd - this.firstCharacterIndex, 0, string.length());
         if (!string.isEmpty()) {
            String string2 = bl ? string.substring(0, j) : string;
            //? if <= 1.21.1 {
            /*m = context.drawString(this.textRenderer, this.renderTextProvider.apply(string2, this.firstCharacterIndex), k, l, i);
            *///?} else {
            FormattedCharSequence sequence = this.renderTextProvider.apply(string2, this.firstCharacterIndex);
            //? if >= 26.1.2 {
            context.text(this.textRenderer, sequence, k, l, i);
            //?} else {
            /*context.drawString(this.textRenderer, sequence, k, l, i);
            *///?}
            m = k + this.textRenderer.width(sequence);
            //?}
         }

         boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
         int o = m;
         if (!bl) {
            o = j > 0 ? k + this.width : k;
         } else if (bl3) {
            o = m - 1;
            m--;
         }

         if (!string.isEmpty() && bl && j < string.length()) {
            //? if >= 26.1.2 {
            context.text(this.textRenderer, this.renderTextProvider.apply(string.substring(j), this.selectionStart), m, l, i);
            //?} else {
            /*context.drawString(this.textRenderer, this.renderTextProvider.apply(string.substring(j), this.selectionStart), m, l, i);
            *///?}
         }

         if (this.placeholder != null && string.isEmpty() && !this.isFocused()) {
            //? if >= 26.1.2 {
            context.text(this.textRenderer, this.placeholder, m, l, i);
            //?} else {
            /*context.drawString(this.textRenderer, this.placeholder, m, l, i);
            *///?}
         }

         if (!bl3 && this.suggestion != null) {
            //? if >= 26.1.2 {
            context.text(this.textRenderer, this.suggestion, o - 1, l, -8355712);
            //?} else {
            /*context.drawString(this.textRenderer, this.suggestion, o - 1, l, -8355712);
            *///?}
         }

         if (bl2) {
            if (bl3) {
               //? if <= 1.21.1 {
               /*context.fill(RenderType.guiOverlay(), o, l - 1, o + 1, l + 1 + 9, -3092272);
               *///?} else {
               context.fill(o, l - 1, o + 1, l + 1 + 9, -3092272);
               //?}
            } else {
               //? if >= 26.1.2 {
               context.text(this.textRenderer, "_", o, l, i);
               //?} else {
               /*context.drawString(this.textRenderer, "_", o, l, i);
               *///?}
            }
         }

         if (n != j) {
            int p = k + this.textRenderer.width(string.substring(0, n));
            this.drawSelectionHighlight(context, o, l - 1, p - 1, l + 1 + 9);
         }
      }
   }

   private void drawSelectionHighlight(GuiGraphicsExtractor context, int x1, int y1, int x2, int y2) {
      if (x1 < x2) {
         int i = x1;
         x1 = x2;
         x2 = i;
      }

      if (y1 < y2) {
         int i = y1;
         y1 = y2;
         y2 = i;
      }

      if (x2 > this.getX() + this.width) {
         x2 = this.drawsBackground ? this.getX() + this.width - this.horizontalPadding : this.getX() + this.width;
      }

      if (x1 > this.getX() + this.width) {
         x1 = this.drawsBackground ? this.getX() + this.width - this.horizontalPadding : this.getX() + this.width;
      }

      //? if <= 1.21.1 {
      /*context.fill(RenderType.guiTextHighlight(), x1, y1, x2, y2, -16776961);
      *///?} else {
      context.fill(x1, y1, x2, y2, -16776961);
      //?}
   }

   public void setMaxLength(int maxLength) {
      this.maxLength = maxLength;
      if (this.text.length() > maxLength) {
         this.text = this.text.substring(0, maxLength);
         this.onChanged(this.text);
      }
   }

   private int getMaxLength() {
      return this.maxLength;
   }

   public int getCursor() {
      return this.selectionStart;
   }

   public boolean drawsBackground() {
      return this.drawsBackground;
   }

   public void setDrawsBackground(boolean drawsBackground) {
      this.drawsBackground = drawsBackground;
   }

   public void setEditableColor(int editableColor) {
      this.editableColor = editableColor;
   }

   public void setUneditableColor(int uneditableColor) {
      this.uneditableColor = uneditableColor;
   }

   public void setVerticalPadding(int padding) {
      this.verticalPadding = padding;
   }

   public void setHorizontalPadding(int padding) {
      this.horizontalPadding = padding;
   }

   public void setPadding(int vertical, int horizontal) {
      this.setVerticalPadding(vertical);
      this.setHorizontalPadding(horizontal);
   }

   public void setPadding(int padding) {
      this.setVerticalPadding(padding);
      this.setHorizontalPadding(padding);
   }

   public void setFocused(boolean focused) {
      if (this.focusUnlocked || focused) {
         super.setFocused(focused);
         if (focused) {
            this.lastSwitchFocusTime = Util.getMillis();
         }
      }
   }

   private boolean isEditable() {
      return this.editable;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   public int getInnerWidth() {
      return this.drawsBackground() ? this.width - this.horizontalPadding * 2 : this.width;
   }

   public void setSelectionEnd(int index) {
      this.selectionEnd = Mth.clamp(index, 0, this.text.length());
      this.updateFirstCharacterIndex(this.selectionEnd);
   }

   private void updateFirstCharacterIndex(int cursor) {
      if (this.textRenderer != null) {
         this.firstCharacterIndex = Math.min(this.firstCharacterIndex, this.text.length());
         int i = this.getInnerWidth();
         String string = this.textRenderer.plainSubstrByWidth(this.text.substring(this.firstCharacterIndex), i);
         int j = string.length() + this.firstCharacterIndex;
         if (cursor == this.firstCharacterIndex) {
            this.firstCharacterIndex = this.firstCharacterIndex - this.textRenderer.plainSubstrByWidth(this.text, i, true).length();
         }

         if (cursor > j) {
            this.firstCharacterIndex += cursor - j;
         } else if (cursor <= this.firstCharacterIndex) {
            this.firstCharacterIndex = this.firstCharacterIndex - (this.firstCharacterIndex - cursor);
         }

         this.firstCharacterIndex = Mth.clamp(this.firstCharacterIndex, 0, this.text.length());
      }
   }

   public void setFocusUnlocked(boolean focusUnlocked) {
      this.focusUnlocked = focusUnlocked;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public void setSuggestion(@Nullable String suggestion) {
      this.suggestion = suggestion;
   }

   public int getCharacterX(int index) {
      return index > this.text.length() ? this.getX() : this.getX() + this.textRenderer.width(this.text.substring(0, index));
   }

   public void updateWidgetNarration(NarrationElementOutput builder) {
      builder.add(NarratedElementType.TITLE, this.createNarrationMessage());
   }

   public void setPlaceholder(Component placeholder) {
      this.placeholder = placeholder;
   }
}
