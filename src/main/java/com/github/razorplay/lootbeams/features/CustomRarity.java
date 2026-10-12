package com.github.razorplay.lootbeams.features;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.compat.prism.PrismCompat;
import com.github.razorplay.lootbeams.helpers.TextColorHelper;
import com.github.razorplay.lootbeams.utils.Selectors;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRarity {
   private static final String CUSTOM_RARITIES_FILE = "custom_rarities.json";
   private static final String CUSTOM_RARITIES_JSON_KEY = "custom_rarities";
   private static final Map<String, CustomRarity> RARITY_CACHE = new HashMap<>();
   private static final String CUSTOM_RARITY_KEY = "custom_rarity";
   private static final String CUSTOM_RARITY_ID_KEY = "id";
   private static final String CUSTOM_RARITY_NAME_KEY = "name";
   private static final String CUSTOM_RARITY_COLOR_KEY = "color";
   private static final String CUSTOM_RARITY_ANIMATED_COLOR_KEY = "animated_color";
   private static final String CUSTOM_RARITY_CONFIG_KEY = "config";
   private static final String CUSTOM_RARITY_SELECTORS_KEY = "selectors";
   public static final String ITEM_CUSTOM_RARITY_ID_KEY = "custom_rarity_id";
   private String name = "";
   private int color = 16777215;
   private TextColor animatedColor = null;
   private JsonObject rarityConfig = null;
   private List<String> selectors = new ArrayList<>();

   public CustomRarity(String rarityName) {
      this.name = rarityName;
   }

   public CustomRarity setColor(int rarityColor) {
      this.color = rarityColor;
      return this;
   }

   public CustomRarity setAnimatedColor(TextColor prismColor) {
      this.animatedColor = prismColor;
      return this;
   }

   public CustomRarity setConfig(JsonObject rarityConfig) {
      this.rarityConfig = rarityConfig;
      return this;
   }

   public CustomRarity setSelectors(List<String> selectors) {
      this.selectors = selectors;
      return this;
   }

   public String getName() {
      return this.name;
   }

   public int getColor() {
      return this.animatedColor != null ? this.animatedColor.getValue() : this.color;
   }

   public boolean hasRarityConfig() {
      return this.rarityConfig != null;
   }

   public List<String> getSelectors() {
      return this.selectors;
   }

   public JsonObject getRarityConfig() {
      return this.rarityConfig;
   }

   public static CustomRarity fromSelectors(ItemStack itemStack) {
      for (CustomRarity rarity : RARITY_CACHE.values()) {
         if (!rarity.getSelectors().isEmpty() && Minecraft.getInstance().level != null) {
            for (String selector : rarity.getSelectors()) {
               if (Selectors.validateSelector(selector) && Selectors.itemMatches(itemStack, selector, Minecraft.getInstance().level.registryAccess())) {
                  return rarity;
               }
            }
         }
      }

      return null;
   }

   public static CustomRarity fromItemStack(ItemStack itemStack) {
      if (itemStack.getComponents().has(DataComponents.CUSTOM_DATA)) {
         CustomData itemCustomData = itemStack.get(DataComponents.CUSTOM_DATA);
         if (itemCustomData != null) {
            CompoundTag customDataNbt = itemCustomData.copyTag();
            if (!customDataNbt.isEmpty()) {
               String customRarityId = customDataNbt.getString(ITEM_CUSTOM_RARITY_ID_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
               CompoundTag customRarity = customDataNbt.getCompound(CUSTOM_RARITY_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
               if (!customRarity.isEmpty()) {
                  String rarityName = customRarity.getString(CUSTOM_RARITY_NAME_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  String hexColor = customRarity.getString(CUSTOM_RARITY_COLOR_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  String animatedHexColor = customRarity.getString(CUSTOM_RARITY_ANIMATED_COLOR_KEY)/*? > 1.21.1 {*//*.get()*//*?}*/;
                  if (!rarityName.isEmpty()) {
                     CustomRarity newRarity = new CustomRarity(Component.translatable(rarityName).getString());
                     if (!hexColor.isEmpty()) {
                        newRarity.setColor(TextColorHelper.getColorFromHEX(hexColor));
                     }

                     if (!animatedHexColor.isEmpty() && PrismCompat.isPrismLoaded()) {
                        TextColor prismColor = PrismCompat.parseColor(animatedHexColor);
                        newRarity.setAnimatedColor(prismColor);
                        newRarity.setColor(prismColor.getValue());
                     }

                     return newRarity;
                  }
               } else if (!customRarityId.isEmpty() && RARITY_CACHE.containsKey(customRarityId)) {
                  return RARITY_CACHE.get(customRarityId);
               }
            }
         }
      }

      CustomRarity rarity = fromSelectors(itemStack);
      return rarity != null ? rarity : null;
   }

   public static Component toText(CustomRarity customRarity) {
      Style newStyle = Style.EMPTY.withItalic(false).withColor(customRarity.getColor());
      return Component.literal(customRarity.getName()).setStyle(newStyle);
   }

   public static void onResourcesReload(ResourceManager resourceManager) {
      RARITY_CACHE.clear();
      Resource customRaritiesFile = resourceManager.getResource(ModTemplate.id(CUSTOM_RARITIES_FILE)).orElse(null);
      if (customRaritiesFile != null) {
         try {
            JsonObject customRaritiesJSON = GsonHelper.parse(customRaritiesFile.openAsReader());
            if (customRaritiesJSON.has(CUSTOM_RARITIES_JSON_KEY)) {
               JsonArray customRarities = GsonHelper.getAsJsonArray(customRaritiesJSON, CUSTOM_RARITIES_JSON_KEY);
               if (!customRarities.isEmpty()) {
                  for (JsonElement jsonElement : customRarities) {
                     if (jsonElement.isJsonObject()) {
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has(CUSTOM_RARITY_ID_KEY)) {
                           String rarityId = GsonHelper.getAsString(jsonObject, CUSTOM_RARITY_ID_KEY);
                           String rarityName = GsonHelper.getAsString(jsonObject, CUSTOM_RARITY_NAME_KEY);
                           String rarityColor = GsonHelper.getAsString(jsonObject, CUSTOM_RARITY_COLOR_KEY);
                           String rarityAnimatedColor = GsonHelper.getAsString(jsonObject, CUSTOM_RARITY_ANIMATED_COLOR_KEY, "");
                           JsonArray raritySelectors = GsonHelper.getAsJsonArray(jsonObject, CUSTOM_RARITY_SELECTORS_KEY, new JsonArray());
                           JsonObject rarityConfig = GsonHelper.getAsJsonObject(jsonObject, CUSTOM_RARITY_CONFIG_KEY);
                           if (!rarityId.isEmpty() && !rarityName.isEmpty()) {
                              CustomRarity newRarity = new CustomRarity(Component.translatable(rarityName).getString());
                              if (!rarityColor.isEmpty()) {
                                 newRarity.setColor(TextColorHelper.getColorFromHEX(rarityColor));
                              }

                              if (!rarityAnimatedColor.isEmpty() && PrismCompat.isPrismLoaded()) {
                                 TextColor prismColor = PrismCompat.parseColor(rarityAnimatedColor);
                                 newRarity.setAnimatedColor(prismColor);
                                 newRarity.setColor(prismColor.getValue());
                              }

                              if (!raritySelectors.isEmpty()) {
                                 List<String> selectors = new ArrayList<>();

                                 for (JsonElement selectorElement : raritySelectors) {
                                    selectors.add(GsonHelper.convertToString(selectorElement, "selector"));
                                 }

                                 if (!selectors.isEmpty()) {
                                    newRarity.setSelectors(selectors);
                                 }
                              }

                              if (!rarityConfig.isEmpty()) {
                                 newRarity.setConfig(rarityConfig);
                              }

                              RARITY_CACHE.put(rarityId, newRarity);
                           }
                        }
                     }
                  }
               }
            }
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
   }

   @Override
   public String toString() {
      return this.getClass().getName() + "[name=" + this.name + ", color=" + this.color + ", rarityConfig=" + this.rarityConfig + "]";
   }
}
