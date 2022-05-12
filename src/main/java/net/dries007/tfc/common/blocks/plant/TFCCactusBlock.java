/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public abstract class TFCCactusBlock extends TFCTallGrassBlock
{
    public static TFCCactusBlock create(IPlant plant, ExtendedProperties properties)
    {
        return new TFCCactusBlock(properties)
        {
            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCCactusBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState blockstate = level.getBlockState(pos.below());
        if (state.getValue(PART) == Part.LOWER)
        {
            return Helpers.isBlock(blockstate, BlockTags.SAND);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return Helpers.isBlock(blockstate, BlockTags.SAND); //calling super here is stupid it does nothing lets just check tags
            }
            return blockstate.getBlock() == this && blockstate.getValue(PART) == Part.LOWER;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.block();
    }

    @Override
    public OffsetType getOffsetType()
    {
        return OffsetType.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        entity.hurt(DamageSource.CACTUS, 1.0F);
    }
}
