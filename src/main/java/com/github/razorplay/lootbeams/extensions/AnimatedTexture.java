package com.github.razorplay.lootbeams.extensions;

import com.github.razorplay.lootbeams.helpers.StringHelper;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.github.razorplay.lootbeams.ModTemplate.MOD_ID;

public abstract class AnimatedTexture {
   private static final String COLORED_POSTFIX = "_colored";
   private static final Map<Class<?>, Map<String, AnimatedTexture>> TEXTURE_CACHE = new HashMap<>();
   public ResourceLocation id;
   public String path;
   private boolean isSpriteSplitted = false;
   private int frameCount = 0;

   public AnimatedTexture(ResourceLocation id) {
      this.id = id;
      this.path = id.toString();
   }

   public abstract TextureAtlas getSpriteAtlasTexture();

   private boolean isMissing(TextureAtlasSprite sprite) {
      return sprite.contents().name() == MissingTextureAtlasSprite.getLocation();
   }

   public int getFrameCount() {
      return this.frameCount;
   }

   public boolean isColored() {
      return this.path.endsWith(COLORED_POSTFIX);
   }

   private void processSplittedSprite() {
      List<TextureAtlasSprite> spriteList = this.getSpriteAtlasTexture()
         .texturesByName
         .values()
         .stream()
         .filter(sprite -> sprite.contents().name().toString().matches(Pattern.quote(this.id.toString()) + "_\\d+"))
         .toList();
      if (!spriteList.isEmpty()) {
         this.isSpriteSplitted = true;
         this.frameCount = spriteList.size();
      }
   }

   public TextureAtlasSprite getSprite() {
      TextureAtlasSprite sprite = this.getSpriteAtlasTexture().getSprite(this.id);
      return this.isMissing(sprite) ? this.getSprite(0) : sprite;
   }

   public TextureAtlasSprite getSprite(int frameIndex) {
      TextureAtlasSprite sprite = this.getSpriteAtlasTexture().getSprite(ResourceLocation.parse(this.id.toString() + "_" + frameIndex));
      if (!this.isMissing(sprite) && !this.isSplitted()) {
         this.processSplittedSprite();
      }

      return sprite;
   }

   public ResourceLocation getAtlasId() {
      return this.getSpriteAtlasTexture().location();
   }

   public boolean isSplitted() {
      return this.isSpriteSplitted;
   }

   @Override
   public String toString() {
      return this.path;
   }

   public String getDisplayName() {
      String displayName = this.path;
      if (displayName.endsWith(COLORED_POSTFIX)) {
         displayName = displayName.substring(0, displayName.length() - COLORED_POSTFIX.length());
      }

      return StringHelper.capitalize(String.join(" ", displayName.replace(this.id.getNamespace() + ":", "").split("_")));
   }

   public static <T extends AnimatedTexture> Map<String, AnimatedTexture> getTextureCacheMap(Class<T> type) {
      if (!TEXTURE_CACHE.containsKey(type)) {
         TEXTURE_CACHE.put(type, new HashMap<>());
      }

      return TEXTURE_CACHE.get(type);
   }

   public static String toResourcePath(String displayName, String namespace) {
      return namespace + ":" + displayName.trim().toLowerCase().replace(" ", "_");
   }

   public static <T extends AnimatedTexture> T of(ResourceLocation id, Class<T> type) {
      String resourcePath = id.toString();
      return of(resourcePath, "", type);
   }

   public static <T extends AnimatedTexture> T of(String path, String namespace, Class<T> type) {
      String resourcePath = path;
      if (!resourcePath.contains(namespace)) {
         resourcePath = toResourcePath(resourcePath, namespace);
      }

      Map<String, AnimatedTexture> MAP = getTextureCacheMap(type);
      if (MAP.containsKey(resourcePath)) {
         return type.cast(MAP.get(resourcePath));
      }

      try {
         Constructor<T> constructor = type.getConstructor(ResourceLocation.class);
         T instance = constructor.newInstance(ResourceLocation.parse(resourcePath));
         MAP.put(resourcePath, instance);
         return instance;
      } catch (Exception e) {
         throw new RuntimeException("Failed to instantiate texture", e);
      }
   }

   public static <T extends AnimatedTexture> List<T> getAnimatedTextures(TextureAtlas atlasTexture, Class<T> type) {
      List<T> animatedTextures = new ArrayList<>();

      for (TextureAtlasSprite sprite : atlasTexture.texturesByName.values()) {
         if (sprite.contents().name().getNamespace().equals(MOD_ID)) {
            animatedTextures.add(of(sprite.contents().name(), type));
         }
      }

      animatedTextures.sort(Comparator.comparing(AnimatedTexture::getDisplayName));
      return animatedTextures;
   }
}
