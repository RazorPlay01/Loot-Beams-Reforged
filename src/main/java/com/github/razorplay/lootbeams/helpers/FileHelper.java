package com.github.razorplay.lootbeams.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
   public static File createPathIfNotExists(String filePath) {
      Path path = Paths.get(filePath);
      if (Files.notExists(path.getParent())) {
         try {
            Files.createDirectories(path.getParent());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      if (Files.notExists(path)) {
         try {
            Files.createFile(path);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      return path.toFile();
   }
}
