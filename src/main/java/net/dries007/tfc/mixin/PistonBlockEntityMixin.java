/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.dries007.tfc.common.fluids.FluidHelpers;

/**
 * Fixes <a href="https://bugs.mojang.com/browse/MC-130183">MC-130183</a> (1-tick piston pulses push waterlogged blocks) as it is a considerable exploit in TFC where moving water sources is restricted.
 * <p>
 * This particular fix was borrowed from carpet-fixes, thanks to <a href="">fxmorin</a>, and is used here under the MIT license.
 */
@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonBlockEntityMixin
{
    /**
     * Fixes MC-130183, which exists for 1-ticked pistons and is reproducible in vanilla.
     */
    @Redirect(method = "finalTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateFromNeighbourShapes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), require = 0)
    private BlockState removeFluidInFinalTick(BlockState state, LevelAccessor level, BlockPos pos)
    {
        return FluidHelpers.emptyFluidFrom(Block.updateFromNeighbourShapes(state, level, pos));
    }

    /**
     * This extends the vanilla remove waterlogged, which exists for pistons ticked for >1 tick, to all TFC fluid blocks.
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;updateFromNeighbourShapes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"), require = 0)
    private static BlockState removeFluidInTick(BlockState state, LevelAccessor level, BlockPos pos)
    {
        return FluidHelpers.emptyFluidFrom(Block.updateFromNeighbourShapes(state, level, pos));
    }
}
