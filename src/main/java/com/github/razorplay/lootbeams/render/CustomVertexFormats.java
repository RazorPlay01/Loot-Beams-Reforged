package com.github.razorplay.lootbeams.render;

import com.github.razorplay.lootbeams.compat.iris.IrisCompat;
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormatElement;
*///?} elif >= 26.2 {
/*import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
*///?} else {
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
//?}

import java.util.ArrayList;
import java.util.List;

public class CustomVertexFormats {
   //? if >= 26.2 {
   
   /*// 26.2 sustituye el registro global de VertexFormatElement (register/byId/Type/Usage) por
   // VertexFormat.builder(stepRate) + addAttribute(nombre, GpuFormat). Los nombres semanticos se
   // mantienen identicos para que los shaders del mod sigan leyendo los mismos atributos.
   public static VertexFormat POSITION_TEX_COLOR0_COLOR1_CUSTOM;
   public static VertexFormat POSITION_TEX_COLOR0_COLOR1_CENTER;

   static {
      POSITION_TEX_COLOR0_COLOR1_CUSTOM = VertexFormat.builder(0)
         .addAttribute("Position", GpuFormat.RGB32_FLOAT)
         .addAttribute("UV0", GpuFormat.RG32_FLOAT)
         .addAttribute("Color0", GpuFormat.RGBA8_UNORM)
         .addAttribute("Color1", GpuFormat.RGBA8_UNORM)
         .addAttribute("CustomData", GpuFormat.RGBA32_FLOAT)
         .build();
      POSITION_TEX_COLOR0_COLOR1_CENTER = VertexFormat.builder(0)
         .addAttribute("Position", GpuFormat.RGB32_FLOAT)
         .addAttribute("UV0", GpuFormat.RG32_FLOAT)
         .addAttribute("Color0", GpuFormat.RGBA8_UNORM)
         .addAttribute("Color1", GpuFormat.RGBA8_UNORM)
         .addAttribute("CenterUV", GpuFormat.RG32_FLOAT)
         .addAttribute("SizeUV", GpuFormat.RG32_FLOAT)
         .addAttribute("GradientBounds", GpuFormat.RGBA32_FLOAT)
         .build();
   }

   public static int indexOf(VertexFormat format, String attributeName) {
      List<VertexFormatElement> elements = format.getElements();

      for (int i = 0; i < elements.size(); i++) {
         if (attributeName.equals(elements.get(i).name())) {
            return i;
         }
      }

      return -1;
   }
   
   *///?} else {
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

      // 26.1.2 elimina VertexFormatElement.Usage: el cuarto parametro pasa a ser el flag de
      // normalizacion (normalized) y el "usage" queda implicito en el tipo.
      //? if >= 26.1.2 {
      COLOR1 = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.UBYTE, true, 4);
      UV_CENTER = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, false, 2);
      UV_SIZE = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, false, 2);
      CUSTOM_DATA = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, false, 4);
      //?} else {
      /*COLOR1 = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.UBYTE, VertexFormatElement.Usage.COLOR, 4);
      UV_CENTER = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      UV_SIZE = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
      CUSTOM_DATA = VertexFormatElement.register(getAvailableIndex(), 0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 4);
      *///?}
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
   //?}
}
