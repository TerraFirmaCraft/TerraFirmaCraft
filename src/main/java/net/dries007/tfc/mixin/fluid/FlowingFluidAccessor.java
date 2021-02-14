/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.fluid;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowingFluidAccessor
{
    /**
     * This method checks if fluid can flow between two positions, based on the shapes of potential waterlogged block states in the way
     * We do not override it as it queries a thread local static cache in {  FlowingFluid}
     */
    @Invoker("canPassThroughWall")
    boolean invoke$canPassThroughWall(Direction direction, IBlockReader world, BlockPos pos, BlockState state, BlockPos adjacentPos, BlockState adjacentState);
}
