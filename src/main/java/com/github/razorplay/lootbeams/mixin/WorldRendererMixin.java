package com.github.razorplay.lootbeams.mixin;

//? if <= 1.21.1 {

import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.RenderBuffers;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Final
    @Shadow
    private RenderBuffers renderBuffers;
    @Shadow
    private ClientLevel level;
    @Shadow
    @Nullable
    private PostChain transparencyChain;
    @Unique
    private WorldRendererContext context = new WorldRendererContext();

    @Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At("HEAD"))
    private void beforeRender(
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            GameRenderer gameRenderer,
            LightTexture lightmapTextureManager,
            Matrix4f positionMatrix,
            Matrix4f projectionMatrix,
            CallbackInfo ci
    ) {
        this.context
                .prepare(
                        (LevelRenderer) (Object) this,
                        tickCounter,
                        renderBlockOutline,
                        camera,
                        gameRenderer,
                        lightmapTextureManager,
                        projectionMatrix,
                        positionMatrix,
                        this.renderBuffers.bufferSource(),
                        this.level.getProfiler(),
                        this.transparencyChain != null,
                        this.level
                );
    }

    @ModifyExpressionValue(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(value = "NEW", target = "com/mojang/blaze3d/vertex/PoseStack"))
    private PoseStack setMatrixStack(PoseStack matrixStack) {
        this.context.setMatrixStack(matrixStack);
        return matrixStack;
    }

	//? fabric {
	/*@Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;F)V"
            )
    )*/
	//?} neoforge {
	@Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/particle/ParticleEngine;render(Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;Ljava/util/function/Predicate;)V"
            )
    )
	//?}
    private void beforeParticles(CallbackInfo ci) {
        RenderEvents.BEFORE_PARTICLES.invoker().beforeParticles(this.context);
    }

    @Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;")
    )
    private void afterTranslucent(CallbackInfo ci) {
        RenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(this.context);
    }

    @Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
                    shift = Shift.AFTER
            )
    )
    private void renderAfterWeather(CallbackInfo ci) {
        RenderEvents.AFTER_WEATHER.invoker().afterWeather(this.context);
    }

    @Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderDebug(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/Camera;)V",
                    shift = Shift.AFTER
            )
    )
    private void renderBeforeEnd(CallbackInfo ci) {
        RenderEvents.BEFORE_END.invoker().beforeEnd(this.context);
    }

    @Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At("RETURN"))
    private void afterRender(CallbackInfo ci) {
        RenderEvents.END.invoker().onEnd(this.context);
    }
}
//?} else {
/*import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.util.profiling.Profiler;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/^*
 * 1.21.11 reescribio el render del mundo: {@code renderLevel} solo construye un
 * {@code FrameGraphBuilder} cuyas pasadas se ejecutan de forma diferida, y ya no existen
 * {@code renderSnowAndRain} ni {@code renderDebug(PoseStack, ...)}. Las tres etapas del mod
 * (particulas, translucidos y meteorologia) se acumulan y se lanzan juntas al final de
 * {@code renderLevel}, con un {@code PoseStack} reconstruido desde la modelview del metodo.
 ^/
@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Shadow
    private ClientLevel level;
    @Unique
    private final WorldRendererContext lootbeams$context = new WorldRendererContext();
    @Unique
    private PoseStack lootbeams$matrixStack = new PoseStack();

    @Inject(
            method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
            at = @At("HEAD")
    )
    private void lootbeams$prepareContext(
            GraphicsResourceAllocator resourceAllocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            Camera camera,
            Matrix4f modelViewMatrix,
            Matrix4f projectionMatrix,
            Matrix4f cullProjectionMatrix,
            GpuBufferSlice fogBuffer,
            Vector4f fogColor,
            boolean renderSky,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        this.lootbeams$matrixStack = new PoseStack();
        this.lootbeams$matrixStack.last().pose().set(modelViewMatrix);

        this.lootbeams$context
                .prepare(
                        (LevelRenderer) (Object) this,
                        tickCounter,
                        renderBlockOutline,
                        camera,
                        minecraft.gameRenderer,
                        minecraft.gameRenderer.lightTexture(),
                        projectionMatrix,
                        modelViewMatrix,
                        this.renderBuffers.bufferSource(),
                        Profiler.get(),
                        false,
                        this.level
                )
                .setMatrixStack(this.lootbeams$matrixStack);
    }

    @Inject(
            method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V",
            at = @At("TAIL")
    )
    private void lootbeams$renderStages(CallbackInfo ci) {
        RenderEvents.BEFORE_PARTICLES.invoker().beforeParticles(this.lootbeams$context);
        RenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(this.lootbeams$context);
        RenderEvents.AFTER_WEATHER.invoker().afterWeather(this.lootbeams$context);
        RenderEvents.BEFORE_END.invoker().beforeEnd(this.lootbeams$context);
        RenderEvents.END.invoker().onEnd(this.lootbeams$context);
    }
}
*///?}
