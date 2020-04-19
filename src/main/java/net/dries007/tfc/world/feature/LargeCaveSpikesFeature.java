package net.dries007.tfc.world.feature;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.objects.blocks.rock.RockSpikeBlock;

public class LargeCaveSpikesFeature extends CaveSpikesFeature
{
    public LargeCaveSpikesFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    /**
     * Different placement
     */
    public void place(IWorld worldIn, BlockPos pos, BlockState spike, BlockState raw, Direction direction, Random rand)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable(pos);
        int thickHeight = rand.nextInt(2);

        // Sides
        for (Direction side : Direction.Plane.HORIZONTAL)
        {
            mutablePos.setPos(pos).offset(side);
            int height = thickHeight + rand.nextInt(3);
            for (int y = 0; y < height; y++)
            {
                if (replaceBlock(worldIn, mutablePos, raw))
                {
                    break;
                }
                mutablePos.move(direction);
            }
        }

        // Middle spike
        mutablePos.setPos(pos);
        int height = 2 + thickHeight + rand.nextInt(3);
        for (int y = 0; y <= height; y++)
        {
            if (replaceBlock(worldIn, mutablePos, raw))
            {
                return;
            }
            mutablePos.move(direction);
        }

        // Thinner peaks
        for (RockSpikeBlock.Part part : RockSpikeBlock.Part.values())
        {
            if (replaceBlock(worldIn, mutablePos, spike.with(RockSpikeBlock.PART, part)))
            {
                return;
            }
            mutablePos.move(direction);
            if (replaceBlock(worldIn, mutablePos, spike.with(RockSpikeBlock.PART, part)))
            {
                return;
            }
            mutablePos.move(direction);
        }
    }
}
