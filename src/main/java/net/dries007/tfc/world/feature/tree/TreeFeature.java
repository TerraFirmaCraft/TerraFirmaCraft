/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.tree;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public abstract class TreeFeature<C extends FeatureConfiguration> extends Feature<C>
{
    protected TreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    protected boolean isValidLocation(LevelAccessor level, BlockPos pos)
    {
        BlockState stateDown = level.getBlockState(pos.below());
        if (!Helpers.isBlock(stateDown, TFCTags.Blocks.TREE_GROWS_ON))
        {
            return false;
        }

        BlockState stateAt = level.getBlockState(pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir();
    }

    protected boolean isAreaClear(LevelAccessor level, BlockPos pos, int radius, int height)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = level.getBlockState(mutablePos);
                    if (!stateAt.isAir())
                    {
                        passed = false;
                        break;
                    }
                }
                if (!passed)
                {
                    break;
                }
            }
            if (passed)
            {
                return true;
            }
        }
        return false;
    }
}