package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.extensions.LootbeamsBufferBuilder;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BufferBuilder.class)
public abstract class BufferBuilderMixin implements LootbeamsBufferBuilder {
   @Shadow
   protected abstract long beginElement(VertexFormatElement var1);

   @Override
   public LootbeamsBufferBuilder color1(int red, int green, int blue, int alpha) {
      long l = this.beginElement(CustomVertexFormats.COLOR1);
      if (l != -1L) {
         MemoryUtil.memPutByte(l, (byte)red);
         MemoryUtil.memPutByte(l + 1L, (byte)green);
         MemoryUtil.memPutByte(l + 2L, (byte)blue);
         MemoryUtil.memPutByte(l + 3L, (byte)alpha);
      }

      return this;
   }

   @Override
   public LootbeamsBufferBuilder uvCenter(float u, float v) {
      long l = this.beginElement(CustomVertexFormats.UV_CENTER);
      if (l != -1L) {
         MemoryUtil.memPutFloat(l, u);
         MemoryUtil.memPutFloat(l + 4L, v);
      }

      return this;
   }

   @Override
   public LootbeamsBufferBuilder uvSize(float w, float h) {
      long l = this.beginElement(CustomVertexFormats.UV_SIZE);
      if (l != -1L) {
         MemoryUtil.memPutFloat(l, w);
         MemoryUtil.memPutFloat(l + 4L, h);
      }

      return this;
   }

   @Override
   public LootbeamsBufferBuilder shortCustomData(float data0, float data1) {
      long l = this.beginElement(CustomVertexFormats.CUSTOM_DATA);
      if (l != -1L) {
         MemoryUtil.memPutFloat(l, data0);
         MemoryUtil.memPutFloat(l + 4L, data1);
         MemoryUtil.memPutFloat(l + 8L, 0.0F);
         MemoryUtil.memPutFloat(l + 12L, 0.0F);
      }

      return this;
   }

   @Override
   public LootbeamsBufferBuilder longCustomData(float data0, float data1, float data2, float data3) {
      long l = this.beginElement(CustomVertexFormats.CUSTOM_DATA);
      if (l != -1L) {
         MemoryUtil.memPutFloat(l, data0);
         MemoryUtil.memPutFloat(l + 4L, data1);
         MemoryUtil.memPutFloat(l + 8L, data2);
         MemoryUtil.memPutFloat(l + 12L, data3);
      }

      return this;
   }
}
