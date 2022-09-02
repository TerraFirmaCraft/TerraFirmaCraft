/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class ShortGrassBlock extends PlantBlock
{
    protected static final VoxelShape GRASS_SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    protected static final VoxelShape SHORTER_GRASS_SHAPE = box(2.0, 0.0, 2.0, 14.0, 8.0, 14.0);
    protected static final VoxelShape SHORT_GRASS_SHAPE = box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);
    protected static final VoxelShape SHORTEST_GRASS_SHAPE = box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);

    public static ShortGrassBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new ShortGrassBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected ShortGrassBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(AGE))
            {
                case 0 -> SHORTEST_GRASS_SHAPE;
                case 1 -> SHORTER_GRASS_SHAPE;
                case 2 -> SHORT_GRASS_SHAPE;
                default -> GRASS_SHAPE;
            };
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.GRASS_PLANTABLE_ON);
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.XZ;
    }
}
