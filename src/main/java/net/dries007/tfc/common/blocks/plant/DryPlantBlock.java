/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.Tags;

import net.dries007.tfc.common.TFCTags;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class DryPlantBlock extends PlantBlock
{
    public static DryPlantBlock create(IPlant plant, Properties properties)
    {
        return new DryPlantBlock(properties)
        {

            @Override
            public IPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected DryPlantBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(getStateDefinition().any().setValue(getPlant().getStageProperty(), 0).setValue(AGE, 0));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockState belowState = worldIn.getBlockState(pos.below());
        return belowState.is(BlockTags.SAND) || belowState.is(Tags.Blocks.SAND) || belowState.is(TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }
}
