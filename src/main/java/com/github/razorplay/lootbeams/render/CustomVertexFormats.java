package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import java.util.ArrayList;
import java.util.List;

public class CustomVertexFormats {
   public static VertexFormatElement COLOR1;
   public static VertexFormatElement UV_CENTER;
   public static VertexFormatElement UV_SIZE;
   public static VertexFormatElement CUSTOM_DATA;
   public static VertexFormat POSITION_TEX_COLOR0_COLOR1_CUSTOM;
   public static VertexFormat POSITION_TEX_COLOR0_COLOR1_CENTER;
   private static List<Integer> EMPTY_INDEXES = new ArrayList<>();

   private static int getAvailableIndex() {
      return EMPTY_INDEXES.removeFirst();
   }

   static {
      for (int i = 0; i < 32; i++) {
         VertexFormatElement element = VertexFormatElement.byId(i);
         if ((!IrisCompat.isIrisLoaded() || !IrisCompat.getVertexFormatsIndexes().contains(i)) && element == null) {
            EMPTY_INDEXES.add(i);
         }
      }

      COLOR1 = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      UV_CENTER = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      UV_SIZE = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      CUSTOM_DATA = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 4);
      POSITION_TEX_COLOR0_COLOR1_CUSTOM = VertexFormat.builder()
         .add("Position", VertexFormatElement.POSITION)
         .add("UV0", VertexFormatElement.UV0)
         .add("Color0", VertexFormatElement.COLOR)
         .add("Color1", COLOR1)
         .add("CustomData", CUSTOM_DATA)
         .build();
      POSITION_TEX_COLOR0_COLOR1_CENTER = VertexFormat.builder()
         .add("Position", VertexFormatElement.POSITION)
         .add("UV0", VertexFormatElement.UV0)
         .add("Color0", VertexFormatElement.COLOR)
         .add("Color1", COLOR1)
         .add("CenterUV", UV_CENTER)
         .add("SizeUV", UV_SIZE)
         .add("GradientBounds", CUSTOM_DATA)
         .build();
   }
}
