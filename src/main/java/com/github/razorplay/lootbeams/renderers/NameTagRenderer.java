package com.github.razorplay.lootbeams.renderers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.helpers.RarityHelper;
import com.github.razorplay.lootbeams.helpers.TargetHelper;
import com.github.razorplay.lootbeams.helpers.TextColorHelper;
import com.github.razorplay.lootbeams.helpers.VersionCompatHelper;
import com.github.razorplay.lootbeams.managers.TooltipManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.List;

public class NameTagRenderer {
    public static void renderNameTags(
            LootBeamBuffer buffer,
            PoseStack matrixStack,
            ItemEntity itemEntity,
            Configuration itemConfig,
            TextColor color,
            float fadeAlpha,
            float currentGroundTime,
            long worldtime,
            float pticks
    ) {
        if (itemConfig.renderNametags) {
            if (!itemConfig.advancedTooltips) {
                if (Minecraft.getInstance().player.isCrouching()
                        || itemConfig.renderNametagsOnlook
                        && TargetHelper.isLookingAt(Minecraft.getInstance().player, itemEntity, itemConfig.nametagLookSensitivity)) {
                    ItemStack itemStack = itemEntity.getItem();
                    float foregroundAlpha = itemConfig.nametagTextAlpha;
                    float backgroundAlpha = itemConfig.nametagBackgroundAlpha;
                    double yOffset = itemConfig.nametagYOffset;
                    int foregroundColor = color.getValue() & 16777215 | (int) (255.0F * foregroundAlpha) << 24;
                    int backgroundColor = color.getValue() & 16777215 | (int) (255.0F * backgroundAlpha) << 24;
                    matrixStack.pushPose();
                    matrixStack.translate(0.0, Math.min(1.0, Minecraft.getInstance().player.distanceToSqr(itemEntity) * 0.025) + yOffset, 0.0);
                    Quaternionf entityRotationQuaternion = VersionCompatHelper.cameraRotation(Minecraft.getInstance());
                    VersionCompatHelper.mulPose(matrixStack, entityRotationQuaternion);
                    VersionCompatHelper.mulPose(matrixStack, new Quaternionf(0.0, Math.toRadians(90.0), 0.0, 0.0));
                    float nametagScale = itemConfig.nametagScale;
                    float nametagScaleCompensation = 0.25F;
                    matrixStack.scale(
                            -0.02F * nametagScale * nametagScaleCompensation,
                            -0.02F * nametagScale * nametagScaleCompensation,
                            0.02F * nametagScale * nametagScaleCompensation
                    );
                    Font fontrenderer = Minecraft.getInstance().font;
                    String itemName = StringUtil.stripColor(itemStack.getHoverName().getString());
                    if (itemConfig.renderStackcount) {
                        int count = itemStack.getCount();
                        if (count > 1) {
                            itemName = itemName + " x" + count;
                        }
                    }

                    matrixStack.translate(0.0F, 0.0F, -10.0F);
                    renderText(itemStack, itemConfig, fontrenderer, matrixStack, buffer, itemName, foregroundColor, backgroundColor, backgroundAlpha);
                    buffer.endBatch();
                    boolean alwaysRenderRarityOnItem = RarityHelper.alwaysHasRarity(itemStack);
                    if (itemConfig.renderItemRarity || alwaysRenderRarityOnItem) {
                        renderRarity(itemStack, itemConfig, foregroundAlpha, backgroundAlpha, fontrenderer, matrixStack, buffer, alwaysRenderRarityOnItem);
                        buffer.endBatch();
                    }

                    matrixStack.popPose();
                }
            }
        }
    }

    private static void renderText(
            ItemStack itemStack,
            Configuration itemConfig,
            Font fontRenderer,
            PoseStack stack,
            LootBeamBuffer buffer,
            String text,
            int foregroundColor,
            int backgroundColor,
            float backgroundAlpha
    ) {
        if (itemConfig.borders) {
            float w = -fontRenderer.width(text) / 2.0F;
            int bg = new Color(0, 0, 0, (int) (255.0F * backgroundAlpha)).getRGB();
            Component orderedText = Component.nullToEmpty(text);
            //? if >= 26.1.2 {
            buffer.submitText(stack, w, 0.0F, orderedText.getVisualOrderText(), false, Font.DisplayMode.NORMAL, 15728880, foregroundColor, bg, bg);
            //?} else {
            /*fontRenderer.drawInBatch8xOutline(orderedText.getVisualOrderText(), w, 0.0F, foregroundColor, bg, stack.last().pose(), buffer.source(), 15728880);
            *///?}
        } else {
            //? if >= 26.1.2 {
            buffer.submitText(stack, (float) (-fontRenderer.width(text) / 2.0), 0.0F, Component.nullToEmpty(text).getVisualOrderText(), false, Font.DisplayMode.NORMAL, 15728864, foregroundColor, backgroundColor, backgroundColor);
            //?} else {
            /*fontRenderer.drawInBatch(
                    text,
                    (float) (-fontRenderer.width(text) / 2.0),
                    0.0F,
                    foregroundColor,
                    false,
                    stack.last().pose(),
                    buffer.source(),
                    Font.DisplayMode.NORMAL,
                    backgroundColor,
                    15728864
            );
            *///?}
        }
    }

    private static void renderRarity(
            ItemStack itemStack,
            Configuration itemConfig,
            float foregroundAlpha,
            float backgroundAlpha,
            Font fontRenderer,
            PoseStack stack,
            LootBeamBuffer buffer,
            boolean alwaysRenderRarityOnItem
    ) {
        stack.translate(0.0, 10.0, 0.0);
        stack.scale(0.75F, 0.75F, 0.75F);
        List<Component> tooltip = TooltipManager.getTooltipFromCache(itemStack);
        if (!tooltip.isEmpty()) {
            if (tooltip.size() > 1) {
                Component tooltipRarity = tooltip.get(1);
                String rarityString = tooltipRarity.getString();
                if (itemConfig.customRarities.contains(rarityString) || alwaysRenderRarityOnItem) {
                    TextColor rarityColor = ModTemplate.config.whiteRarities
                            ? TextColor.fromLegacyFormat(ChatFormatting.WHITE)
                            : TextColorHelper.getRawColor(tooltipRarity);
                    int foregroundColor = rarityColor.getValue() & 16777215 | (int) (255.0F * foregroundAlpha) << 24;
                    int backgroundColor = rarityColor.getValue() & 16777215 | (int) (255.0F * backgroundAlpha) << 24;
                    renderText(itemStack, itemConfig, fontRenderer, stack, buffer, rarityString, foregroundColor, backgroundColor, backgroundAlpha);
                    return;
                }
            }

            if (itemConfig.vanillaRarities || alwaysRenderRarityOnItem) {
                String rarity = RarityHelper.getRarity(itemStack);
                TextColor rarityColor = ModTemplate.config.whiteRarities ? TextColor.fromLegacyFormat(ChatFormatting.WHITE) : TextColorHelper.getItemColor(itemStack);
                float R = (rarityColor.getValue() >> 16 & 0xFF) / 255.0F;
                float G = (rarityColor.getValue() >> 8 & 0xFF) / 255.0F;
                float B = (rarityColor.getValue() & 0xFF) / 255.0F;
                int foregroundColor = new Color(R, G, B, (int) foregroundAlpha).getRGB();
                int backgroundColor = new Color(R, G, B, (int) backgroundAlpha).getRGB();
                renderText(itemStack, itemConfig, fontRenderer, stack, buffer, rarity, foregroundColor, backgroundColor, backgroundAlpha);
            }
        }
    }
}
