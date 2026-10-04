package com.github.razorplay.lootbeams.compat.iris;

import com.github.razorplay.lootbeams.ModTemplate;

import java.util.List;

public class IrisCompat {
   public static boolean isIrisLoaded() {
      return ModTemplate.xplat().isModLoaded("iris");
   }

   public static List<Integer> getVertexFormatsIndexes() {
      return List.of(10, 11, 12, 13, 14);
   }

   public static boolean isShaderPackInUse() {
      if (!isIrisLoaded()) {
         return false;
      }

      try {
         Object irisApiInstance = Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("getInstance").invoke(null);
         return (Boolean)Class.forName("net.irisshaders.iris.api.v0.IrisApi").getMethod("isShaderPackInUse").invoke(irisApiInstance);
      } catch (Exception ex) {
         ex.printStackTrace();
         return false;
      }
   }
}
