package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.AbstractTopPlantBlock;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class TFCWeepingVinesFeature extends Feature<DoublePlantConfig>
{
    public TFCWeepingVinesFeature(Codec<DoublePlantConfig> codec)
    {
        super(codec);
    }

    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, DoublePlantConfig config)
    {
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        boolean placedAny = false;
        for (int i = 0; i < config.getTries(); i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(10) - rand.nextInt(10), rand.nextInt(14) - rand.nextInt(6), rand.nextInt(10) - rand.nextInt(10));
            mutablePos.move(Direction.UP);
            BlockState aboveState = world.getBlockState(mutablePos);
            mutablePos.move(Direction.DOWN);
            if ((aboveState.is(BlockTags.LEAVES) || aboveState.is(BlockTags.LOGS) || aboveState.is(BlockTags.BASE_STONE_OVERWORLD)) && world.isEmptyBlock(mutablePos))
            {
                placeColumn(world, rand, mutablePos, rand.nextInt(6) + 14, 17, 25, config.getBodyState(), config.getHeadState());
                placedAny = true;
            }
        }
        return placedAny;
    }

    // This code is copied from WeepingVineFeature
    private static void placeColumn(IWorld world, Random rand, BlockPos.Mutable mutablePos, int height, int minAge, int maxAge, BlockState bodyState, BlockState headState)
    {
        for (int i = 0; i <= height; ++i)//this assumes that we found a valid place to attach
        {
            if (world.isEmptyBlock(mutablePos))//if it's empty, we can grow
            {
                if (i == height || !world.isEmptyBlock(mutablePos.below()))//if we guarantee the next iteration will fail, set the end block
                {
                    world.setBlock(mutablePos, headState.setValue(AbstractTopPlantBlock.AGE, MathHelper.nextInt(rand, minAge, maxAge)), 2);
                    break;
                }
                world.setBlock(mutablePos, bodyState, 2);
            }
            mutablePos.move(Direction.DOWN);
        }
    }
}
