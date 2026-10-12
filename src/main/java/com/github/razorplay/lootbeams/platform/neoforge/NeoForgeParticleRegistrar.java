package com.github.razorplay.lootbeams.platform.neoforge;

//? neoforge {

import com.github.razorplay.lootbeams.ParticleRegistrar;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.function.Function;

public class NeoForgeParticleRegistrar implements ParticleRegistrar {
	private final RegisterParticleProvidersEvent event;

	public NeoForgeParticleRegistrar(RegisterParticleProvidersEvent event) {
		this.event = event;
	}

	@Override
	public <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider) {
		event.registerSpriteSet(type, spriteSet -> provider);
	}

	@Override
	public <T extends ParticleOptions> void registerSpriteSet(
			ParticleType<T> type,
			Function<SpriteSet, ParticleProvider<T>> factory) {
		event.registerSpriteSet(type, factory::apply);
	}
}
//?}
