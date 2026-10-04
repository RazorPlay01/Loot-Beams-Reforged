package com.github.razorplay.lootbeams.compat.modmenu;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.dconfig.DynamicConfig;
import com.github.razorplay.lootbeams.extensions.TriConsumer;
import com.github.razorplay.lootbeams.features.BeamOpacityOnApproach;
import com.github.razorplay.lootbeams.features.BeamSizeOnApproach;
import com.github.razorplay.lootbeams.managers.GlowEffectManager;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder.TopCellElementBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBeamsConfigScreen {
   public static Map<Class<?>, TriConsumer<ConfigBuilder, ConfigCategory, DynamicConfig.Control.Field>> FIELD_BUILDERS = Map.of(
      boolean.class,
      (builder, group, field) -> {
         boolean defaultValue = (Boolean)field.defaultValue;
         group.addEntry(
            builder.entryBuilder()
               .startBooleanToggle(Component.translatable(field.name), ModTemplate.configManager.<Boolean>getFieldValue(field.key))
               .setDefaultValue(defaultValue)
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .setSaveConsumer(value -> ModTemplate.configManager.setFieldValue(field.key, value))
               .build()
         );
      },
      int.class,
      (builder, group, field) -> {
         int defaultValue = (Integer)field.defaultValue;
         int minValue = (Integer)field.minValue;
         int maxValue = (Integer)field.maxValue;
         group.addEntry(
            builder.entryBuilder()
               .startIntField(Component.translatable(field.name), ModTemplate.configManager.<Integer>getFieldValue(field.key))
               .setDefaultValue(defaultValue)
               .setMin(minValue)
               .setMax(maxValue)
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .setSaveConsumer(value -> ModTemplate.configManager.setFieldValue(field.key, value))
               .build()
         );
      },
      float.class,
      (builder, group, field) -> {
         float defaultValue = (Float)field.defaultValue;
         float minValue = (Float)field.minValue;
         float maxValue = (Float)field.maxValue;
         group.addEntry(
            builder.entryBuilder()
               .startFloatField(Component.translatable(field.name), ModTemplate.configManager.<Float>getFieldValue(field.key))
               .setDefaultValue(defaultValue)
               .setMin(minValue)
               .setMax(maxValue)
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .setSaveConsumer(value -> ModTemplate.configManager.setFieldValue(field.key, value))
               .build()
         );
      },
      List.class,
      (builder, group, field) -> {
         List<String> defaultValue = (List<String>)field.defaultValue;
         group.addEntry(
            builder.entryBuilder()
               .startStrList(Component.translatable(field.name), ModTemplate.configManager.getFieldValue(field.key))
               .setDefaultValue(defaultValue)
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .setSaveConsumer(value -> ModTemplate.configManager.setFieldValue(field.key, value))
               .build()
         );
      },
      ParticleManager.ParticleTexture.class,
      (builder, group, field) -> {
         ParticleManager.ParticleTexture defaultValue = (ParticleManager.ParticleTexture)field.defaultValue;
         ParticleManager.ParticleTexture currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name),
                  AnimatedTextureCellCreator.topParticleCell(currentValue),
                  AnimatedTextureCellCreator.selectionParticleCell(20, 112, 6)
               )
               .setDefaultValue(defaultValue)
               .setSelections(ParticleManager.getTextures())
               .setSuggestionMode(false)
               .setSaveConsumer(texture -> {
                  if (!currentValue.equals(texture)) {
					  ModTemplate.configManager.setFieldValue(field.key, texture);
                  }
               })
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      },
      GlowEffectManager.GlowEffectTexture.class,
      (builder, group, field) -> {
         GlowEffectManager.GlowEffectTexture defaultValue = (GlowEffectManager.GlowEffectTexture)field.defaultValue;
         GlowEffectManager.GlowEffectTexture currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name),
                  AnimatedTextureCellCreator.topGlowEffectCell(currentValue),
                  AnimatedTextureCellCreator.selectionGlowEffectCell(20, 112, 6)
               )
               .setDefaultValue(defaultValue)
               .setSelections(GlowEffectManager.getTextures())
               .setSuggestionMode(false)
               .setSaveConsumer(texture -> {
                  if (!currentValue.equals(texture)) {
					  ModTemplate.configManager.setFieldValue(field.key, texture);
                  }
               })
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      },
      LootBeamShaders.Shader.class,
      (builder, group, field) -> {
         LootBeamShaders.Shader defaultValue = (LootBeamShaders.Shader)field.defaultValue;
         LootBeamShaders.Shader currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name), TopCellElementBuilder.of(currentValue.name().toLowerCase(), name -> name, Component::literal)
               )
               .setDefaultValue(defaultValue.name().toLowerCase())
               .setSelections(Arrays.stream(LootBeamShaders.Shader.values()).map(shader -> shader.name().toLowerCase()).toList())
               .setSuggestionMode(false)
               .setSaveConsumer(shaderName -> ModTemplate.configManager.setFieldValue(field.key, LootBeamShaders.Shader.valueOf(shaderName.toUpperCase())))
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      },
      LootBeamShaders.CustomShader.class,
      (builder, group, field) -> {
         LootBeamShaders.CustomShader defaultValue = (LootBeamShaders.CustomShader)field.defaultValue;
         LootBeamShaders.CustomShader currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name), TopCellElementBuilder.of(currentValue.name().toLowerCase(), name -> name, Component::literal)
               )
               .setDefaultValue(defaultValue.name().toLowerCase())
               .setSelections(LootBeamShaders.CustomShader.values().stream().map(shader -> shader.name().toLowerCase()).toList())
               .setSuggestionMode(false)
               .setSaveConsumer(shaderName -> ModTemplate.configManager.setFieldValue(field.key, LootBeamShaders.CustomShader.valueOf(shaderName.toUpperCase())))
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      },
      BeamOpacityOnApproach.class,
      (builder, group, field) -> {
         BeamOpacityOnApproach defaultValue = (BeamOpacityOnApproach)field.defaultValue;
         BeamOpacityOnApproach currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name), TopCellElementBuilder.of(currentValue.name().toLowerCase(), name -> name, Component::literal)
               )
               .setDefaultValue(defaultValue.name().toLowerCase())
               .setSelections(Arrays.stream(BeamOpacityOnApproach.values()).map(option -> option.name().toLowerCase()).toList())
               .setSuggestionMode(false)
               .setSaveConsumer(optionName -> ModTemplate.configManager.setFieldValue(field.key, BeamOpacityOnApproach.valueOf(optionName.toUpperCase())))
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      },
      BeamSizeOnApproach.class,
      (builder, group, field) -> {
         BeamSizeOnApproach defaultValue = (BeamSizeOnApproach)field.defaultValue;
         BeamSizeOnApproach currentValue = ModTemplate.configManager.getFieldValue(field.key);
         group.addEntry(
            builder.entryBuilder()
               .startDropdownMenu(
                  Component.translatable(field.name), TopCellElementBuilder.of(currentValue.name().toLowerCase(), name -> name, Component::literal)
               )
               .setDefaultValue(defaultValue.name().toLowerCase())
               .setSelections(Arrays.stream(BeamSizeOnApproach.values()).map(option -> option.name().toLowerCase()).toList())
               .setSuggestionMode(false)
               .setSaveConsumer(optionName -> ModTemplate.configManager.setFieldValue(field.key, BeamSizeOnApproach.valueOf(optionName.toUpperCase())))
               .setTooltip(new Component[]{Component.translatable(field.description)})
               .build()
         );
      }
   );

   public static Screen getConfigScreen(Screen parent) {
      ConfigBuilder builder = ConfigBuilder.create();
      if (parent != null) {
         builder.setParentScreen(parent);
      }

      builder.setTitle(Component.translatable("lootbeams.title"));
      Map<String, ConfigCategory> groups = new HashMap<>();

      for (DynamicConfig.Control.Field field : ModTemplate.configManager.getFields()) {
         if (field.displayOnConfigScreen && field.category.displayOnConfigScreen) {
            if (!groups.containsKey(field.category.key)) {
               groups.put(field.category.key, builder.getOrCreateCategory(Component.translatable("lootbeams.config." + field.category.key)));
            }

            ConfigCategory group = groups.get(field.category.key);
            if (FIELD_BUILDERS.containsKey(field.type)) {
               FIELD_BUILDERS.get(field.type).accept(builder, group, field);
            }
         }
      }

      builder.setSavingRunnable(() -> ModTemplate.configManager.save());
      return builder.build();
   }
}
