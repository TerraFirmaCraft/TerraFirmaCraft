/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.block;

import net.minecraft.block.Block;
import net.minecraft.block.CakeBlock;
import net.minecraft.util.FoodStats;

import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.food.TFCFoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block
{
    private CakeBlockMixin(Properties properties)
    {
        super(properties);
    }

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/FoodStats;eat(IF)V"))
    private void redirect$eat$eat(FoodStats foodStats, int foodLevelIn, float foodSaturationModifier)
    {
        if (foodStats instanceof TFCFoodStats)
        {
            ((TFCFoodStats) foodStats).eat(new FoodData(1, 0, 0, 0, 0, 0, 0, 0, 1));
        }
    }
}
