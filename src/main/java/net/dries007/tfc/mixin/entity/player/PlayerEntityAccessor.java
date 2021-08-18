/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
