package com.github.razorplay.lootbeams.managers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.events.PresetEvents;
import com.github.razorplay.lootbeams.helpers.FileHelper;
import com.github.razorplay.lootbeams.helpers.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PresetManager {
   private static final String PRESET_ASSETS_DIRECTORY = "data/presets";
   private static final String PRESET_CONFIG_DIRECTORY = "lootbeams/presets";
   private static final String PRESET_EXTENSION = ".json";
   private static Map<ResourceLocation, Resource> RESOURCE_PRESETS_MAP = new HashMap<>();
   private static List<Preset> presetList = null;

   public static void init() {
      getPresetFiles();
   }

   public static String getPresetFormattedName(String path) {
      String result = path;
      if (result.contains(".")) {
         result = result.substring(path.lastIndexOf(47) + 1, path.lastIndexOf(46));
      }

      if (result.contains("_")) {
         result = result.replace('_', ' ');
      }

      return StringHelper.capitalize(result);
   }

   public static String getPresetFileName(String presetName) {
      return presetName.replace(' ', '_');
   }

   public static Preset getFirstPreset() {
      return presetList != null && !presetList.isEmpty() ? presetList.getFirst() : null;
   }

   public static String getFirstPresetName() {
      Preset preset = getFirstPreset();
      return preset == null ? "" : preset.displayName();
   }

   public static Set<String> getPresetNames() {
      Set<String> presetNames = new HashSet<>();
      if (presetList != null && !presetList.isEmpty()) {
         for (Preset preset : presetList) {
            presetNames.add(preset.displayName());
         }

         return presetNames;
      } else {
         return presetNames;
      }
   }

   public static Map<String, ResourceLocation> getDefaultPresets() {
      Map<String, ResourceLocation> defaultPresetMap = new HashMap<>();
      if (presetList != null && !presetList.isEmpty()) {
         for (Preset preset : presetList) {
            if (preset.type() == PresetType.IDENTIFIER) {
               defaultPresetMap.put(preset.displayName(), preset./*? fabric {*//*location()*//*}? else {*/identifier /*?} */);

			}
         }

         return defaultPresetMap;
      } else {
         return defaultPresetMap;
      }
   }

   public static Map<String, File> getCustomPresets() {
      Map<String, File> customPresetMap = new HashMap<>();
      if (presetList != null && !presetList.isEmpty()) {
         for (Preset preset : presetList) {
            if (preset.type() == PresetType.FILE) {
               customPresetMap.put(preset.displayName(), preset.presetFile());
            }
         }

         return customPresetMap;
      } else {
         return customPresetMap;
      }
   }

   public static void onResourcesReload(ResourceManager resourceManager) {
      RESOURCE_PRESETS_MAP = resourceManager.listResources(PRESET_ASSETS_DIRECTORY, identifier -> identifier.getPath().endsWith(PRESET_EXTENSION));
   }

   public static List<Preset> getPresetFiles() {
      List<Preset> presets = new ArrayList<>();

      for (ResourceLocation presetId : RESOURCE_PRESETS_MAP.keySet()) {
         presets.add(new Preset(getPresetFormattedName(presetId.getPath()), presetId));
      }

      try {
         Path presetDirPath = ModTemplate.xplat().getConfigDir().resolve(PRESET_CONFIG_DIRECTORY);
         if (Files.exists(presetDirPath)) {
            Files.list(presetDirPath)
               .filter(x$0 -> Files.isRegularFile(x$0))
               .filter(path -> path.toString().endsWith(PRESET_EXTENSION))
               .forEach(path -> presets.add(new Preset(getPresetFormattedName(path.getFileName().toString()), path.toFile())));
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      presetList = presets;
      return presets;
   }

   public static void loadPreset(File presetFile) {
	   ModTemplate.configManager.load(presetFile);
   }

   public static void loadPreset(ResourceLocation id) {
      Resource presetResource = RESOURCE_PRESETS_MAP.get(id);
      if (presetResource != null) {
         try {
			 ModTemplate.configManager.load(presetResource.open());
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }

   public static void savePreset(String presetName) {
      Path newPresetPath = ModTemplate.xplat().getConfigDir().resolve(PRESET_CONFIG_DIRECTORY + "/" + presetName + PRESET_EXTENSION);
      File newPresetFile = FileHelper.createPathIfNotExists(newPresetPath.toString());
	   ModTemplate.configManager.save(newPresetFile);
      Minecraft client = Minecraft.getInstance();
      client.getToasts()
         .addToast(
            SystemToast.multiline(
               client,
               SystemToast.SystemToastId.NARRATOR_TOGGLE,
               Component.nullToEmpty("LootBeams"),
               Component.translatable("lootbeams.notification.savePreset.text", getPresetFormattedName(presetName))
            )
         );
   }

   public static boolean tryApplyPreset(String presetName) {
      if (ModTemplate.config.selectedPreset.equals(presetName)) {
         return false;
      } else {
         Map<String, ResourceLocation> defaultPresets = getDefaultPresets();
         Map<String, File> customPresets = getCustomPresets();
         if (defaultPresets.containsKey(presetName)) {
            ResourceLocation presetId = defaultPresets.get(presetName);
            loadPreset(presetId);
			 ModTemplate.config.selectedPreset = presetName;
            return true;
         } else if (customPresets.containsKey(presetName)) {
            File presetFile = customPresets.get(presetName);
            loadPreset(presetFile);
			 ModTemplate.config.selectedPreset = presetName;
            return true;
         } else {
            return false;
         }
      }
   }

   public static void loadAndApplyPreset(String presetName) {
      if (tryApplyPreset(presetName)) {
         Minecraft client = Minecraft.getInstance();
         client.getToasts()
            .addToast(
               SystemToast.multiline(
                  client,
                  SystemToast.SystemToastId.NARRATOR_TOGGLE,
                  Component.nullToEmpty("LootBeams"),
                  Component.translatable("lootbeams.notification.loadPreset.text", presetName)
               )
            );
		  ModTemplate.configManager.save();
         PresetEvents.APPLY_PRESET.invoker().onApplyPreset(presetName);
      }
   }

	public record Preset(PresetType type, String displayName, ResourceLocation identifier, File presetFile) {
		public Preset(String displayName, ResourceLocation identifier) {
			this(PresetType.IDENTIFIER, displayName, identifier, null);
		}

		public Preset(String displayName, File presetFile) {
			this(PresetType.FILE, displayName, null, presetFile);
		}

		@Override
		public String toString() {
			return this.displayName;
		}
	}

   public enum PresetType {
      IDENTIFIER,
      FILE
   }
}
