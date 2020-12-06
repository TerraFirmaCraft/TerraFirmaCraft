package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

public class TFCTwistingVinesFeature extends Feature<DoublePlantConfig>
{
    public TFCTwistingVinesFeature(Codec<DoublePlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, DoublePlantConfig config)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(10) - rand.nextInt(10), rand.nextInt(2) - rand.nextInt(2), rand.nextInt(10) - rand.nextInt(10));
            mutablePos.move(Direction.DOWN);
            if (!world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
                return false;
            mutablePos.move(Direction.UP);
            if (world.isEmptyBlock(mutablePos))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(7) + 1, 17, 25, config.getBodyState(), config.getHeadState());
                placedAny = true;
            }
        }
        return placedAny;
    }

    public static void placeColumn(IWorld world, Random rand, BlockPos.Mutable mutablePos, int height, int minAge, int maxAge, BlockState body, BlockState head)
    {
        for (int i = 1; i <= height; ++i)
        {
            if (world.isEmptyBlock(mutablePos))
            {
                if (i == height || !world.isEmptyBlock(mutablePos.above()))
                {
                    world.setBlock(mutablePos, head.setValue(AbstractTopPlantBlock.AGE, MathHelper.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlock(mutablePos, body, 2);
            }
            mutablePos.move(Direction.UP);
        }
    }
}
