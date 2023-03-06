/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.VineBlock;

import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VineBlock.class)
public abstract class VineBlockMixin
{
    @Inject(method = "isAcceptableNeighbour", at = @At("HEAD"), cancellable = true)
    private static void inject$isAcceptableNeighbour(BlockGetter level, BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir)
    {
        if (level.getBlockState(pos.relative(dir.getOpposite())) instanceof ILeavesBlock)
        {
            cir.setReturnValue(true);
        }
    }
}
