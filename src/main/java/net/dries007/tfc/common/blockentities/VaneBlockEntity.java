/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VaneBlockEntity extends TickableBlockEntity
{

    protected VaneBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public VaneBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.VANE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AnemometerBlockEntity anemometer)
    {
        clientTick(level, pos, state, anemometer);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, AnemometerBlockEntity anemometer)
    {

    }
}
