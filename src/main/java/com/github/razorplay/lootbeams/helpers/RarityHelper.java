package com.github.razorplay.lootbeams.helpers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.features.CustomRarity;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Optional;

public class RarityHelper {
   public static String getRarity(ItemStack stack) {
      String rarity = stack.getRarity().name().toLowerCase();
      rarity = rarity.replace(":", ".").replace("_", ".");
      CustomRarity customRarity = CustomRarity.fromItemStack(stack);
      if (customRarity != null) {
         return customRarity.getName();
      }

      String translatableKey = "lootbeams.rarity." + rarity;
      return I18n.exists(translatableKey) ? I18n.get(translatableKey, new Object[0]) : rarity;
   }

   public static boolean rarityCheck(ItemStack itemStack, boolean isRare) {
      CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
      return customRarity != null ? true : isRare || itemStack.getRarity() != Rarity.COMMON;
   }

	public static boolean alwaysHasRarity(ItemStack item) {
		List<String> overrides = ModTemplate.config.alwaysDrawRaritiesOn;
		if (overrides.isEmpty()) {
			return false;
		}

		for (String name : overrides.stream().filter(s -> !s.isEmpty()).toList()) {
			// Namespace solo (ej: "minecraft")
			if (!name.contains(":") && !name.startsWith("#")
					&& BuiltInRegistries.ITEM.getKey(item.getItem()).getNamespace().equals(name)) {
				return true;
			}

			ResourceLocation registry = ResourceLocation.tryParse(name.replace("#", ""));
			if (registry == null) continue;

			if (name.startsWith("#")) {
				//? if <= 1.21.1 {
			Optional<net.minecraft.core.HolderSet.Named<Item>> tag = BuiltInRegistries.ITEM
					.getTags()
					.filter(pair -> pair.getFirst().location().equals(registry))
					.findFirst()
					.map(Pair::getSecond);
			if (tag.isPresent()
					&& tag.get().contains(
					BuiltInRegistries.ITEM.getHolder(BuiltInRegistries.ITEM.getResourceKey(item.getItem()).get()).get()
			)) {
				return true;
			}
			//?} else {
				/*net.minecraft.tags.TagKey<Item> tagKey = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.ITEM, registry);
				if (item.is(tagKey)) {
					return true;
				}
				*///?}
				continue;
			}

			//? if <= 1.21.1 {
		Optional<Item> registryItem = BuiltInRegistries.ITEM.getOptional(registry);
		if (registryItem.isPresent() && registryItem.get().asItem() == item.getItem()) {
			return true;
		}
		//?} else {
			/*Item registryItem = BuiltInRegistries.ITEM.get(registry).get().value();
			if (registryItem != null && registryItem != net.minecraft.world.item.Items.AIR && registryItem == item.getItem()) {
				return true;
			}
			*///?}
		}

		return false;
	}
}
