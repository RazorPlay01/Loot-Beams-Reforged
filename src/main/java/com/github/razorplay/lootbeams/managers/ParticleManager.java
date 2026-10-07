package com.github.razorplay.lootbeams.managers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.ParticleRegistrar;
import com.github.razorplay.lootbeams.extensions.AnimatedTexture;
import com.github.razorplay.lootbeams.vfx.VFXParticle;
import com.github.razorplay.lootbeams.vfx.VFXParticleType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ParticleManager {
	public static final ResourceLocation GLOW_TEXTURE_ID = ModTemplate.id("glow");
	public static final ParticleTexture GLOW_TEXTURE = ParticleTexture.of(GLOW_TEXTURE_ID);
	public static VFXParticleType GLOW_PARTICLE;

	/**
	 * Crea el ParticleType pero NO lo registra.
	 * El registro lo hace cada loader en su evento correspondiente.
	 */
	public static VFXParticleType createGlowParticleType() {
		return new VFXParticleType(true);
	}

	/**
	 * Registra el provider. Se llama desde el evento del loader
	 * (Fabric: ParticleFactoryRegistry / NeoForge: RegisterParticleProvidersEvent).
	 */
	public static void registerParticleProviders(ParticleRegistrar registrar) {
		registrar.registerSpriteSet(GLOW_PARTICLE, VFXParticle.Factory::new);
	}

	public static List<ParticleTexture> getTextures() {
		return ParticleTexture.getAnimatedTextures(ParticleTexture.getAtlasTexture(), ParticleTexture.class);
	}

	public static class ParticleTexture extends AnimatedTexture {
		public ParticleTexture(ResourceLocation textureId) {
			super(textureId);
		}

		public static ParticleTexture of(ResourceLocation textureId) {
			return of(textureId, ParticleTexture.class);
		}

		public static ParticleTexture of(String path, String namespace) {
			return of(path, namespace, ParticleTexture.class);
		}

		public static TextureAtlas getAtlasTexture() {
			return Minecraft.getInstance().particleEngine.textureAtlas;
		}

		@Override
		public TextureAtlas getSpriteAtlasTexture() {
			return getAtlasTexture();
		}
	}
}
