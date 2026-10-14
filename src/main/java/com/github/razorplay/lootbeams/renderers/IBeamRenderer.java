package com.github.razorplay.lootbeams.renderers;

import com.github.razorplay.lootbeams.config.Configuration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.github.razorplay.lootbeams.render.LootBeamBuffer;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.item.ItemEntity;

@FunctionalInterface
public interface IBeamRenderer {
   void renderBeam(
           LootBeamBuffer var1, PoseStack var2, ItemEntity var3, Configuration var4, TextColor var5, float var6, float var7, float var8, long var9, float var11
   );
}
