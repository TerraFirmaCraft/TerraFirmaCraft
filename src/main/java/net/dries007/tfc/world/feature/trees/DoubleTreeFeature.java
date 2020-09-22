/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;

import com.mojang.serialization.Codec;

/**
 * For trees which have a 2x2 base
 * todo: stuff
 */
public abstract class DoubleTreeFeature<C extends IFeatureConfig> extends TreeFeature<C>
{
    protected DoubleTreeFeature(Codec<C> codec)
    {
        super(codec);
    }

    /**
     * Modified to check all four blocks of a 2x2 tree
     */
    @Override
    protected boolean isValidLocation(IWorld worldIn, BlockPos pos)
    {
        return super.isValidLocation(worldIn, pos) && super.isValidLocation(worldIn, pos.offset(1, 0, 0)) && super.isValidLocation(worldIn, pos.offset(0, 0, 1)) && super.isValidLocation(worldIn, pos.offset(1, 0, 1));
    }

    /**
     * Modified to check a radius from a 2x2 trunk
     */
    @Override
    protected boolean isAreaClear(IWorld world, BlockPos pos, int radius, int height)
    {
        for (int y = 0; y < height; y++)
        {
            boolean passed = true;
            for (int x = -radius; x <= radius + 1; x++)
            {
                for (int z = -radius; z <= radius + 1; z++)
                {
                    mutablePos.set(pos);
                    mutablePos.move(x, y, z);
                    BlockState stateAt = world.getBlockState(mutablePos);
                    if (!stateAt.isAir(world, mutablePos))
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