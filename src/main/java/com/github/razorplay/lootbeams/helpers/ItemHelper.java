package com.github.razorplay.lootbeams.helpers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

//? if <=1.21.1 {
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
//?}

//? if >=1.21.2 {
/*import net.minecraft.core.component.DataComponents;
*///?}

import java.util.Arrays;
import java.util.List;

public class ItemHelper {

	public static boolean isItemInRegistryList(List<String> registryNames, Item item) {
		if (registryNames.size() > 0) {
			for (String id : registryNames.stream().filter(s -> !s.isEmpty()).toList()) {
				if (!id.contains(":") && BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(id)) {
					return true;
				}

				ResourceLocation itemResource = ResourceLocation.tryParse(id);
				if (itemResource != null && BuiltInRegistries.ITEM.get(itemResource)/*? >1.21.1 {*//*.get().value().asItem()*//*?} */ == item.asItem()) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isEquipmentItem(ItemStack itemStack) {
		//? if <=1.21.1 {
		for (Class<? extends Item> item : Arrays.asList(
				SwordItem.class,
				DiggerItem.class,
				ArmorItem.class,
				ShieldItem.class,
				ProjectileWeaponItem.class,
				TridentItem.class,
				ArrowItem.class,
				FishingRodItem.class,
				FlintAndSteelItem.class,
				ShearsItem.class,
				BrushItem.class,
				ElytraItem.class
		)) {
			if (item.isAssignableFrom(itemStack.getItem().getClass())) {
				return true;
			}
		}
		return false;
		//?}

		//? if >=1.21.2 {
		/*// Armas cuerpo a cuerpo (reemplaza SwordItem)
		if (itemStack.get(DataComponents.WEAPON) != null) {
			return true;
		}

		// Herramientas (reemplaza DiggerItem y subtipos)
		if (itemStack.get(DataComponents.TOOL) != null) {
			return true;
		}

		// Armadura + Elytra (reemplaza ArmorItem + ElytraItem)
		// EQUIPPABLE cubre cascos, petos, pantalones, botas y elítras
		if (itemStack.get(DataComponents.EQUIPPABLE) != null) {
			return true;
		}

		// Escudos (reemplaza ShieldItem)
		if (itemStack.get(DataComponents.BLOCKS_ATTACKS) != null) {
			return true;
		}

		// Clases que todavía existen en 1.21.11
		Item item = itemStack.getItem();

		if (item instanceof net.minecraft.world.item.TridentItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.ProjectileWeaponItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.ArrowItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.FishingRodItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.FlintAndSteelItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.ShearsItem) {
			return true;
		}
		if (item instanceof net.minecraft.world.item.BrushItem) {
			return true;
		}

		// Opcional: detectar elítras también por GLIDER (por si algún mod no pone EQUIPPABLE)
		if (itemStack.get(DataComponents.GLIDER) != null) {
			return true;
		}

		return false;
		*///?}
	}
}
