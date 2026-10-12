package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.compat.prism.PrismCompat;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.features.CustomRarity;
import com.github.razorplay.lootbeams.managers.CrashManager;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextColorHelper {
   private static Map<ItemStack, TextColor> DC_MAP = new HashMap<>();
   private static String NBT_COLOR_KEY = "lootbeams.color";
   private static String NBT_ANIMATED_COLOR_KEY = "lootbeams.animated_color";

   public static TextColor getItemColor(ItemStack itemStack) {
      if (CrashManager.CRASH_BLACKLIST.contains(itemStack)) {
         return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
      }

      Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);

      try {
         TextColor override = getColorFromItemOverrides(itemStack.getItem());
         if (override != null) {
            return override;
         }

         if (itemStack.getComponents().has(DataComponents.CUSTOM_DATA)) {
            CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
            if (data != null) {
               CompoundTag value = data.copyTag();
               if (value.contains(NBT_ANIMATED_COLOR_KEY) && PrismCompat.isPrismLoaded()) {
                  if (DC_MAP.containsKey(itemStack)) {
                     return TextColor.fromRgb(DC_MAP.get(itemStack).getValue());
                  }

                  TextColor dColor = PrismCompat.parseColor(value.getString(NBT_ANIMATED_COLOR_KEY));
                  DC_MAP.put(itemStack, dColor);
                  return TextColor.fromRgb(dColor.getValue());
               }

               if (value.contains(NBT_COLOR_KEY)) {
                  return TextColor.parseColor(value.getString(NBT_COLOR_KEY)/*? >1.21.1 {*//*.get()*//*?} */).getOrThrow();
               }
            }
         }

         if (itemConfig.renderNameColor) {
            TextColor nameColor = getRawColor(itemStack.getHoverName());
            if (!nameColor.equals(TextColor.fromLegacyFormat(ChatFormatting.WHITE))) {
               return nameColor;
            }
         }

         if (itemConfig.renderRarityColor) {
            CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
            if (customRarity != null) {
               return TextColor.fromRgb(customRarity.getColor());
            }

            if (itemStack.getRarity().color() != null) {
               return TextColor.fromLegacyFormat(itemStack.getRarity().color());
            }
         }

         return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
      } catch (Exception e) {
         CrashManager.LOGGER.error("Failed to get color for ({}), added to temporary blacklist", itemStack.getDisplayName());
         CrashManager.CRASH_BLACKLIST.add(itemStack);
         CrashManager.LOGGER.info("Temporary blacklist is now : ");

         for (ItemStack s : CrashManager.CRASH_BLACKLIST) {
            CrashManager.LOGGER.info(s.getDisplayName());
         }

         return TextColor.fromLegacyFormat(ChatFormatting.WHITE);
      }
   }

	public static TextColor getColorFromItemOverrides(Item i) {
		List<String> overrides = ModTemplate.config.colorOverrides;
		if (overrides.isEmpty()) {
			return null;
		}

		ItemStack stack = i.getDefaultInstance();

		for (String unparsed : overrides.stream().filter(s -> !s.isEmpty()).toList()) {
			String[] configValue = unparsed.split("=");
			if (configValue.length != 2) continue;

			String nameIn = configValue[0];
			TextColor colorIn = null;

			// Parsear color
			try {
				if (PrismCompat.isPrismLoaded()) {
					if (DC_MAP.containsKey(stack)) {
						colorIn = TextColor.fromRgb(DC_MAP.get(stack).getValue());
					} else {
						TextColor dColor = PrismCompat.parseColor(configValue[1]);
						DC_MAP.put(stack, dColor);
						colorIn = TextColor.fromRgb(dColor.getValue());
					}
				} else {
					//? if <= 1.21.1 {
					colorIn = TextColor.parseColor(configValue[1])
						.result()
						.orElseThrow(() -> new IllegalArgumentException("Invalid color " + configValue[1]));
					 //?} else {
					/*colorIn = TextColor.parseColor(configValue[1]).getOrThrow();
					*///?}
				}
			} catch (Exception e) {
				CrashManager.LOGGER.error("Color overrides error! \"{}\" is not a valid hex color for \"{}\"", configValue[1], nameIn);
				return null;
			}

			// 1. Namespace solo (ej: "minecraft")
			if (!nameIn.contains(":") && !nameIn.startsWith("#")
					&& BuiltInRegistries.ITEM.getKey(i).getNamespace().equals(nameIn)) {
				return colorIn;
			}

			ResourceLocation registry = ResourceLocation.tryParse(nameIn.replace("#", ""));
			if (registry == null) continue;

			// 2. Tag (#minecraft:swords, #c:tools, etc.)
			if (nameIn.startsWith("#")) {
				//? if <= 1.21.1 {
			Optional<HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM
					.getTags()
					.filter(pair -> pair.getFirst().location().equals(registry))
					.findFirst()
					.map(Pair::getSecond);
			if (tag.isPresent()
					&& tag.get().contains(
					BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getResourceKey(i).get()).get()
			)) {
				return colorIn;
			}
			//?} else {
				/*net.minecraft.tags.TagKey<Item> tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, registry);
				if (stack.is(tagKey)) {
					return colorIn;
				}
				*///?}
				continue;
			}

			// 3. ID completo (minecraft:diamond_sword)
			//? if <= 1.21.1 {
		Optional<Item> registryItem = BuiltInRegistries.ITEM.getOptional(registry);
		if (registryItem.isPresent() && registryItem.get().asItem() == i.asItem()) {
			return colorIn;
		}
		//?} else {
			/*Item registryItem = BuiltInRegistries.ITEM.get(registry).get().value();
			if (registryItem != null && registryItem != net.minecraft.world.item.Items.AIR && registryItem == i) {
				return colorIn;
			}
			*///?}
		}

		return null;
	}

   public static TextColor getRawColor(Component text) {
      List<Style> list = Lists.newArrayList();
      text.visit((acceptor, styleIn) -> {
         StringDecomposer.iterateFormatted(styleIn, acceptor, (string, style, consumer) -> {
            list.add(style);
            return true;
         });
         return Optional.empty();
      }, Style.EMPTY);
      return !list.isEmpty() && list.get(0).getColor() != null ? list.get(0).getColor() : TextColor.fromLegacyFormat(ChatFormatting.WHITE);
   }

   private static String compensateHex(String hex) {
      StringBuilder compensatedHex = new StringBuilder();

      for (char c : hex.toCharArray()) {
         compensatedHex.append(c).append(c);
      }

      return compensatedHex.toString();
   }

   public static int getColorFromHEX(String hex) {
      int defaultColor = Integer.parseInt("FFFFFF", 16);
      if (hex != null && !hex.isEmpty()) {
         if (hex.contains("#")) {
            hex = hex.replace("#", "");
         }

         if (!hex.isEmpty()) {
            switch (hex.length()) {
               case 3:
                  return Integer.parseInt(compensateHex(hex), 16);
               case 4:
                  return Integer.parseInt(compensateHex(hex).substring(0, 6), 16);
               case 5:
               case 7:
               default:
                  break;
               case 6:
                  return Integer.parseInt(hex, 16);
               case 8:
                  return Integer.parseInt(hex.substring(0, 6), 16);
            }
         }

         return defaultColor;
      } else {
         return defaultColor;
      }
   }
}
