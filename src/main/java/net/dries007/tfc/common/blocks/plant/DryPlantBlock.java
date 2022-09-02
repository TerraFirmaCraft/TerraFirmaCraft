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
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class DryPlantBlock extends PlantBlock
{
    public static DryPlantBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new DryPlantBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected DryPlantBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockState belowState = level.getBlockState(pos.below());
        return Helpers.isBlock(belowState, BlockTags.SAND) || Helpers.isBlock(belowState, Tags.Blocks.SAND) || Helpers.isBlock(belowState, TFCTags.Blocks.BUSH_PLANTABLE_ON);
    }
}
