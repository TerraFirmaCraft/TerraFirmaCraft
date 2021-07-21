package net.dries007.tfc.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEntity.class)
public interface PlayerEntityAccessor
{
    /**
     * In order to overwrite the player's food data, as we replace it with a TFC handler that delegates to the original one
     */
    @Accessor(value = "foodData")
    void accessor$setFoodData(FoodStats foodData);
}
