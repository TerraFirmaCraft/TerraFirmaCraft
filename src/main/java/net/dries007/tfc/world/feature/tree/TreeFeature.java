/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.tree;

import net.minecraft.block.BlockState;
import net.minecraft.block.SaplingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public abstract class TreeFeature<C extends IFeatureConfig> extends Feature<C>
{
    protected TreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    @SuppressWarnings("deprecation")
    protected boolean isValidLocation(IWorld worldIn, BlockPos pos)
    {
        BlockState stateDown = worldIn.getBlockState(pos.below());
        if (!TFCTags.Blocks.TREE_GROWS_ON.contains(stateDown.getBlock()))
        {
            return false;
        }

        BlockState stateAt = worldIn.getBlockState(pos);
        return stateAt.getBlock() instanceof SaplingBlock || stateAt.isAir(worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    protected boolean isAreaClear(IWorld world, BlockPos pos, int radius, int height)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = world.getBlockState(mutablePos);
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