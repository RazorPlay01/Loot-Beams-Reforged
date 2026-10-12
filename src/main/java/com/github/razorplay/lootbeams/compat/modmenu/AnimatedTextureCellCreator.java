package com.github.razorplay.lootbeams.compat.modmenu;

import com.github.razorplay.lootbeams.helpers.RenderHelper;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.DefaultSelectionTopCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellCreator;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionCellElement;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry.SelectionTopCellElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;

import java.util.Optional;

import static com.github.razorplay.lootbeams.ModTemplate.MOD_ID;

public class AnimatedTextureCellCreator {
	public static SelectionTopCellElement<ParticleManager.ParticleTexture> topParticleCell(ParticleManager.ParticleTexture texture) {
		return new DefaultSelectionTopCellElement<ParticleManager.ParticleTexture>(
				texture, key -> ParticleManager.ParticleTexture.of(key, MOD_ID), i -> Component.literal(i.toString())
		) {
			public void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
				ParticleManager.ParticleTexture particleTexture = this.getValue();
				this.textFieldWidget.setX(x + 4);
				this.textFieldWidget.setY(y + 6);
				this.textFieldWidget.setWidth(width - 4 - 20);
				this.textFieldWidget.setEditable(this.getParent().isEditable());
				this.textFieldWidget.setTextColor(this.getPreferredTextColor());
				this.textFieldWidget.setValue(particleTexture.getDisplayName());
				this.textFieldWidget.moveCursorTo(0, false);
				this.textFieldWidget.render(graphics, mouseX, mouseY, delta);
				//? if <= 1.21.1 {
				RenderSystem.enableDepthTest();
				RenderSystem.enableBlend();
				//?}
				TextureAtlasSprite particleSprite = particleTexture.getSprite();
				Rect2i spriteRect = RenderHelper.getSpritePositionAndSize(particleSprite);
				RenderHelper.drawColored(
						graphics,
						particleSprite.atlasLocation(),
						x + width - 18,
						y + 2,
						16,
						16,
						spriteRect.getX(),
						spriteRect.getY(),
						spriteRect.getWidth(),
						spriteRect.getHeight(),
						particleTexture.isColored() ? "#808080" : "#bbbbbb"
				);
				//? if <= 1.21.1 {
				RenderSystem.disableBlend();
				RenderSystem.disableDepthTest();
				//?}
			}

			public void setValue(ParticleManager.ParticleTexture value) {
				this.value = value;
			}

			public ParticleManager.ParticleTexture getValue() {
				return this.value;
			}

			public Component getSearchTerm() {
				return Component.literal(this.value.getDisplayName());
			}

			public Optional<Component> getError() {
				return this.toObjectFunction.apply(this.value.path) != null
						? Optional.empty()
						: Optional.of(Component.literal("Invalid Value!"));
			}
		};
	}

	public static SelectionTopCellElement<GlowEffectManager.GlowEffectTexture> topGlowEffectCell(GlowEffectManager.GlowEffectTexture texture) {
		return new DefaultSelectionTopCellElement<GlowEffectManager.GlowEffectTexture>(
				texture, key -> GlowEffectManager.GlowEffectTexture.of(key, MOD_ID), i -> Component.literal(i.toString())
		) {
			public void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
				GlowEffectManager.GlowEffectTexture glowEffectTexture = this.getValue();
				this.textFieldWidget.setX(x + 4);
				this.textFieldWidget.setY(y + 6);
				this.textFieldWidget.setWidth(width - 4 - 20);
				this.textFieldWidget.setEditable(this.getParent().isEditable());
				this.textFieldWidget.setTextColor(this.getPreferredTextColor());
				this.textFieldWidget.setValue(glowEffectTexture.getDisplayName());
				this.textFieldWidget.moveCursorTo(0, false);
				this.textFieldWidget.render(graphics, mouseX, mouseY, delta);
				//? if <= 1.21.1 {
				RenderSystem.enableDepthTest();
				RenderSystem.enableBlend();
				//?}
				TextureAtlasSprite particleSprite = glowEffectTexture.getSprite();
				Rect2i spriteRect = RenderHelper.getSpritePositionAndSize(particleSprite);
				RenderHelper.drawColored(
						graphics,
						particleSprite.atlasLocation(),
						x + width - 18,
						y + 2,
						16,
						16,
						spriteRect.getX(),
						spriteRect.getY(),
						spriteRect.getWidth(),
						spriteRect.getHeight(),
						glowEffectTexture.isColored() ? "#808080" : "#bbbbbb"
				);
				//? if <= 1.21.1 {
				RenderSystem.disableBlend();
				RenderSystem.disableDepthTest();
				//?}
			}

			public void setValue(GlowEffectManager.GlowEffectTexture value) {
				this.value = value;
			}

			public GlowEffectManager.GlowEffectTexture getValue() {
				return this.value;
			}

			public Component getSearchTerm() {
				return Component.literal(this.value.getDisplayName());
			}

			public Optional<Component> getError() {
				return this.toObjectFunction.apply(this.value.path) != null
						? Optional.empty()
						: Optional.of(Component.literal("Invalid Value!"));
			}
		};
	}

	public static SelectionCellCreator<ParticleManager.ParticleTexture> selectionParticleCell(int cellHeight, int cellWidth, int maxItems) {
		return new DefaultSelectionCellCreator<>(i -> Component.literal(i.toString())) {
			public SelectionCellElement<ParticleManager.ParticleTexture> create(ParticleManager.ParticleTexture selection) {
				return new DefaultSelectionCellElement<>(selection, this.toTextFunction) {
					public void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
						this.rendering = true;
						this.x = x;
						this.y = y + 1;
						this.width = width;
						this.height = height;
						boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
						if (b) {
							graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
						}

						graphics.drawString(
								Minecraft.getInstance().font,
								Component.literal(selection.getDisplayName()).getVisualOrderText(),
								x + 4,
								y + 6,
								b ? 16777215 : 8947848
						);
						//? if <= 1.21.1 {
						RenderSystem.enableDepthTest();
						RenderSystem.enableBlend();
						//?}
						TextureAtlasSprite particleSprite = ParticleManager.ParticleTexture.getAtlasTexture().getSprite(selection.id);
						Rect2i spriteRect = RenderHelper.getSpritePositionAndSize(particleSprite);
						RenderHelper.drawColored(
								graphics,
								particleSprite.atlasLocation(),
								x + width - 24,
								y + 2,
								16,
								16,
								spriteRect.getX(),
								spriteRect.getY(),
								spriteRect.getWidth(),
								spriteRect.getHeight(),
								selection.isColored() ? "#808080" : "#bbbbbb"
						);
						//? if <= 1.21.1 {
						RenderSystem.disableBlend();
						RenderSystem.disableDepthTest();
						//?}
					}
				};
			}

			public int getCellHeight() {
				return cellHeight;
			}

			public int getCellWidth() {
				return cellWidth;
			}

			public int getDropBoxMaxHeight() {
				return this.getCellHeight() * maxItems;
			}
		};
	}

	public static SelectionCellCreator<GlowEffectManager.GlowEffectTexture> selectionGlowEffectCell(int cellHeight, int cellWidth, int maxItems) {
		return new DefaultSelectionCellCreator<GlowEffectManager.GlowEffectTexture>(i -> Component.literal(i.toString())) {
			public SelectionCellElement<GlowEffectManager.GlowEffectTexture> create(GlowEffectManager.GlowEffectTexture selection) {
				return new DefaultSelectionCellElement<GlowEffectManager.GlowEffectTexture>(selection, this.toTextFunction) {
					public void render(GuiGraphics graphics, int mouseX, int mouseY, int x, int y, int width, int height, float delta) {
						this.rendering = true;
						this.x = x;
						this.y = y + 1;
						this.width = width;
						this.height = height;
						boolean b = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
						if (b) {
							graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, -15132391);
						}

						graphics.drawString(
								Minecraft.getInstance().font,
								Component.literal(selection.getDisplayName()).getVisualOrderText(),
								x + 4,
								y + 6,
								b ? 16777215 : 8947848
						);
						//? if <= 1.21.1 {
						RenderSystem.enableDepthTest();
						RenderSystem.enableBlend();
						//?}
						TextureAtlasSprite particleSprite = selection.getSpriteAtlasTexture().getSprite(selection.id);
						Rect2i spriteRect = RenderHelper.getSpritePositionAndSize(particleSprite);
						RenderHelper.drawColored(
								graphics,
								particleSprite.atlasLocation(),
								x + width - 24,
								y + 2,
								16,
								16,
								spriteRect.getX(),
								spriteRect.getY(),
								spriteRect.getWidth(),
								spriteRect.getHeight(),
								selection.isColored() ? "#808080" : "#bbbbbb"
						);
						//? if <= 1.21.1 {
						RenderSystem.disableBlend();
						RenderSystem.disableDepthTest();
						//?}
					}
				};
			}

			public int getCellHeight() {
				return cellHeight;
			}

			public int getCellWidth() {
				return cellWidth;
			}

			public int getDropBoxMaxHeight() {
				return this.getCellHeight() * maxItems;
			}
		};
	}
}
