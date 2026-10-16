package com.github.razorplay.lootbeams.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.BiConsumer;
//? if >= 26.1.2 {
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
*///?}
//? <= 1.21.1 {
/*import net.minecraft.client.renderer.RenderType;
*///?} else {
import net.minecraft.client.renderer.rendertype.RenderType;
//?}

/**
 * Abstraccion del destino de geometria de LootBeams.
 * <p>
 * En 1.21.x envuelve un {@code MultiBufferSource.BufferSource}: los vertices se acumulan por
 * {@link RenderType} y se vuelcan con {@code endBatch()}.
 * <p>
 * A partir de 26.2 el motor sustituye esa API por "submit nodes": la geometria se entrega por capa
 * con {@link #submit} y la ejecuta el {@code SubmitNodeCollector} del frame. Para que el codigo de
 * los renderers no cambie entre versiones, {@link #submit} mantiene la misma firma en ambas.
 */
public final class LootBeamBuffer {
   //? if >= 26.1.2 {
   private final SubmitNodeCollector collector;

   private LootBeamBuffer(SubmitNodeCollector collector) {
      this.collector = collector;
   }

   public static LootBeamBuffer of(SubmitNodeCollector collector) {
      return new LootBeamBuffer(collector);
   }

   public SubmitNodeCollector collector() {
      return this.collector;
   }

   public boolean isValid() {
      return this.collector != null;
   }

   // 26.2: somete la geometria de una capa al collector del frame. El lambda recibe el consumidor y
   // un PoseStack cuyo ultimo estado es la pose capturada en el momento del submit, para que los
   // helpers del mod (que leen stack.last()) funcionen igual que en 1.21.x.
   public void submit(RenderType layer, PoseStack stack, BiConsumer<VertexConsumer, PoseStack> geometry) {
      if (this.collector != null && layer != null && stack != null) {
         this.collector.submitCustomGeometry(stack, layer, (pose, consumer) -> {
            PoseStack captured = new PoseStack();
            captured.last().pose().set(pose.pose());
            captured.last().normal().set(pose.normal());
            geometry.accept(consumer, captured);
         });
      }
   }

   // 26.2: somete texto al collector del frame (sustituye a Font#drawInBatch*). El orden de los
   // enteros es el de SubmitNodeCollector#submitText: lightCoords, color, backgroundColor,
   // outlineColor.
   public void submitText(
      PoseStack stack,
      float x,
      float y,
      FormattedCharSequence text,
      boolean dropShadow,
      Font.DisplayMode displayMode,
      int lightCoords,
      int color,
      int backgroundColor,
      int outlineColor
   ) {
      if (this.collector != null && stack != null) {
         this.collector.submitText(stack, x, y, text, dropShadow, displayMode, lightCoords, color, backgroundColor, outlineColor);
      }
   }

   // 26.2: no existen batches que volcar; se mantiene para no ramificar el codigo llamante.
   public void endBatch() {
   }
   //?} else {
   /*private final MultiBufferSource.BufferSource source;

   private LootBeamBuffer(MultiBufferSource.BufferSource source) {
      this.source = source;
   }

   public static LootBeamBuffer of(MultiBufferSource.BufferSource source) {
      return new LootBeamBuffer(source);
   }

   public MultiBufferSource.BufferSource source() {
      return this.source;
   }

   public boolean isValid() {
      return this.source != null;
   }

   /^* 1.21.x: consumidor compartido por RenderType (los vertices se vuelcan en endBatch). ^/
   public VertexConsumer getBuffer(RenderType layer) {
      return this.source.getBuffer(layer);
   }

   /^* 1.21.x: dibuja de inmediato en el buffer de la capa (equivalente al flujo con submit). ^/
   public void submit(RenderType layer, PoseStack stack, java.util.function.BiConsumer<VertexConsumer, PoseStack> geometry) {
      if (this.source != null && layer != null) {
         geometry.accept(this.source.getBuffer(layer), stack);
      }
   }

   public void endBatch() {
      this.source.endBatch();
   }
   *///?}
}
