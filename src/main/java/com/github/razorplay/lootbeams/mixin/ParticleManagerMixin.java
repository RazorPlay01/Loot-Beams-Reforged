package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.extensions.LootbeamsParticleManager;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
//? if >= 26.1.2 {
//? if >= 26.3 {
import com.mojang.renderpearl.api.textures.GpuTextureView;
//?} else {
/*import com.mojang.blaze3d.textures.GpuTextureView;
*///?}
//?} else {
/*import net.minecraft.client.renderer.LightTexture;
*///?}
import org.spongepowered.asm.mixin.Mixin;

//? <= 1.21.1 {
/*import com.github.razorplay.lootbeams.vfx.VFXParticle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public class ParticleManagerMixin implements LootbeamsParticleManager {
    @Unique
    public boolean customParticlesRender = false;
    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> particles;
    @Shadow
    @Final
    public TextureManager textureManager;

    @Override
    public void renderCustomParticles(LightTexture lightmapTextureManager, Camera camera, float tickDelta) {
        ParticleEngine particleManager = (ParticleEngine) (Object) this;
        this.customParticlesRender = true;
        particleManager.render(lightmapTextureManager, camera, tickDelta);
        this.customParticlesRender = false;
    }

    @Inject(method = "render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V", at = @At("HEAD"), cancellable = true)
    private void renderLootBeamParticles(LightTexture lightmapTextureManager, Camera camera, float tickDelta, CallbackInfo ci) {
        if (this.customParticlesRender) {
            ci.cancel();
            Queue<Particle> queue = this.particles.get(VFXParticle.RENDER_TYPE);
            if (queue != null && !queue.isEmpty()) {
                lightmapTextureManager.turnOnLightLayer();
                RenderSystem.enableDepthTest();
                Tesselator tessellator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = VFXParticle.RENDER_TYPE.begin(tessellator, this.textureManager);
                if (bufferBuilder != null) {
                    for (Particle particle : queue) {
                        try {
                            particle.render(bufferBuilder, camera, tickDelta);
                        } catch (Throwable ex) {
                            CrashReport crashReport = CrashReport.forThrowable(ex, "Rendering Particle");
                            CrashReportCategory crashReportSection = crashReport.addCategory("Particle being rendered");
                            crashReportSection.setDetail("Particle", particle::toString);
                            throw new ReportedException(crashReport);
                        }
                    }

                    MeshData builtBuffer = bufferBuilder.build();
                    if (builtBuffer != null) {
                        BufferUploader.drawWithShader(builtBuffer);
                    }
                }

                VFXParticle.RENDER_TYPE.end(tessellator);
                lightmapTextureManager.turnOffLightLayer();
                RenderSystem.disableDepthTest();
            }
        }
    }
}
*///?} else {
@Mixin(ParticleEngine.class)
public class ParticleManagerMixin implements LootbeamsParticleManager {
    @Override
    //? if >= 26.1.2 {
    public void renderCustomParticles(GpuTextureView lightmapTextureManager, Camera camera, float tickDelta) {
    //?} else {
    /*public void renderCustomParticles(LightTexture lightmapTextureManager, Camera camera, float tickDelta) {
    *///?}
        // 1.21.11: las particulas del mod se dibujan con el sistema nativo de particulas
        // (SingleQuadParticle + ParticleEngine.extract), asi que ya no hace falta una pasada de
        // render propia ni el grupo de particulas personalizado.
    }
}
//?}
