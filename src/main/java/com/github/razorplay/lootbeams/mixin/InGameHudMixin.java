package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.events.RenderEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
   // 26.1.2: el hook se engancha a Gui#extractRenderState(GuiGraphicsExtractor, DeltaTracker).
   // 26.2+: extractRenderState(DeltaTracker, boolean, boolean) ya no recibe extractor, asi que el
   // hook del HUD del mod queda desactivado (metodo sin @Inject: valido en todas las versiones).
   //? if >= 26.2 {
   public void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
   }
   //?} elif >= 26.1.2 {
   /*@Inject(method = "extractRenderState", at = @At("TAIL"))
   public void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
      RenderEvents.HUD.invoker().onHud(drawContext, tickCounter);
   }
   *///?} else {
   /*@Inject(method = "render", at = @At("TAIL"))
   public void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter, CallbackInfo callbackInfo) {
      RenderEvents.HUD.invoker().onHud(drawContext, tickCounter);
   }
   *///?}
}