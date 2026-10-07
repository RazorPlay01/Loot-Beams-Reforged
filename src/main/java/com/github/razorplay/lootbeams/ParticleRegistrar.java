package com.github.razorplay.lootbeams;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

/**
 * Abstrae el registro del provider de una partícula.
 * El tipo de partícula y el factory son comunes; solo cambia el mecanismo.
 */
public interface ParticleRegistrar {
	<T extends ParticleOptions> void register(ParticleType<T> type,
											  ParticleProvider<T> provider);

	/** Variante con SpriteSet (la que usan Fabric y NeoForge por igual). */
	<T extends ParticleOptions> void registerSpriteSet(ParticleType<T> type,
													   java.util.function.Function<SpriteSet, ParticleProvider<T>> factory);
}
