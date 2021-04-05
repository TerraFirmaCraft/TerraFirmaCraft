package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockStateFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;

import static net.dries007.tfc.common.blocks.berrybush.AbstractBerryBushBlock.STAGE;

public class BananaFeature extends Feature<BlockStateFeatureConfig>
{
    public BananaFeature(Codec<BlockStateFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config)
    {
        BlockState banana = config.state;

        pos = world.getHeightmapPos(Heightmap.Type.WORLD_SURFACE_WG, pos);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int i = 0; i < 15; i++)
        {
            mutablePos.setWithOffset(pos, rand.nextInt(10) - rand.nextInt(10), -1, rand.nextInt(10) - rand.nextInt(10));
            if (world.getBlockState(mutablePos).is(TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                boolean blocked = false;
                for (int j = 1; j <= 10; j++)
                {
                    mutablePos.move(Direction.UP);
                    if (!world.isEmptyBlock(mutablePos))
                    {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked)
                {
                    mutablePos.move(Direction.DOWN, 10);
                    for (int stage = 0; stage <= 2; stage++)
                    {
                        for (int k = 1; k < rand.nextInt(3) + 1; k++)
                        {
                            mutablePos.move(Direction.UP);
                            world.setBlock(mutablePos, banana.setValue(STAGE, 0), 3);
                            if (stage == 2) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
