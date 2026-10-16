package com.github.razorplay.lootbeams.contexts;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
//? if >= 26.1.2 {
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.textures.GpuTextureView;
*///?} else {
import com.mojang.blaze3d.textures.GpuTextureView;
//?}
//?} else {
/*import net.minecraft.client.renderer.LightTexture;
*///?}
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4f;

public class WorldRendererContext {
   private LevelRenderer worldRenderer;
   private DeltaTracker tickCounter;
   private PoseStack matrixStack;
   private boolean blockOutlines;
   private Camera camera;
   private GameRenderer gameRenderer;
   //? if >= 26.1.2 {
   private GpuTextureView lightmapTextureManager;
   //?} else {
   /*private LightTexture lightmapTextureManager;
   *///?}
   private Matrix4f projectionMatrix;
   private Matrix4f positionMatrix;
   private LootBeamBuffer consumers;
   private ProfilerFiller profiler;
   private boolean advancedTranslucency;
   private ClientLevel world;

   public WorldRendererContext prepare(
      LevelRenderer worldRenderer,
      DeltaTracker delta,
      boolean blockOutlines,
      Camera camera,
      GameRenderer gameRenderer,
      //? if >= 26.1.2 {
      GpuTextureView lightmapTextureManager,
      //?} else {
      /*LightTexture lightmapTextureManager,
      *///?}
      Matrix4f projectionMatrix,
      Matrix4f positionMatrix,
      LootBeamBuffer consumers,
      ProfilerFiller profiler,
      boolean advancedTranslucency,
      ClientLevel world
   ) {
      this.worldRenderer = worldRenderer;
      this.tickCounter = delta;
      this.matrixStack = null;
      this.blockOutlines = blockOutlines;
      this.camera = camera;
      this.gameRenderer = gameRenderer;
      this.lightmapTextureManager = lightmapTextureManager;
      this.projectionMatrix = projectionMatrix;
      this.positionMatrix = positionMatrix;
      this.consumers = consumers;
      this.profiler = profiler;
      this.advancedTranslucency = advancedTranslucency;
      this.world = world;
      return this;
   }

   public PoseStack getMatrixStack() {
      return this.matrixStack;
   }

   public WorldRendererContext setMatrixStack(PoseStack matrixStack) {
      this.matrixStack = matrixStack;
      return this;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public WorldRendererContext setCamera(Camera camera) {
      this.camera = camera;
      return this;
   }

   //? if >= 26.1.2 {
   public GpuTextureView getLightmapTextureManager() {
   //?} else {
   /*public LightTexture getLightmapTextureManager() {
   *///?}
      return this.lightmapTextureManager;
   }

   //? if >= 26.1.2 {
   public WorldRendererContext setLightmapTextureManager(GpuTextureView lightmapTextureManager) {
   //?} else {
   /*public WorldRendererContext setLightmapTextureManager(LightTexture lightmapTextureManager) {
   *///?}
      this.lightmapTextureManager = lightmapTextureManager;
      return this;
   }

   public LevelRenderer getWorldRenderer() {
      return this.worldRenderer;
   }

   public WorldRendererContext setWorldRenderer(LevelRenderer worldRenderer) {
      this.worldRenderer = worldRenderer;
      return this;
   }

   public DeltaTracker getTickCounter() {
      return this.tickCounter;
   }

   public WorldRendererContext setTickCounter(DeltaTracker tickCounter) {
      this.tickCounter = tickCounter;
      return this;
   }

   public boolean isBlockOutlines() {
      return this.blockOutlines;
   }

   public WorldRendererContext setBlockOutlines(boolean blockOutlines) {
      this.blockOutlines = blockOutlines;
      return this;
   }

   public GameRenderer getGameRenderer() {
      return this.gameRenderer;
   }

   public WorldRendererContext setGameRenderer(GameRenderer gameRenderer) {
      this.gameRenderer = gameRenderer;
      return this;
   }

   public Matrix4f getProjectionMatrix() {
      return this.projectionMatrix;
   }

   public WorldRendererContext setProjectionMatrix(Matrix4f projectionMatrix) {
      this.projectionMatrix = projectionMatrix;
      return this;
   }

   public Matrix4f getPositionMatrix() {
      return this.positionMatrix;
   }

   public WorldRendererContext setPositionMatrix(Matrix4f positionMatrix) {
      this.positionMatrix = positionMatrix;
      return this;
   }

   public LootBeamBuffer getConsumers() {
      return this.consumers;
   }

   public WorldRendererContext setConsumers(LootBeamBuffer consumers) {
      this.consumers = consumers;
      return this;
   }

   public boolean isAdvancedTranslucency() {
      return this.advancedTranslucency;
   }

   public WorldRendererContext setAdvancedTranslucency(boolean advancedTranslucency) {
      this.advancedTranslucency = advancedTranslucency;
      return this;
   }

   public ClientLevel getWorld() {
      return this.world;
   }

   public WorldRendererContext setWorld(ClientLevel world) {
      this.world = world;
      return this;
   }

   public ProfilerFiller profiler() {
      return this.profiler;
   }
}
