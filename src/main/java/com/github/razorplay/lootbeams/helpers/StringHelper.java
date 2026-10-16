package com.github.razorplay.lootbeams.helpers;

public class StringHelper {
   public static String capitalize(String str) {
      return str != null && !str.isEmpty() ? str.substring(0, 1).toUpperCase() + str.substring(1) : "";
   }

   public static String camelToSnake(String str) {
      return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").replaceAll("([A-Z])([A-Z][a-z])", "$1_$2").toLowerCase();
   }

   /** Extrae el nombre de archivo de una ruta y lo devuelve en mayúsculas (usado por los shaders). */
   public static String getFormattedName(String path) {
      String result = path;
      if (result.contains(".")) {
         result = result.substring(path.lastIndexOf(47) + 1, path.lastIndexOf(46));
      }
      return result.toUpperCase();
   }

   public static String toBinaryName(String mapName) {
      return "L" + mapName.replace('.', '/') + ";";
   }
}
