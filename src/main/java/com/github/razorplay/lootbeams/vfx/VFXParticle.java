package com.github.razorplay.lootbeams.vfx;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.managers.RenderManager;
import com.github.razorplay.lootbeams.render.LootBeamRenderLayers;
import com.github.razorplay.lootbeams.render.ParticleRenderType;
import com.github.razorplay.lootbeams.shaders.LootBeamShaders;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;

public class VFXParticle extends TextureSheetParticle {
    private final boolean fullbright;
    private boolean stoppedByCollision;
    private boolean hasTrail = false;
    private float yHeightCompensation = 0.5F;
    private static final Identifier TEXTURE = ModTemplate.id("textures/trail/white.png");
    Trail trail;
    Vec3 particleCenter = Vec3.ZERO;
    Vec3 axis;
    ItemStack itemStack;
    ParticleManager.ParticleTexture texture;
    Configuration config;
    private long lastUpdate = 0L;
    private static final int TARGET_FPS = 60;
    private static final long TARGET_FRAME_TIME = 1000L / TARGET_FPS;
    private int currentFrame = 0;
    private int tickDelayCounter = 0;
    public static final ParticleRenderType RENDER_TYPE = new ParticleRenderType();

    public VFXParticle(
            ClientLevel world,
            ParticleManager.ParticleTexture texture,
            ItemStack itemStack,
            float r,
            float g,
            float b,
            float a,
            int lifetime,
            float size,
            Vec3 pos,
            Vec3 motion,
            float gravity,
            boolean collision,
            boolean fullbright
    ) {
        super(world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        this.texture = texture;
        this.setSprite(this.texture.getSprite());
        this.itemStack = itemStack;
        this.config = CustomLootBeamsConfig.fromItemStack(this.itemStack);
        this.axis = new Vec3(0.0, this.config.beamYOffset + this.config.beamHeight + this.yHeightCompensation, 0.0);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = Math.min(a, 1.0F);
        this.lifetime = lifetime + 5;
        this.setSize(size);
        this.xd = motion.x;
        this.yd = motion.y;
        this.zd = motion.z;
        this.gravity = gravity;
        this.hasPhysics = collision;
        this.fullbright = fullbright;
        this.hasTrail = Math.random() < this.config.trailChance;
        if (this.hasTrail && this.config.trailParticlesInvisible) {
            this.setSize(1.0E-5F);
        }

        if (this.config.trails && this.hasTrail) {
            this.trail = new Trail(
                    FastColor.ARGB32.color((int) (r * 255.0F), (int) (g * 255.0F), (int) (b * 255.0F), (int) (a * 255.0F)),
                    width -> (float) ((float) Math.sin(width.floatValue() * 3.15) / 2.0F * 0.09F * this.config.trailWidth * (1.0 + Math.random()))
            );
            this.trail.setStack(this.itemStack);
            this.trail.setColor(r, g, b, a);
            this.trail.setBillboard(true);
            this.trail.setLength((int) (this.config.trailLength * (1.0 + Math.random())));
            this.trail.setFrequency(this.config.trailFrequency);
            this.trail.pushPoint(new Vec3(this.x, this.y, this.z));
        }
    }

    private void updateTrailPoints(float partialTicks) {
        if (this.config.trails && this.hasTrail && this.trail != null) {
            this.trail
                    .pushPoint(
                            new Vec3(
                                    (float) Mth.lerp(partialTicks, this.xo, this.x),
                                    (float) Mth.lerp(partialTicks, this.yo, this.y),
                                    (float) Mth.lerp(partialTicks, this.zo, this.z)
                            )
                    );
        }
    }

    private Vector3f[] calculateParticleVectors(Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf = new Quaternionf(camera.rotation());
        if (this.roll != 0.0F) {
            quaternionf.rotateZ(Mth.lerp(partialTicks, this.oRoll, this.roll));
        }

        quaternionf.mul(new Quaternionf(0.0, Math.toRadians(90.0), 0.0, 0.0));
        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        return avector3f;
    }

    private void renderParticles(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        Vector3f[] particleVectors = this.calculateParticleVectors(camera, partialTicks);
        float minX = this.getU0();
        float maxX = this.getU1();
        float minY = this.getV0();
        float maxY = this.getV1();
        int j = this.getLightColor(partialTicks);
        boolean particleInheritsColor = this.config.particleInheritsColor && !this.texture.isColored();
        float averageColor = LootBeamShaders.getAverageColor(this.config.particleColorMode);
        float R = particleInheritsColor ? this.rCol : averageColor;
        float G = particleInheritsColor ? this.gCol : averageColor;
        float B = particleInheritsColor ? this.bCol : averageColor;
        float A = particleInheritsColor ? this.alpha : 1.0F;
        vertexConsumer.addVertex(particleVectors[0].x(), particleVectors[0].y(), particleVectors[0].z())
                .setUv(maxX, maxY)
                .setColor(R, G, B, A)
                .setLight(j)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(particleVectors[1].x(), particleVectors[1].y(), particleVectors[1].z())
                .setUv(maxX, minY)
                .setColor(R, G, B, A)
                .setLight(j)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(particleVectors[2].x(), particleVectors[2].y(), particleVectors[2].z())
                .setUv(minX, minY)
                .setColor(R, G, B, A)
                .setLight(j)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0.0F, 1.0F, 0.0F);
        vertexConsumer.addVertex(particleVectors[3].x(), particleVectors[3].y(), particleVectors[3].z())
                .setUv(minX, maxY)
                .setColor(R, G, B, A)
                .setLight(j)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private void renderTrail() {
        if (this.config.trails && this.hasTrail && this.trail != null) {
            this.trail.setColor(this.rCol, this.gCol, this.bCol, this.alpha);
            this.trail.setCenterPoint(this.particleCenter);
            RenderManager.addRenderBeforeEnd(
                    (matrixStack, consumer) -> this.trail
                            .render(matrixStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(LootBeamRenderLayers.translucentNoCull(TEXTURE)))
            );
        }
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        long currentTime = System.currentTimeMillis();
        this.renderParticles(vertexConsumer, camera, partialTicks);
        if (currentTime - this.lastUpdate > TARGET_FRAME_TIME) {
            this.updateTrailPoints(partialTicks);
            this.lastUpdate = currentTime;
        }

        this.renderTrail();
    }

    public void setParticleCenter(Vec3 particleCenter) {
        this.particleCenter = particleCenter;
    }

    private void applyForce() {
        Vec3 particleToCenter = this.particleCenter.subtract(this.getPosition());
        Vec3 particleToCenterOnAxis = particleToCenter.subtract(this.axis.scale(particleToCenter.dot(this.axis)));
        Vec3 particleToCenterOnAxisUnit = particleToCenterOnAxis.normalize();
        Vec3 particleToCenterOnAxisUnitCrossVortexAxis = particleToCenterOnAxisUnit.cross(this.axis);
        Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnit = particleToCenterOnAxisUnitCrossVortexAxis.normalize();
        Vec3 particleToCenterOnAxisUnitCrossVortexAxisUnitScaled = particleToCenterOnAxisUnitCrossVortexAxisUnit.scale(0.01F);
        this.xd = this.xd + particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.x * 0.65;
        this.yd = this.yd + particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.y;
        this.zd = this.zd + particleToCenterOnAxisUnitCrossVortexAxisUnitScaled.z * 0.65;
        Vec3 target = this.particleCenter.add(this.axis.add(0.0, this.config.beamYOffset, 0.0));
        Vec3 particleToTarget = target.subtract(this.getPosition());
        Vec3 particleToTargetUnit = particleToTarget.normalize();
        int mod = this.config.particleDirectionY >= 0.0F ? (this.y > target.y ? 0 : 1) : (this.y < target.y ? 0 : -1);
        Vec3 particleToTargetUnitScaled = particleToTargetUnit.multiply(
                this.config.particleSpeedX * mod, this.config.particleSpeedY * mod, this.config.particleSpeedZ * mod
        );
        this.xd = this.xd + particleToTargetUnitScaled.x;
        this.yd = this.yd + (this.config.particleUseConstantVerticalSpeed ? this.config.particleSpeedY : particleToTargetUnitScaled.y);
        this.zd = this.zd + particleToTargetUnitScaled.z;
    }

    private Vec3 getPosition() {
        return new Vec3(this.x, this.y, this.z);
    }

    protected int getLightColor(float tint) {
        return this.fullbright ? LightTexture.pack(15, 15) : super.getLightColor(tint);
    }

    public void setSize(float size) {
        this.quadSize = size / 10.0F;
        this.setSize(size / 10.0F, size / 10.0F);
    }

    private void updateParticleSprite() {
        if (this.texture.isSplitted()) {
            this.tickDelayCounter++;
            if (this.tickDelayCounter >= this.config.tickPerParticleSpriteUpdate) {
                this.currentFrame++;
                if (this.currentFrame >= this.texture.getFrameCount()) {
                    this.currentFrame = 0;
                }

                this.tickDelayCounter = 0;
            }
        }
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.config.spinAroundBeam) {
            this.applyForce();
        }

        if (this.texture.isSplitted() && this.config.tickPerParticleSpriteUpdate > 0) {
            this.updateParticleSprite();
            this.setSprite(this.texture.getSprite(this.currentFrame));
        }

        if (this.age > this.lifetime - 5) {
            this.alpha = 1.0F - (this.age - (this.lifetime - 5)) / 5.0F;
        }

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
        }
    }

    public void move(double x, double y, double z) {
        if (!this.stoppedByCollision) {
            double dX = x;
            double dY = y;
            double dZ = z;
            if (this.hasPhysics && (x != 0.0 || y != 0.0 || z != 0.0)) {
                Vec3 vector3d = Entity.collideBoundingBox(null, new Vec3(x, y, z), this.getBoundingBox(), this.level, List.of());
                x = vector3d.x;
                y = vector3d.y;
                z = vector3d.z;
            }

            if (x == 0.0 && y == 0.0 && z == 0.0) {
                this.stoppedByCollision = true;
            } else {
                this.setBoundingBox(this.getBoundingBox().move(x, y, z));
                this.setLocationFromBoundingbox();
            }

            if (dX != x) {
                this.xd = 0.0;
            }

            if (dY != y) {
                this.yd = 0.0;
            }

            if (dZ != z) {
                this.zd = 0.0;
            }
        }
    }

    public ParticleRenderType getRenderType() {
        return this.config.particleColorMode != null
                ? RENDER_TYPE.setTexture(this.sprite.atlasLocation()).setShader(this.config.particleColorMode)
                : RENDER_TYPE.setTexture(this.sprite.atlasLocation());
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory(SpriteSet spriteSet) {
        }

        @Nullable
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velX, double velY, double velZ) {
            if (parameters instanceof VFXParticleType type) {
                VFXParticle vfxParticle = new VFXParticle(
                        world,
                        type.texture,
                        type.itemStack,
                        type.red,
                        type.green,
                        type.blue,
                        type.alpha,
                        type.lifetime,
                        type.size,
                        new Vec3(x, y, z),
                        new Vec3(velX, velY, velZ),
                        type.gravity,
                        type.collision,
                        type.fullbright
                );
                vfxParticle.setParticleCenter(type.sourcePos);
                return vfxParticle;
            } else {
                return null;
            }
        }
    }
}
