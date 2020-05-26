/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.datafixers.Dynamic;

public class LargeCaveSpikesFeature extends CaveSpikesFeature
{
    @SuppressWarnings("unused")
    public LargeCaveSpikesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    public LargeCaveSpikesFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    /**
     * Much larger spikes, calls to the smaller spikes on the outsides
     */
    public void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        int height = 6 + rand.nextInt(11);
        int radius = 2 + rand.nextInt(1);
        int maxHeightReached = 0;
        for (int y = -3; y <= height; y++)
        {
            float radiusSquared = radius * (1 - 1.5f * Math.abs(y) / height);
            if (radiusSquared < 0)
            {
                continue;
            }
            radiusSquared *= radiusSquared;
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.setPos(pos).move(x, y * direction.getYOffset(), z);
                    float actualRadius = ((x * x) + (z * z)) / radiusSquared;
                    if (actualRadius < 0.7)
                    {
                        // Fill in actual blocks
                        replaceBlock(worldIn, mutablePos, raw);
                        if (x == 0 && z == 0)
                        {
                            maxHeightReached = y;
                        }
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (worldIn.getBlockState(mutablePos.add(0, -direction.getYOffset(), 0)) == raw)
                        {
                            replaceBlock(worldIn, mutablePos, raw);
                        }
                    }
                    else if (actualRadius < 1 && rand.nextInt(3) == 0 && y > 0)
                    {
                        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand);
                    }
                }
            }
        }
        mutablePos.setPos(pos).move(direction, maxHeightReached - 1);
        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand, 1.0f);
    }
}
