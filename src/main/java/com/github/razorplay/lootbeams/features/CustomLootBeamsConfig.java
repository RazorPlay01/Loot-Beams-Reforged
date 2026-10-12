package com.github.razorplay.lootbeams.features;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.dconfig.DynamicConfig;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.github.razorplay.lootbeams.ModTemplate.MOD_ID;

public class CustomLootBeamsConfig {
   private static final String CONFIG_KEY = "lootbeams_config";
   private static final Map<ItemStack, Configuration> CONFIG_CACHE = new WeakHashMap<>();

   public static void clearConfigCache() {
      CONFIG_CACHE.clear();
   }

   public static void onConfigurationChange() {
      clearConfigCache();
   }

   public static void onResourcesReload() {
      clearConfigCache();
   }

   private static Configuration getConfigCopy(Configuration configInstance) {
      Configuration configCopy = new Configuration();

      for (Field field : configInstance.getClass().getDeclaredFields()) {
         try {
            Field fieldRef = configCopy.getClass().getDeclaredField(field.getName());
            fieldRef.setAccessible(true);
            fieldRef.set(configCopy, field.get(configInstance));
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      return configCopy;
   }

   private static Configuration processConfig(CompoundTag customConfig) {
      Configuration configCopy = getConfigCopy(ModTemplate.config);

      for (DynamicConfig.Control.Field field : ModTemplate.configManager.getFieldsByGroup(Configuration.Groups.VISUAL)) {
         try {
            Field fieldRef = configCopy.getClass().getDeclaredField(field.key);
            fieldRef.setAccessible(true);
            if (!customConfig.isEmpty() && customConfig.contains(field.saveKey)) {
               if (field.type == boolean.class) {
                  fieldRef.set(configCopy, customConfig.getBoolean(field.saveKey));
               }

               if (field.type == int.class) {
                  fieldRef.set(configCopy, customConfig.getInt(field.saveKey));
               }

               if (field.type == long.class) {
                  fieldRef.set(configCopy, customConfig.getLong(field.saveKey));
               }

               if (field.type == float.class) {
                  fieldRef.set(configCopy, customConfig.getFloat(field.saveKey));
               }

               if (field.type == double.class) {
                  fieldRef.set(configCopy, customConfig.getDouble(field.saveKey));
               }

               if (field.type == String.class) {
                  fieldRef.set(configCopy, customConfig.getString(field.saveKey));
               }

               if (field.type == List.class) {
                  String value = customConfig.getString(field.saveKey)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  List<String> values = Arrays.stream(value.split(",")).map(String::trim).toList();
                  fieldRef.set(configCopy, values);
               }

               if (field.type == ParticleManager.ParticleTexture.class) {
                  String textureKey = customConfig.getString(field.saveKey)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  if (textureKey.contains(":")) {
                     String[] parts = textureKey.split(":");
                     fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, parts[0]));
                  } else {
                     fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, MOD_ID));
                  }
               }

               if (field.type == GlowEffectManager.GlowEffectTexture.class) {
                  String textureKey = customConfig.getString(field.saveKey)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  if (textureKey.contains(":")) {
                     String[] parts = textureKey.split(":");
                     fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, parts[0]));
                  } else {
                     fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, MOD_ID));
                  }
               }

               if (field.type == LootBeamShaders.Shader.class) {
                  fieldRef.set(configCopy, LootBeamShaders.Shader.valueOf(customConfig.getString(field.saveKey)/*? > 1.21.1 {*//*.get()*//*?}*/.toUpperCase()));
               }

               if (field.type == LootBeamShaders.CustomShader.class) {
                  fieldRef.set(configCopy, LootBeamShaders.CustomShader.valueOf(customConfig.getString(field.saveKey)/*? > 1.21.1 {*//*.get()*//*?}*/.toUpperCase()));
               }
            } else {
               fieldRef.set(configCopy, ModTemplate.configManager.getFieldValue(field.key));
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      return configCopy;
   }

   private static Configuration processConfig(JsonObject customConfig) {
      Configuration configCopy = getConfigCopy(ModTemplate.config);

      for (DynamicConfig.Control.Field field : ModTemplate.configManager.getFieldsByGroup(Configuration.Groups.VISUAL)) {
         try {
            Field fieldRef = configCopy.getClass().getDeclaredField(field.key);
            fieldRef.setAccessible(true);
            if (!customConfig.isEmpty() && customConfig.has(field.saveKey)) {
               if (field.type == boolean.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsBoolean(customConfig, field.saveKey));
               }

               if (field.type == int.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsInt(customConfig, field.saveKey));
               }

               if (field.type == long.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsLong(customConfig, field.saveKey));
               }

               if (field.type == float.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsFloat(customConfig, field.saveKey));
               }

               if (field.type == double.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsDouble(customConfig, field.saveKey));
               }

               if (field.type == String.class) {
                  fieldRef.set(configCopy, GsonHelper.getAsString(customConfig, field.saveKey));
               }

               if (field.type == List.class) {
                  List<String> values = GsonHelper.getAsJsonArray(customConfig, field.saveKey).asList().stream().<String>map(JsonElement::getAsString).toList();
                  fieldRef.set(configCopy, values);
               }

               if (field.type == ParticleManager.ParticleTexture.class) {
                  String textureKey = GsonHelper.getAsString(customConfig, field.saveKey);
                  if (textureKey.contains(":")) {
                     String[] parts = textureKey.split(":");
                     fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, parts[0]));
                  } else {
                     fieldRef.set(configCopy, ParticleManager.ParticleTexture.of(textureKey, MOD_ID));
                  }
               }

               if (field.type == GlowEffectManager.GlowEffectTexture.class) {
                  String textureKey = GsonHelper.getAsString(customConfig, field.saveKey);
                  if (textureKey.contains(":")) {
                     String[] parts = textureKey.split(":");
                     fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, parts[0]));
                  } else {
                     fieldRef.set(configCopy, GlowEffectManager.GlowEffectTexture.of(textureKey, MOD_ID));
                  }
               }

               if (field.type == LootBeamShaders.Shader.class) {
                  fieldRef.set(configCopy, LootBeamShaders.Shader.valueOf(GsonHelper.getAsString(customConfig, field.saveKey).toUpperCase()));
               }

               if (field.type == LootBeamShaders.CustomShader.class) {
                  fieldRef.set(configCopy, LootBeamShaders.CustomShader.valueOf(GsonHelper.getAsString(customConfig, field.saveKey).toUpperCase()));
               }
            } else {
               fieldRef.set(configCopy, ModTemplate.configManager.getFieldValue(field.key));
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      return configCopy;
   }

   public static Configuration fromItemStack(ItemStack itemStack) {
      if (CONFIG_CACHE.containsKey(itemStack)) {
         return CONFIG_CACHE.get(itemStack);
      }

      if (itemStack.getComponents().has(DataComponents.CUSTOM_DATA)) {
         CustomData itemCustomData = itemStack.get(DataComponents.CUSTOM_DATA);
         if (itemCustomData != null) {
            CompoundTag customDataNbt = itemCustomData.copyTag();
            if (!customDataNbt.isEmpty()) {
               CompoundTag customConfig = customDataNbt.getCompound(CONFIG_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
               if (!customConfig.isEmpty()) {
                  Configuration newConfig = processConfig(customConfig);
                  CONFIG_CACHE.put(itemStack, newConfig);
                  return newConfig;
               }
            }
         }
      }

      CustomRarity itemRarity = CustomRarity.fromItemStack(itemStack);
      if (itemRarity != null && itemRarity.hasRarityConfig()) {
         Configuration newConfig = processConfig(itemRarity.getRarityConfig());
         CONFIG_CACHE.put(itemStack, newConfig);
         return newConfig;
      } else {
         return ModTemplate.config;
      }
   }
}
