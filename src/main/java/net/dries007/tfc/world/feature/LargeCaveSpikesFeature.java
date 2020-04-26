package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class LargeCaveSpikesFeature extends CaveSpikesFeature
{
    /**
     * Much larger spikes, calls to the smaller spikes on the outsides
     */
    public void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        float height = 6 + rand.nextInt(11);
        int radius = 1 + rand.nextInt(3);
        for (int y = -4; y <= height; y++)
        {
            float radiusSquared = radius * (1 - 1.3f * Math.abs(y) / height);
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
                    }
                    else if (actualRadius < 0.85 && rand.nextBoolean())
                    {
                        // Only fill in if continuing downwards
                        if (worldIn.getBlockState(mutablePos.add(0, -direction.getYOffset(), 0)) == raw)
                        {
                            replaceBlock(worldIn, mutablePos, raw);
                        }
                    }
                    else if (actualRadius < 1 && rand.nextInt(3) == 0)
                    {
                        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand);
                    }
                    else if (x == 0 && z == 0)
                    {
                        placeSmallSpike(worldIn, mutablePos, spike, raw, direction, rand);
                        return;
                    }
                }
            }
        }
    }
}
