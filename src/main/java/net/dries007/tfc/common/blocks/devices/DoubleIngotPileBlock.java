/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class DoubleIngotPileBlock extends IngotPileBlock
{
    public static final IntegerProperty DOUBLE_COUNT = TFCBlockStateProperties.COUNT_1_36;

    private static final VoxelShape[] SHAPES = {
        box(0.25, 0, 0.25, 15.75, 1f / 6 * 16, 15.75),
        box(0.25, 0, 0.25, 15.75, 2f / 6 * 16, 15.75),
        box(0.25, 0, 0.25, 15.75, 3f / 6 * 16, 15.75),
        box(0.25, 0, 0.25, 15.75, 4f / 6 * 16, 15.75),
        box(0.25, 0, 0.25, 15.75, 5f / 6 * 16, 15.75),
        box(0.25, 0, 0.25, 15.75, 16, 15.75),
    };

    public DoubleIngotPileBlock(ExtendedProperties properties)
    {
        super(properties, DOUBLE_COUNT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[(state.getValue(getCountProperty()) - 1) / 6];
    }

    @Override
    public IntegerProperty getCountProperty()
    {
        return DOUBLE_COUNT;
    }
}
