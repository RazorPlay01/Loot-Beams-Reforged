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
    /**
     * 1.21.1 y anteriores: {@code GuiGraphics#renderTooltip(Font, ItemStack, int, int)}
     * dibujaba el tooltip de forma inmediata, asi que cancelarlo a HEAD evita el vanilla.
     */
    //? if <= 1.21.1 {
    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    protected void drawItemTooltipHandler(Font font, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        lootbeams$renderCustomTooltip(font, itemStack, x, y, ci);
    }
    //?}

    /**
     * 1.21.11 y posteriores: {@code renderTooltip(Font, ItemStack, int, int)} fue sustituido por
     * {@code setTooltipForNextFrame(Font, ItemStack, int, int)}, con los mismos parametros,
     * invocadores y significado, asi que cancelarla a HEAD reproduce el comportamiento anterior.
     */
    //? if >= 1.21.11 {
    /*@Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"), cancellable = true)
    private void lootbeams$onItemTooltipNew(Font font, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        lootbeams$renderCustomTooltip(font, itemStack, x, y, ci);
    }
    *///?}

    /** Si el item pasa el filtro, pinta el tooltip propio y cancela el vanilla. */
    private void lootbeams$renderCustomTooltip(Font font, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        if (itemStack == null || itemStack.isEmpty() || !TooltipManager.canRenderTooltips(itemStack)) {
            return;
        }
        GuiGraphics ctx = (GuiGraphics) (Object) this;
        TooltipRenderer.renderItemTooltip(ctx, font, itemStack, x, y);
        ci.cancel();
    }
}
