package com.github.razorplay.lootbeams.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
//? if >= 1.21.11 {
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.NbtOps;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSink;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;

public class Selectors {
    private static final Map<String, Rarity> rarities = Map.of(
            "common", Rarity.COMMON,
            "uncommon", Rarity.UNCOMMON,
            "rare", Rarity.RARE,
            "epic", Rarity.EPIC
    );
    private static final Map<String, BiPredicate<Tag, String>> nbtComparators = Map.of(
            "=", (tag, value) -> tagAsString(tag).contentEquals(value),
            "!=", (tag, value) -> !tagAsString(tag).contentEquals(value),
            ">", (tag, value) -> {
                try {
                    double parsedValue = Double.valueOf(value);
                    return tag instanceof NumericTag ? numericValue(tag) > parsedValue : false;
                } catch (Exception var4) {
                    return false;
                }
            },
            "<", (tag, value) -> {
                try {
                    double parsedValue = Double.valueOf(value);
                    return tag instanceof NumericTag ? numericValue(tag) < parsedValue : false;
                } catch (Exception var4) {
                    return false;
                }
            }
    );

    private static String tagAsString(Tag tag) {
        //? if <= 1.21.1 {
        /*return tag.getAsString();
        *///?} else {
        return tag.asString().orElseGet(tag::toString);
        //?}
    }

    private static double numericValue(Tag tag) {
        //? if <= 1.21.1 {
        /*return ((NumericTag) tag).getAsDouble();
        *///?} else {
        return ((NumericTag) tag).doubleValue();
        //?}
    }

    private static Set<String> compoundKeys(CompoundTag tag) {
        //? if <= 1.21.1 {
        /*return tag.getAllKeys();
        *///?} else {
        return tag.keySet();
        //?}
    }

    private static byte childTagId(CompoundTag tag, String key) {
        Tag child = tag.get(key);
        return child == null ? 0 : child.getId();
    }

    private static Tag parseSnbtOrNull(String value) {
        try {
            //? if <= 1.21.1 {
            /*return TagParser.parseTag(value);
            *///?} else {
            return TagParser.parseCompoundFully(value);
            //?}
        } catch (Exception e) {
            return null;
        }
    }

    private static Tag saveItemStack(ItemStack item, HolderLookup.Provider provider) {
        //? if <= 1.21.1 {
        /*return item.save(provider);
        *///?} else {
        return ItemStack.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), item)
                .result()
                .orElseGet(CompoundTag::new);
        //?}
    }

    private static Optional<TagKey<Item>> findItemTagKey(String rawId) {
        Identifier target = Identifier.parse(rawId);
        //? if <= 1.21.1 {
        /*return BuiltInRegistries.ITEM.getTagNames().filter(tagKey -> tagKey.location().equals(target)).findFirst();
        *///?} else {
        return BuiltInRegistries.ITEM.getTags()
                .map(HolderSet.Named::key)
                .filter(tagKey -> tagKey.location().equals(target))
                .findFirst();
        //?}
    }

    public static boolean validateSelector(String value) {
        if (value.contains("+")) {
            for (String selector : value.split("\\+")) {
                if (!validateSelector(selector)) {
                    return false;
                }
            }

            return true;
        } else if (value.startsWith("~")) {
            return validateSelector(value.substring(1));
        } else if (value.contentEquals("*")) {
            return true;
        } else if (value.startsWith("$")) {
            return Identifier.tryParse(value.substring(1)) != null;
        } else if (value.startsWith("@")) {
            return value.substring(1).matches("^[a-z][a-z0-9_-]{1,63}$");
        } else if (value.startsWith("!")) {
            return rarities.keySet().contains(value.substring(1).toLowerCase());
        } else if (value.startsWith("#")) {
            return TextColor.parseColor(value).result().orElse(null) != null;
        } else if (value.startsWith("%") || value.startsWith("^")) {
            return true;
        } else {
            return value.startsWith("&") ? true : value == null || value == "" || Identifier.tryParse(value) != null;
        }
    }

    public static boolean itemMatches(ItemStack item, String selector, HolderLookup.Provider provider) {
        if (item.isEmpty()) {
            return false;
        }

        if (selector.contains("+")) {
            for (String tooltipText : selector.split("\\+")) {
                if (!itemMatches(item, tooltipText, provider)) {
                    return false;
                }
            }

            return true;
        } else {
            if (selector.startsWith("~")) {
                return !itemMatches(item, selector.substring(1), provider);
            }

            if (selector.contentEquals("*")) {
                return true;
            }

            String itemIdentifier = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
            if (!selector.equals(itemIdentifier) && !selector.equals(itemIdentifier.replace("minecraft:", ""))) {
                if (selector.startsWith("@")) {
                    if (itemIdentifier.startsWith(selector.substring(1) + ":")) {
                        return true;
                    }
                } else if (selector.startsWith("#")) {
                    TextColor entryColor = TextColor.parseColor(selector).result().orElse(null);
                    if (entryColor != null && entryColor.equals(getColorForItem(item, TextColor.fromRgb(16777215)))) {
                        return true;
                    }
                } else if (selector.startsWith("!")) {
                    if (item.getRarity() == rarities.get(selector.substring(1))) {
                        return true;
                    }
                } else if (selector.startsWith("$")) {

                    Optional<TagKey<Item>> matchingTag = findItemTagKey(selector.substring(1));
                    if (matchingTag.isPresent() && item.is(matchingTag.get())) {
                        return true;
                    }
                } else if (selector.startsWith("%")) {
                    if (item.getDisplayName().getString().contains(selector.substring(1))) {
                        return true;
                    }
                } else if (selector.startsWith("^")) {
                    Minecraft mc = Minecraft.getInstance();
                    List<Component> lines = item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.ADVANCED);
                    String tooltipText = "";

                    for (int n = 1; n < lines.size(); n++) {
                        tooltipText = tooltipText + lines.get(n).getString() + "\n";
                    }

                    if (tooltipText.contains(selector.substring(1))) {
                        return true;
                    }
                } else if (selector.startsWith("&")) {
                    String name = selector.substring(1);
                    String value = null;
                    BiPredicate<Tag, String> valueChecker = null;

                    for (String comparator : nbtComparators.keySet()) {
                        if (name.contains(comparator)) {
                            valueChecker = nbtComparators.get(comparator);
                            String[] components = name.split(comparator);
                            name = components[0];
                            if (components.length > 1) {
                                value = components[1];
                            }
                            break;
                        }
                    }

                    Tag itemTag = saveItemStack(item, provider);
                    boolean result = findMatchingSubtag(itemTag, name, value, valueChecker);
                    if (!result) {
                        if (!name.contains(":")) {
                            name = "minecraft:" + name;
                        }

                        if (value != null) {
                            if (!value.contains(":") && value.matches("^[a-z]+$")) {
                                value = "minecraft:" + value;
                            } else if (value.contains("\"")) {
                                String[] components = value.split("\"");

                                for (int i = 0; i < components.length; i++) {
                                    if (i % 2 == 1 && !components[i].contains(":")) {
                                        components[i] = "minecraft:" + components[i];
                                    }
                                }

                                value = String.join("\"", components);
                            }
                        }

                        result = findMatchingSubtag(itemTag, name, value, valueChecker);
                    }

                    return result;
                }

                return false;
            } else {
                return true;
            }
        }
    }

    private static boolean findMatchingSubtag(Tag tag, String key, String value, BiPredicate<Tag, String> valueChecker) {
        if (tag == null) {
            return false;
        }

        if (tag.getId() == 8) {
            Tag parsed = parseSnbtOrNull(tagAsString(tag));
            if (parsed != null) {
                tag = parsed;
            }
        }

        if (tag.getId() == 10) {
            CompoundTag compoundTag = (CompoundTag) tag;
            if (compoundTag.contains(key)) {
                return value == null && valueChecker == null ? true : valueChecker.test(compoundTag.get(key), value);
            }

            for (String innerKey : compoundKeys(compoundTag)) {
                byte innerId = childTagId(compoundTag, innerKey);
                if ((innerId == 8 || innerId == 9 || innerId == 10)
                        && findMatchingSubtag(compoundTag.get(innerKey), key, value, valueChecker)) {
                    return true;
                }
            }

            return false;
        } else {
            if (tag.getId() != 9) {
                return false;
            }

            for (Tag innerTag : (ListTag) tag) {
                if ((innerTag.getId() == 9 || innerTag.getId() == 10) && findMatchingSubtag(innerTag, key, value, valueChecker)) {
                    return true;
                }
            }

            return false;
        }
    }

    public static TextColor findFirstColorCode(Component textComponent) {
        String rawTitle = textComponent.getString();

        for (int i = 0; i < rawTitle.length(); i += 2) {
            if (rawTitle.charAt(i) != 167) {
                return null;
            }

            try {
                ChatFormatting format = ChatFormatting.getByCode(rawTitle.charAt(i + 1));
                //? if >= 26.2 {
                if (format != null) {
                   TextColor legacyColor = TextColor.fromLegacyFormat(format);
                   if (legacyColor != null) {
                      return legacyColor;
                   }
                }
                //?} else {
                /*if (format != null && format.isColor()) {
                    return TextColor.fromLegacyFormat(format);
                }
                *///?}
            } catch (StringIndexOutOfBoundsException var4) {
                return null;
            }
        }

        return null;
    }

    public static TextColor getColorForItem(ItemStack item, TextColor defaultColor) {
        TextColor result = null;
        result = item.getDisplayName().getStyle().getColor();
        if (item.getItem() != null
                && item.getItem().getName(item) != null
                && item.getItem().getName(item).getStyle() != null
                && item.getItem().getName(item).getStyle().getColor() != null) {
            result = item.getItem().getName(item).getStyle().getColor();
        }

        if (!item.getHoverName().getStyle().isEmpty() && item.getHoverName().getStyle().getColor() != null) {
            result = item.getHoverName().getStyle().getColor();
        }

        TextColor formattingColor = findFirstColorCode(item.getHoverName());
        if (formattingColor != null) {
            result = formattingColor;
        }

        ColorCollector colorCollector = new ColorCollector();
        item.getHoverName().getVisualOrderText().accept(colorCollector);
        if (colorCollector.getColor() != null) {
            result = colorCollector.getColor();
        }

        if (result == null || result.equals(item.getDisplayName().getStyle().getColor())) {
            Minecraft mc = Minecraft.getInstance();
            List<Component> lines = null;

            try {
                lines = item.getTooltipLines(Item.TooltipContext.EMPTY, mc.player, TooltipFlag.ADVANCED);
            } catch (Exception var8) {
            }

            if (lines != null && !lines.isEmpty()) {
                result = lines.get(0).getStyle().getColor();
            }
        }

        if (result == null) {
            result = defaultColor;
        }

        return result;
    }

    private static class ColorCollector implements FormattedCharSink {
        private TextColor color = null;

        public boolean accept(int index, Style style, int codePoint) {
            if (style.getColor() != null) {
                this.color = style.getColor();
                return false;
            } else {
                return true;
            }
        }

        public TextColor getColor() {
            return this.color;
        }
    }

    public record SelectorDocumentation(String name, String description, List<String> examples) {
        public SelectorDocumentation(String name, String description, String... examples) {
            this(name, description, Arrays.asList(examples));
        }
    }
}
