package com.github.razorplay.lootbeams.platform.fabric;

//? fabric {

/*import com.github.razorplay.lootbeams.ParticleRegistrar;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public class FabricParticleRegistrar implements ParticleRegistrar {

    @Override
    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        ParticleFactoryRegistry.getInstance().register(type, provider);
    }

    @Override
    public <T extends ParticleOptions> void registerSpriteSet(
            ParticleType<T> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        ParticleFactoryRegistry.getInstance().register(type, factory::apply);
    }
}
*///?}
