package com.github.razorplay.lootbeams.extensions;

import net.minecraft.client.Camera;
//? if >= 26.1.2 {
//? if >= 26.3 {
/*import com.mojang.renderpearl.api.textures.GpuTextureView;
*///?} else {
import com.mojang.blaze3d.textures.GpuTextureView;
//?}
//?} else {
/*import net.minecraft.client.renderer.LightTexture;
*///?}

public interface LootbeamsParticleManager {
   //? if >= 26.1.2 {
   void renderCustomParticles(GpuTextureView var1, Camera var2, float var3);
   //?} else {
   /*void renderCustomParticles(LightTexture var1, Camera var2, float var3);
   *///?}
}
