/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FlowingFluid.class)
public interface FlowingFluidAccessor
{
    @Invoker("canConvertToSource")
    boolean invoke$canConvertToSource();

    @Invoker("canPassThroughWall")
    boolean invoke$canPassThroughWall(Direction face, BlockGetter world, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState);

    @Invoker("isWaterHole")
    boolean invoke$isWaterHole(BlockGetter world, Fluid fluid, BlockPos pos, BlockState state, BlockPos fromPos, BlockState fromState);
}
