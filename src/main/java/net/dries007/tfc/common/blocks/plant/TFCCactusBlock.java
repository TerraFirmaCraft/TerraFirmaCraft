/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class TFCCactusBlock extends TFCTallGrassBlock
{
    public static TFCCactusBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new TFCCactusBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected static final VoxelShape COLLISION_SHAPE = box(1, 0, 1, 15, 15, 15);
    protected static final VoxelShape OUTLINE_SHAPE = box(1, 0, 1, 15, 16, 15);

    protected TFCCactusBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            mutable.setWithOffset(pos, direction);
            BlockState stateAt = level.getBlockState(mutable);
            if (stateAt.isSolid() || Helpers.isFluid(level.getFluidState(mutable), TFCTags.Fluids.LAVA_LIKE))
            {
                return false;
            }
        }

        mutable.setWithOffset(pos, 0, -1, 0);
        BlockState belowState = level.getBlockState(mutable);
        if (state.getValue(PART) == Part.LOWER)
        {
            return Helpers.isBlock(belowState, BlockTags.SAND);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return Helpers.isBlock(belowState, BlockTags.SAND);
            }
            return belowState.getBlock() == this && belowState.getValue(PART) == Part.LOWER;
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return OUTLINE_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return COLLISION_SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        entity.hurt(entity.damageSources().cactus(), 1f);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }
}
