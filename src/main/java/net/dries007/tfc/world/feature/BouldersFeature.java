/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.IChunkDataProvider;

public class BouldersFeature extends Feature<BoulderConfig>
{
    public BouldersFeature(Codec<BoulderConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BoulderConfig config)
    {
        final IChunkDataProvider provider = IChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
        final BlockState baseState = rock.getBlock(config.getBaseType()).defaultBlockState();
        final BlockState decorationState = rock.getBlock(config.getDecorationType()).defaultBlockState();
        place(worldIn, baseState, decorationState, pos, rand);
        return true;
    }

    private void place(ISeedReader worldIn, BlockState baseState, BlockState decorationState, BlockPos pos, Random rand)
    {
        float radius = 1 + rand.nextFloat() * 5;
        int size = MathHelper.ceil(radius);
        for (BlockPos posAt : BlockPos.betweenClosed(pos.getX() - size, pos.getY() - size, pos.getZ() - size, pos.getX() + size, pos.getY() + size, pos.getZ() + size))
        {
            if (posAt.distSqr(pos) <= radius * radius)
            {
                if (rand.nextFloat() < 0.4f)
                {
                    setBlock(worldIn, posAt, decorationState);
                }
                else
                {
                    setBlock(worldIn, posAt, baseState);
                }
            }
        }
    }
}