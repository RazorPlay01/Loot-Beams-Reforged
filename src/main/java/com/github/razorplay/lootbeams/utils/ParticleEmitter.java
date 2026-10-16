package com.github.razorplay.lootbeams.utils;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.helpers.ColorHelper;
import com.github.razorplay.lootbeams.helpers.RarityHelper;
import com.github.razorplay.lootbeams.managers.ParticleManager;
import com.github.razorplay.lootbeams.vfx.VFXParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.WeakHashMap;

public class ParticleEmitter {
    private ParticleEmitter() {
    }

    private static final Random RANDOM = new Random();
    private static final float TICK_PER_SECOND = 20.0F;
    private static final WeakHashMap<ItemEntity, Float> nextParticleSpawnTicks = new WeakHashMap<>();

    public static void createParticlesForItem(ItemEntity itemEntity, Configuration itemConfig,
                                              int entityTime, TextColor color, float alpha, float pticks) {
        if (!shouldCreateParticles(itemEntity, itemConfig)) {
            return;
        }

        spawnParticleIfReady(itemEntity, itemConfig, entityTime, color, alpha, pticks);
    }

    private static boolean shouldCreateParticles(ItemEntity itemEntity, Configuration itemConfig) {
        return itemConfig.particles
                && (!itemConfig.particleRareOnly || RarityHelper.rarityCheck(itemEntity.getItem(), false))
                && !Minecraft.getInstance().isPaused();
    }

    private static void spawnParticleIfReady(ItemEntity itemEntity, Configuration itemConfig,
                                             int entityTime, TextColor color, float alpha, float pticks) {
        initializeNextSpawnTick(itemEntity, itemConfig);

        float ticksPerOneParticle = TICK_PER_SECOND / itemConfig.particleCount;
        updateSpawnTickIfCycleReset(itemEntity, ticksPerOneParticle, entityTime);

        float currentTickValue = (entityTime % TICK_PER_SECOND) + pticks;

        if (isTimeToSpawn(itemEntity, currentTickValue)) {
            updateNextSpawnTick(itemEntity, currentTickValue, ticksPerOneParticle);
            createAndAddParticle(itemEntity, itemConfig, color, alpha);
        }
    }

    private static void initializeNextSpawnTick(ItemEntity itemEntity, Configuration itemConfig) {
        nextParticleSpawnTicks.putIfAbsent(itemEntity, TICK_PER_SECOND / itemConfig.particleCount);
    }

    private static void updateSpawnTickIfCycleReset(ItemEntity itemEntity, float ticksPerParticle, int entityTime) {
        if ((entityTime % TICK_PER_SECOND) == 0.0F && nextParticleSpawnTicks.get(itemEntity) >= TICK_PER_SECOND) {
            nextParticleSpawnTicks.put(itemEntity, ticksPerParticle);
        }
    }

    private static boolean isTimeToSpawn(ItemEntity itemEntity, float currentTickValue) {
        return currentTickValue >= nextParticleSpawnTicks.get(itemEntity);
    }

    private static void updateNextSpawnTick(ItemEntity itemEntity, float currentTickValue, float ticksPerParticle) {
        float nextTick = ((float) Math.floor(currentTickValue / ticksPerParticle) + 1.0F) * ticksPerParticle;
        nextParticleSpawnTicks.put(itemEntity, nextTick);
    }

    private static void createAndAddParticle(ItemEntity itemEntity, Configuration itemConfig,
                                             TextColor color, float alpha) {
        ParticleData particleData = createParticleData(itemConfig, color);
        Vec3 spawnPos = calculateSpawnPosition(itemEntity, itemConfig);
        Vec3 velocity = calculateVelocity(itemConfig);

        addParticle(
                particleData.texture,
                particleData.color.fR,
                particleData.color.fG,
                particleData.color.fB,
                alpha,
                itemConfig.particleLifetime,
                particleData.size,
                spawnPos,
                velocity,
                itemEntity
        );
    }

    private static ParticleData createParticleData(Configuration itemConfig, TextColor color) {
        ColorHelper.Color particleColor = ColorHelper.Color.of(color);
        float size = itemConfig.particleRandomSize
                ? RANDOM.nextFloat(0.25F * itemConfig.particleSize, 1.1F * itemConfig.particleSize)
                : itemConfig.particleSize;

        return new ParticleData(particleColor, size, itemConfig.particleTexture);
    }

    private static Vec3 calculateSpawnPosition(ItemEntity itemEntity, Configuration itemConfig) {
        double x = RANDOM.nextDouble(
                itemEntity.getX() - itemConfig.particleRadius,
                itemEntity.getX() + itemConfig.particleRadius
        );

        double y = itemConfig.particleRandomY
                ? RANDOM.nextDouble(
                itemEntity.getY() + itemConfig.particleYOffset - itemConfig.particleRadius / 3.0F,
                itemEntity.getY() + itemConfig.particleYOffset + itemConfig.particleRadius / 3.0F
        )
                : itemEntity.getY() + itemConfig.particleYOffset + itemConfig.particleSize / 10.0F;

        double z = RANDOM.nextDouble(
                itemEntity.getZ() - itemConfig.particleRadius,
                itemEntity.getZ() + itemConfig.particleRadius
        );

        return new Vec3(x, y, z);
    }

    private static Vec3 calculateVelocity(Configuration itemConfig) {
        Vec3 randomDir = new Vec3(
                RANDOM.nextDouble(-itemConfig.particleSpeed / 2.0F, itemConfig.particleSpeed / 2.0F),
                itemConfig.particleRandomY
                        ? RANDOM.nextDouble(itemConfig.particleSpeed / 2.0F, itemConfig.particleSpeed)
                        : itemConfig.particleSpeed,
                RANDOM.nextDouble(-itemConfig.particleSpeed / 2.0F, itemConfig.particleSpeed / 2.0F)
        ).multiply(itemConfig.randomnessIntensity, itemConfig.randomnessIntensity, itemConfig.randomnessIntensity);

        return new Vec3(itemConfig.particleDirectionX, itemConfig.particleDirectionY, itemConfig.particleDirectionZ)
                .multiply(randomDir);
    }

    private static void addParticle(
            ParticleManager.ParticleTexture texture,
            float red,
            float green,
            float blue,
            float alpha,
            int lifetime,
            float size,
            Vec3 pos,
            Vec3 motion,
            ItemEntity itemEntity
    ) {
        Minecraft mc = Minecraft.getInstance();
        alpha *= 1.5F;
        VFXParticleType glowParticle = ParticleManager.GLOW_PARTICLE
                .setTexture(texture)
                .setColor(red, green, blue, alpha)
                .setLifetime(lifetime)
                .setSize(size)
                .setSourcePos(itemEntity.position())
                .setGravity(0.0F)
                .setCollision(false)
                .setFullbright(true)
                .setItemStack(itemEntity.getItem());
        if (mc.level != null) {
            mc.level.addParticle(glowParticle, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        }
    }

    private record ParticleData(ColorHelper.Color color, float size, ParticleManager.ParticleTexture texture) {
    }
}
