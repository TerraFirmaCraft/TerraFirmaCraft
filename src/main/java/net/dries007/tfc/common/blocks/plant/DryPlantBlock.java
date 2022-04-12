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
import net.dries007.tfc.util.Helpers;

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
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState belowState = level.getBlockState(pos.below());
        return Helpers.isBlock(belowState, BlockTags.SAND) || Helpers.isBlock(belowState, Tags.Blocks.SAND) || Helpers.isBlock(belowState, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }
}
