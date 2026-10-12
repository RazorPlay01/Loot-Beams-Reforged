package com.github.razorplay.lootbeams.managers;

import com.github.razorplay.lootbeams.ModTemplate;
import com.github.razorplay.lootbeams.extensions.AnimatedTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class GlowEffectManager {
   public static final ResourceLocation GLOW_EFFECTS_PATH = ModTemplate.id("glow_effects");
   public static final ResourceLocation ATLAS_ID = ModTemplate.id("textures/atlas/glow_effects.png");
   public static final ResourceLocation GLOW_TEXTURE_ID = ModTemplate.id("glow");
   public static final ResourceLocation GLOW_SHADER_TEXTURE_ID = ModTemplate.id("glow_shaders");
   public static final GlowEffectTexture GLOW_TEXTURE = GlowEffectTexture.of(GLOW_TEXTURE_ID);
   public static final GlowEffectTexture GLOW_SHADER_TEXTURE = GlowEffectTexture.of(GLOW_SHADER_TEXTURE_ID);
   public static TextureAtlas ATLAS_TEXTURE = null;

	public static void onResourceManagerReload(ResourceManager resourceManager, Executor prepareExecutor) {
		if (ATLAS_TEXTURE == null) {
			TextureAtlas atlasTexture = new TextureAtlas(ATLAS_ID);
			Minecraft.getInstance().getTextureManager().register(atlasTexture.location(), atlasTexture);
			ATLAS_TEXTURE = atlasTexture;
		}

		//? if <= 1.21.4 {
	CompletableFuture<SpriteLoader.Preparations> completableFuture = SpriteLoader.create(ATLAS_TEXTURE)
			.loadAndStitch(resourceManager, GLOW_EFFECTS_PATH, 0, prepareExecutor)
			.thenCompose(SpriteLoader.Preparations::waitForUpload);
	//?} else {
		/*CompletableFuture<SpriteLoader.Preparations> completableFuture = SpriteLoader.create(ATLAS_TEXTURE)
				.loadAndStitch(resourceManager, GLOW_EFFECTS_PATH, 0, prepareExecutor, java.util.Set.of())
				.thenCompose(preparations -> preparations.readyForUpload().thenApply(v -> preparations));
		*///?}

		ATLAS_TEXTURE.clearTextureData();
		SpriteLoader.Preparations stitchResult = completableFuture.join();
		ATLAS_TEXTURE.upload(stitchResult);
	}

   public static List<GlowEffectTexture> getTextures() {
      return GlowEffectTexture.getAnimatedTextures(
         GlowEffectTexture.getAtlasTexture(), GlowEffectTexture.class
      );
   }

   public static class GlowEffectTexture extends AnimatedTexture {
      public GlowEffectTexture(ResourceLocation textureId) {
         super(textureId);
      }

      public static GlowEffectTexture of(ResourceLocation textureId) {
         return of(textureId, GlowEffectTexture.class);
      }

      public static GlowEffectTexture of(String path, String namespace) {
         return of(path, namespace, GlowEffectTexture.class);
      }

      public static TextureAtlas getAtlasTexture() {
         return GlowEffectManager.ATLAS_TEXTURE;
      }

      @Override
      public TextureAtlas getSpriteAtlasTexture() {
         return getAtlasTexture();
      }
   }
}
