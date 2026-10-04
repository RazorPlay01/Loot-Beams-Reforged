package com.github.razorplay.lootbeams.mixin;

import com.github.razorplay.lootbeams.config.Configuration;
import com.github.razorplay.lootbeams.features.CustomLootBeamsConfig;
import com.github.razorplay.lootbeams.features.CustomRarity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Unique
    private boolean isTooltipModificationActive = false;

    @Inject(method = "getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void modifyTooltip(Item.TooltipContext context, @Nullable Player player, TooltipFlag type, CallbackInfoReturnable<List<Component>> cir) {
        ItemStack itemStack = (ItemStack) (Object) this;
        List<Component> tooltip = cir.getReturnValue();
        if (!this.isTooltipModificationActive) {
            this.isTooltipModificationActive = true;

            try {
                CustomRarity customRarity = CustomRarity.fromItemStack(itemStack);
                Configuration itemConfig = CustomLootBeamsConfig.fromItemStack(itemStack);
                if (itemConfig.renderItemRarity && itemConfig.renderItemRarityInTooltip && customRarity != null) {
                    tooltip.add(CustomRarity.toText(customRarity));
                }
            } finally {
                this.isTooltipModificationActive = false;
            }
        }

        cir.setReturnValue(tooltip);
    }
}
