/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.TFCLightBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public interface IGlow
{
    default Entity getEntity()
    {
        return (Entity) this;
    }

    void setLightPos(BlockPos pos);

    BlockPos getLightPos();

    default int getLightLevel()
    {
        return 10;
    }

    default int getLightUpdateDistanceSqr()
    {
        return 3 * 3;
    }

    default int getLightUpdateInterval()
    {
        return 20;
    }

    default void saveLight(CompoundTag tag)
    {
        final BlockPos light = getLightPos();
        tag.putInt("lightX", light.getX());
        tag.putInt("lightY", light.getY());
        tag.putInt("lightZ", light.getZ());
    }

    default void readLight(CompoundTag tag)
    {
        setLightPos(new BlockPos(tag.getInt("lightX"), tag.getInt("lightY"), tag.getInt("lightZ")));
    }

    default void tick()
    {
        Entity entity = getEntity();
        if (entity.tickCount % getLightUpdateInterval() == 0)
        {
            final BlockPos oldPos = getLightPos();
            final BlockPos currentPos = entity.blockPosition();
            if (oldPos.distSqr(currentPos) > getLightUpdateDistanceSqr() || oldPos == BlockPos.ZERO)
            {
                // guarding our check for setting the old block to empty fluid
                if (Helpers.isBlock(entity.level.getBlockState(oldPos), TFCBlocks.LIGHT.get()))
                {
                    entity.level.setBlockAndUpdate(oldPos, entity.level.getFluidState(oldPos).createLegacyBlock());
                }
                BlockState currentState = entity.level.getBlockState(currentPos);
                if (entity instanceof AquaticMob)
                {
                    // if we have an empty fluid, we are good to go
                    FluidHelpers.isEmptyFluid(currentState).ifPresent(fluid -> {
                        // since we know what we're dealing with, FluidHelpers#fillWithFluid has more checks than we need.
                        BlockState newState = TFCBlocks.LIGHT.get().defaultBlockState().setValue(TFCLightBlock.LEVEL, getLightLevel()).setValue(TFCLightBlock.FLUID, TFCLightBlock.FLUID.keyFor(fluid.getType()));
                        entity.level.setBlockAndUpdate(currentPos, newState);
                        setLightPos(currentPos);
                    });
                }
                else if (FluidHelpers.isAirOrEmptyFluid(currentState))
                {
                    BlockState newState = TFCBlocks.LIGHT.get().defaultBlockState().setValue(TFCLightBlock.LEVEL, getLightLevel()).setValue(TFCLightBlock.FLUID, TFCLightBlock.FLUID.keyFor(currentState.getFluidState().getType()));
                    entity.level.setBlockAndUpdate(currentPos, newState);
                    setLightPos(currentPos);
                }

            }
        }
    }

    default void tryRemoveLight()
    {
        Entity entity = getEntity();
        final BlockPos light = getLightPos();
        BlockState state = entity.level.getBlockState(light);
        if (Helpers.isBlock(state, TFCBlocks.LIGHT.get()))
        {
            entity.level.setBlockAndUpdate(light, state.getFluidState().createLegacyBlock());
        }
    }
}
