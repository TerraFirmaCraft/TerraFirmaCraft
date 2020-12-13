package net.dries007.tfc.world.feature.plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.TFCVineBlock;

public class TFCVinesFeature extends Feature<VineConfig>
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public TFCVinesFeature(Codec<VineConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, VineConfig config)
    {
        BlockPos.Mutable mutablePos = pos.mutable();
        BlockState state = config.getState();
        List<Direction> dirs = new ArrayList<>(4);
        int r = config.getRadius();

        for (int j = 0; j < config.getTries(); j++)
        {
            for (int y = config.getMinHeight(); y < config.getMaxHeight(); ++y)
            {
                mutablePos.set(pos);
                mutablePos.move(rand.nextInt(r) - rand.nextInt(r), 0, rand.nextInt(r) - rand.nextInt(r));
                mutablePos.setY(y);
                if (world.isEmptyBlock(mutablePos))
                {
                    for (Direction direction : DIRECTIONS)
                    {
                        mutablePos.move(direction);
                        BlockState foundState = world.getBlockState(mutablePos);
                        if (direction != Direction.DOWN && (foundState.is(TFCTags.Blocks.CREEPING_PLANTABLE_ON) || foundState.is(BlockTags.LOGS) || foundState.is(BlockTags.LEAVES)))
                        {
                            mutablePos.move(direction.getOpposite());
                            world.setBlock(mutablePos, state.setValue(TFCVineBlock.getPropertyForFace(direction), true), 2);
                            if (direction != Direction.UP)
                                dirs.add(direction);
                            break;
                        }
                        mutablePos.move(direction.getOpposite());
                    }
                    if (!dirs.isEmpty())
                    {
                        for (int k = 0; k < 6 + rand.nextInt(13); k++)
                        {
                            mutablePos.move(Direction.DOWN);
                            if (world.isEmptyBlock(mutablePos))
                            {
                                for (Direction direction : dirs)
                                {
                                    world.setBlock(mutablePos, state.setValue(TFCVineBlock.getPropertyForFace(direction), true), 2);
                                }
                            }
                        }
                        dirs.clear();
                    }
                }
            }
        }


        return true;
    }
}
