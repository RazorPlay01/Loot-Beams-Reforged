package com.github.razorplay.lootbeams.mixin;

//? if <= 1.21.1 {

/*import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
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
                        LootBeamBuffer.of(this.renderBuffers.bufferSource()),
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
	@Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "__LOOTBEAMS_PARTICLE_RENDER_TARGET__"
            )
    )
	//?} neoforge {
	/^@Inject(
            method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V",
            at = @At(
                    value = "INVOKE",
                    target = "__LOOTBEAMS_PARTICLE_RENDER_TARGET__"
            )
    )
	^///?}
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
*///?} else {
//? if >= 26.1.2 {
import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.helpers.VersionCompatHelper;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.1.2 introduce los "submit nodes": la geometria del mundo se recolecta en
// submitEntities(poseStack, state, collector) y MultiBufferSource deja de volcar. El mod se engancha
// al final de esa fase para someter sus vigas con el collector del frame (mismo collector y mismo
// orden que las entidades).
@Mixin(LevelRenderer.class)
public class WorldRendererMixin {
    @Unique
    private final WorldRendererContext lootbeams$context = new WorldRendererContext();

    @Inject(
            method = "submitEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V",
            at = @At("TAIL")
    )
    private void lootbeams$submitWorldGeometry(
            PoseStack poseStack,
            LevelRenderState renderState,
            SubmitNodeCollector collector,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        // 26.1.2 recolecta la geometria del mundo igual que 26.2/26.3: submitEntities recibe un
        // PoseStack en IDENTIDAD y las entidades se someten con coordenadas relativas a la camara
        // (vanilla usa state.x - camX, ver LevelRenderer.submitEntities). El engine aplica despues la
        // modelview del frame (CameraRenderState.viewRotationMatrix) al dibujar los nodos, asi que la
        // pose debe quedarse en identidad: bakear aqui la rotacion o la posicion de la camara haria
        // que se aplicasen dos veces y la geometria acabaria fuera del frustum (nada visible).
        // RenderManager sigue aportando el desplazamiento por la posicion de la camara.
        CameraRenderState cameraState = renderState.cameraRenderState;
        Matrix4f projectionMatrix = new Matrix4f(cameraState.projectionMatrix);
        Matrix4f modelViewMatrix = new Matrix4f(cameraState.viewRotationMatrix);
        PoseStack matrixStack = new PoseStack();

        this.lootbeams$context
                .prepare(
                        (LevelRenderer) (Object) this,
                        minecraft.getDeltaTracker(),
                        false,
                        VersionCompatHelper.mainCamera(minecraft.gameRenderer),
                        minecraft.gameRenderer,
                        minecraft.gameRenderer.lightmap(),
                        projectionMatrix,
                        modelViewMatrix,
                        LootBeamBuffer.of(collector),
                        Profiler.get(),
                        false,
                        minecraft.level
                )
                .setMatrixStack(matrixStack);

        RenderEvents.BEFORE_PARTICLES.invoker().beforeParticles(this.lootbeams$context);
        RenderEvents.AFTER_TRANSLUCENT.invoker().afterTranslucent(this.lootbeams$context);
        RenderEvents.AFTER_WEATHER.invoker().afterWeather(this.lootbeams$context);
        RenderEvents.BEFORE_END.invoker().beforeEnd(this.lootbeams$context);
        RenderEvents.END.invoker().onEnd(this.lootbeams$context);
    }
}
//?} elif >= 26.1.2 {
/*import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.profiling.Profiler;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 26.1.2 reescribio renderLevel: la camara llega como CameraRenderState (ya no Camera) y desaparecen
// los parametros GameRenderer/LightTexture y las tres matrices; aparece ChunkSectionsToRender.
// La modelview se reconstruye desde viewRotationMatrix + pos del estado de camara y la camara y el
// lightmap se toman de GameRenderer. Sigue siendo render inmediato (RenderBuffers), por lo que se
// mantiene la ruta MultiBufferSource de LootBeamBuffer hasta 26.2.
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
            method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V",
            at = @At("HEAD")
    )
    private void lootbeams$prepareContext(
            GraphicsResourceAllocator resourceAllocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            CameraRenderState cameraState,
            Matrix4fc projection,
            GpuBufferSlice fogBuffer,
            Vector4f fogColor,
            boolean renderSky,
            ChunkSectionsToRender chunkSections,
            CallbackInfo ci
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Matrix4f projectionMatrix = new Matrix4f(projection);
        Matrix4f modelViewMatrix = new Matrix4f(cameraState.viewRotationMatrix);
        modelViewMatrix.translate((float) -cameraState.pos.x, (float) -cameraState.pos.y, (float) -cameraState.pos.z);

        this.lootbeams$matrixStack = new PoseStack();
        this.lootbeams$matrixStack.last().pose().set(modelViewMatrix);

        this.lootbeams$context
                .prepare(
                        (LevelRenderer) (Object) this,
                        tickCounter,
                        renderBlockOutline,
                        camera,
                        minecraft.gameRenderer,
                        minecraft.gameRenderer.lightmap(),
                        projectionMatrix,
                        modelViewMatrix,
                        LootBeamBuffer.of(this.renderBuffers.bufferSource()),
                        Profiler.get(),
                        false,
                        this.level
                )
                .setMatrixStack(this.lootbeams$matrixStack);
    }

    @Inject(
            method = "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V",
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
*///?} else {
/*import com.github.razorplay.lootbeams.contexts.WorldRendererContext;
import com.github.razorplay.lootbeams.events.RenderEvents;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
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

// 1.21.11 reescribio el render del mundo: {@code renderLevel} solo construye un
// {@code FrameGraphBuilder} cuyas pasadas se ejecutan de forma diferida, y ya no existen
// {@code renderSnowAndRain} ni {@code renderDebug(PoseStack, ...)}. Las tres etapas del mod
// (particulas, translucidos y meteorologia) se acumulan y se lanzan juntas al final de
// {@code renderLevel}, con un {@code PoseStack} reconstruido desde la modelview del metodo.
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
                        //? if >= 26.1.2 {
                        minecraft.gameRenderer.lightmap(),
                        //?} else {
                        /^minecraft.gameRenderer.lightTexture(),
                        ^///?}
                        projectionMatrix,
                        modelViewMatrix,
                        LootBeamBuffer.of(this.renderBuffers.bufferSource()),
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
//?}

