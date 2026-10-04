package com.github.razorplay.lootbeams.shaders;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.render.CustomVertexFormats;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.RegistrationContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBeamShaders {
   private static final Map<Shader, ShaderInstance> SHADERS_MAP = new EnumMap<>(Shader.class);
   private static final Map<DroplightShader, ShaderInstance> DROPLIGHT_SHADERS_MAP = new EnumMap<>(DroplightShader.class);
   private static final Map<String, ShaderInstance> CUSTOM_SHADERS_MAP = new HashMap<>();
   private static final String CUSTOM_SHADERS_DIRECTORY = "shaders/core/custom";
   private static final String CUSTOM_SHADERS_EXTENSION = ".json";

   public static void registerShader(RegistrationContext registry, Shader shader, String name, VertexFormat format) {
      try {
         registry.register(ModTemplate.id(name), format, shaderProgram -> SHADERS_MAP.put(shader, shaderProgram));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static void registerDroplightShader(RegistrationContext registry, DroplightShader shader, String name, VertexFormat format) {
      try {
         registry.register(ModTemplate.id(name), format, shaderProgram -> DROPLIGHT_SHADERS_MAP.put(shader, shaderProgram));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static void registerCustomShader(RegistrationContext registry, CustomShader shader, String name, VertexFormat format) {
      try {
         registry.register(ModTemplate.id(name), format, shaderProgram -> CUSTOM_SHADERS_MAP.put(shader.name(), shaderProgram));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public static ShaderInstance getShader(Shader shader) {
      return SHADERS_MAP.get(shader);
   }

   public static ShaderInstance getShader(DroplightShader shader) {
      return DROPLIGHT_SHADERS_MAP.get(shader);
   }

   public static ShaderInstance getShader(CustomShader shader) {
      return CUSTOM_SHADERS_MAP.get(shader.name());
   }

   public static float getAverageColor(Shader shader) {
      return switch (shader) {
         case ADD -> 1.0F;
         case GLOW_OVERLAY -> 0.5F;
         case PARTICLE_OVERLAY -> 0.5F;
         default -> 0.5F;
      };
   }

   public static void registerCoreShaders(RegistrationContext shaderRegistry) {
      for (Shader shader : Shader.values()) {
         String registryShaderName = "lootbeams_" + shader.name().toLowerCase();
         registerShader(shaderRegistry, shader, registryShaderName, DefaultVertexFormat.PARTICLE);
      }
   }

   private static void registerDroplightCoreShader(RegistrationContext shaderRegistry, DroplightShader shader, VertexFormat vertexFormat) {
      String registryShaderName = "lootbeams_" + shader.name().toLowerCase();
      registerDroplightShader(shaderRegistry, shader, registryShaderName, vertexFormat);
   }

   public static void registerDroplightCoreShaders(RegistrationContext shaderRegistry) {
      registerDroplightCoreShader(shaderRegistry, DroplightShader.DROPLIGHT, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);
      registerDroplightCoreShader(shaderRegistry, DroplightShader.DROPLIGHT_ANIMATED, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CUSTOM);
      registerDroplightCoreShader(shaderRegistry, DroplightShader.DROPLIGHT_GLOW, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);
   }

   public static void registerCustomCoreShaders(RegistrationContext shaderRegistry) {
      ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
      if (resourceManager != null) {
         onCustomShadersReload(shaderRegistry, resourceManager);
      }
   }

   public static String getFormattedName(String path) {
      String result = path;
      if (result.contains(".")) {
         result = result.substring(path.lastIndexOf(47) + 1, path.lastIndexOf(46));
      }

      return result.toUpperCase();
   }

   public static void onCustomShadersReload(RegistrationContext shaderRegistry, ResourceManager resourceManager) {
      CustomShader.clearDynamicValues();
      CUSTOM_SHADERS_MAP.clear();
      Map<ResourceLocation, Resource> resourceShaderMap = resourceManager.listResources(
         CUSTOM_SHADERS_DIRECTORY, identifier -> identifier.getPath().endsWith(CUSTOM_SHADERS_EXTENSION)
      );

      for (ResourceLocation shaderId : resourceShaderMap.keySet()) {
         CustomShader newShader = CustomShader.addValue(getFormattedName(shaderId.getPath()));
         String registryShaderName = newShader.name().toLowerCase();
         if (shaderRegistry != null) {
            registerCustomShader(shaderRegistry, newShader, "custom/" + registryShaderName, CustomVertexFormats.POSITION_TEX_COLOR0_COLOR1_CENTER);
         }
      }
   }

   public static class CustomShader {
      public static final CustomShader NONE = new CustomShader("NONE");
      private final String name;
      private static final List<CustomShader> dynamicValues = new ArrayList<>();
      private static final List<CustomShader> staticValues = new ArrayList<>();

      private CustomShader(String name) {
         this.name = name;
      }

      public String name() {
         return this.name;
      }

      public static CustomShader addValue(String name) {
         synchronized (dynamicValues) {
            for (CustomShader value : values()) {
               if (value.name.equals(name)) {
                  return value;
               }
            }

            CustomShader newValue = new CustomShader(name);
            dynamicValues.add(newValue);
            return newValue;
         }
      }

      public static void clearDynamicValues() {
         synchronized (dynamicValues) {
            dynamicValues.clear();
         }
      }

      public static List<CustomShader> values() {
         synchronized (dynamicValues) {
            List<CustomShader> allValues = new ArrayList<>(staticValues);
            allValues.addAll(dynamicValues);
            return allValues;
         }
      }

      public static CustomShader valueOf(String name) {
         for (CustomShader shader : staticValues) {
            if (shader.name.equals(name)) {
               return shader;
            }
         }

         synchronized (dynamicValues) {
            for (CustomShader shader : dynamicValues) {
               if (shader.name.equals(name)) {
                  return shader;
               }
            }
         }

         return addValue(name);
      }

      @Override
      public String toString() {
         return this.name;
      }

      static {
         staticValues.add(NONE);
      }
   }

   public enum DroplightShader {
      DROPLIGHT,
      DROPLIGHT_ANIMATED,
      DROPLIGHT_GLOW
   }

   public enum Shader {
      ADD,
      GLOW_OVERLAY,
      PARTICLE_OVERLAY
   }
}
