package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.managers.TooltipManager;
import com.github.razorplay.lootbeams.renderers.TooltipRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public class DrawContextMixin {
    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    protected void drawItemTooltipHandler(Font font, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (TooltipManager.canRenderTooltips(itemStack)) {
            GuiGraphics ctx = (GuiGraphics) (Object) this;
            TooltipRenderer.renderItemTooltip(ctx, font, itemStack, x, y);
            ci.cancel();
        }
    }
}
