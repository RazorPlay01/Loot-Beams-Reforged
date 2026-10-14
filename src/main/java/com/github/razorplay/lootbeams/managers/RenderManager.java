package com.github.razorplay.lootbeams.managers;

import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.extensions.LootbeamsParticleManager;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RenderManager {
   private RenderManager() {
      /* This utility class should not be instantiated */
   }

   protected static final List<ItemEntity> LIGHT_CACHE = new ArrayList<>();
   private static final List<BiConsumer<PoseStack, LootBeamBuffer>> RENDER_BEFORE_PARTICLES = new ArrayList<>();
   private static final List<BiConsumer<PoseStack, LootBeamBuffer>> RENDER_AFTER_TRANSLUCENT = new ArrayList<>();
   private static final List<BiConsumer<PoseStack, LootBeamBuffer>> RENDER_AFTER_WEATHER = new ArrayList<>();
   private static final List<BiConsumer<PoseStack, LootBeamBuffer>> RENDER_BEFORE_END = new ArrayList<>();
   private static final Map<ItemEntity, Boolean> ITEM_RENDER_STATES = new HashMap<>();
   private static final Map<Class<?>, Consumer<WorldRendererContext>> END_RENDERS = new HashMap<>();

   public static void onWorldRenderBeforeParticles(WorldRendererContext worldRendererContext) {
      renderBeforeParticles(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera()/*? <= 1.21.1 {*/ /*.getPosition() *//*?} else {*/ .position() /*?} */, worldRendererContext.getConsumers());
   }


   public static void onWorldRenderAfterTranslucent(WorldRendererContext worldRendererContext) {
      renderAfterTranslucent(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera()/*? <= 1.21.1 {*/ /*.getPosition() *//*?} else {*/ .position() /*?} */, worldRendererContext.getConsumers());
   }

   public static void onWorldRenderAfterWeather(WorldRendererContext worldRendererContext) {
      renderAfterWeather(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera()/*? <= 1.21.1 {*/ /*.getPosition() *//*?} else {*/ .position() /*?} */, worldRendererContext.getConsumers());
      LootbeamsParticleManager particleManager = (LootbeamsParticleManager)Minecraft.getInstance().particleEngine;
      if (particleManager != null) {
         particleManager.renderCustomParticles(
            worldRendererContext.getLightmapTextureManager(), worldRendererContext.getCamera(), worldRendererContext.getTickCounter().getGameTimeDeltaPartialTick(false)
         );
      }
   }

   public static void onWorldRenderBeforeEnd(WorldRendererContext worldRendererContext) {
      renderBeforeEnd(worldRendererContext.getMatrixStack(), worldRendererContext.getCamera()/*? <= 1.21.1 {*/ /*.getPosition() *//*?} else {*/ .position() /*?} */, worldRendererContext.getConsumers());
   }

   public static void onWorldRenderEnd(WorldRendererContext worldRendererContext) {
      afterRender(worldRendererContext);
      RENDER_BEFORE_PARTICLES.clear();
      RENDER_AFTER_TRANSLUCENT.clear();
      RENDER_AFTER_WEATHER.clear();
      RENDER_BEFORE_END.clear();
      END_RENDERS.clear();
   }

   public static void onEntityLoad(ItemEntity itemEntity, Level world) {
      if (!LIGHT_CACHE.contains(itemEntity)) {
         LIGHT_CACHE.add(itemEntity);
      }
   }

   public static void onEntityUnload(ItemEntity itemEntity, Level world) {
      LIGHT_CACHE.remove(itemEntity);
   }

   public static void renderAfter(PoseStack stack, Vec3 cameraPos, LootBeamBuffer immediate, List<BiConsumer<PoseStack, LootBeamBuffer>> consumers) {
      if (stack != null) {
         stack.pushPose();
         stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         consumers.forEach(consumer -> consumer.accept(stack, immediate));
         stack.popPose();
      }
   }

   public static void afterRender(WorldRendererContext context) {
      END_RENDERS.values().forEach(consumer -> consumer.accept(context));
   }

   public static void renderBeforeParticles(PoseStack stack, Vec3 cameraPos, LootBeamBuffer immediate) {
      renderAfter(stack, cameraPos, immediate, RENDER_BEFORE_PARTICLES);
   }

   public static void renderAfterTranslucent(PoseStack stack, Vec3 cameraPos, LootBeamBuffer immediate) {
      renderAfter(stack, cameraPos, immediate, RENDER_AFTER_TRANSLUCENT);
   }

   public static void renderAfterWeather(PoseStack stack, Vec3 cameraPos, LootBeamBuffer immediate) {
      renderAfter(stack, cameraPos, immediate, RENDER_AFTER_WEATHER);
   }

   public static void renderBeforeEnd(PoseStack stack, Vec3 cameraPos, LootBeamBuffer immediate) {
      renderAfter(stack, cameraPos, immediate, RENDER_BEFORE_END);
   }

   public static void addRenderBeforeParticles(BiConsumer<PoseStack, LootBeamBuffer> render) {
      RENDER_BEFORE_PARTICLES.add(render);
   }

   public static void addRenderAfterTranslucent(BiConsumer<PoseStack, LootBeamBuffer> render) {
      RENDER_AFTER_TRANSLUCENT.add(render);
   }

   public static void setRendering(ItemEntity itemEntity, boolean isRendering) {
      ITEM_RENDER_STATES.put(itemEntity, isRendering);
   }

   public static boolean isRendering(ItemEntity itemEntity) {
      return ITEM_RENDER_STATES.getOrDefault(itemEntity, false);
   }

   public static void addRenderAfterWeather(BiConsumer<PoseStack, LootBeamBuffer> render) {
      RENDER_AFTER_WEATHER.add(render);
   }

   public static void addRenderBeforeEnd(BiConsumer<PoseStack, LootBeamBuffer> render) {
      RENDER_BEFORE_END.add(render);
   }

   public static void addOnRenderEnd(Class<?> parentClass, Consumer<WorldRendererContext> render) {
      if (!END_RENDERS.containsKey(parentClass)) {
         END_RENDERS.put(parentClass, render);
      }
   }
}
