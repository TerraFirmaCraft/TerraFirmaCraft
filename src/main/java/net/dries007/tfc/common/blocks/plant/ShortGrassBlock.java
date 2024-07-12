/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.ISpecialPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class ShortGrassBlock extends PlantBlock implements ISpecialPile
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

    public static ShortGrassBlock createBeachGrass(RegistryPlant plant, ExtendedProperties properties)
    {
        return new ShortGrassBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }

            @Override
            protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
            {
                return Helpers.isBlock(state.getBlock(), BlockTags.SAND) || Helpers.isBlock(state.getBlock(), Tags.Blocks.SANDS);
            }
        };

    }

    protected ShortGrassBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        super.randomTick(state, level, pos, random);
        if (PlantRegrowth.canSpread(level, random))
        {
            final BlockPos newPos = PlantRegrowth.spreadSelf(state, level, pos, random, 2, 2, 4);
            if (newPos != null)
            {
                level.setBlockAndUpdate(newPos, updateStateWithCurrentMonth(state.setValue(AGE, 0)));
            }
        }
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
    public BlockState getHiddenState(BlockState internalState, boolean byPlayer)
    {
        return internalState.setValue(AGE, 0);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Helpers.isBlock(state.getBlock(), TFCTags.Blocks.GRASS_PLANTABLE_ON);
    }

}
