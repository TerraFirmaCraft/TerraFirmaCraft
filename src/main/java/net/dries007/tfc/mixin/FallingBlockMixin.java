/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.recipes.LandslideRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlock.class)
public abstract class FallingBlockMixin
{
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void preventVanillaFallingBlockBehavior(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci)
    {
        if (LandslideRecipe.getRecipe(state) != null)
        {
            ci.cancel();
        }
    }
}
