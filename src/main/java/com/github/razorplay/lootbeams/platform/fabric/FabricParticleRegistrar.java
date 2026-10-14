package com.github.razorplay.lootbeams.platform.fabric;

//? fabric {

import com.github.razorplay.lootbeams.ParticleRegistrar;
//? if >= 26.1.2 {
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
//?} else {
/*import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
*///?}
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

import java.util.function.Function;

public class FabricParticleRegistrar implements ParticleRegistrar {

    @Override
    public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
        //? if >= 26.1.2 {
        ParticleProviderRegistry.getInstance().register(type, provider);
        //?} else {
        /*ParticleFactoryRegistry.getInstance().register(type, provider);
        *///?}
    }

    @Override
    public <T extends ParticleOptions> void registerSpriteSet(
            ParticleType<T> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        //? if >= 26.1.2 {
        ParticleProviderRegistry.getInstance().register(type, factory::apply);
        //?} else {
        /*ParticleFactoryRegistry.getInstance().register(type, factory::apply);
        *///?}
    }
}
//?}
