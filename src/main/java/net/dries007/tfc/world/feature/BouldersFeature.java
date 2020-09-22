/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class BouldersFeature extends Feature<BoulderConfig>
{
    public BouldersFeature(Codec<BoulderConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BoulderConfig config)
    {
        ChunkData data = ChunkDataProvider.get(worldIn).map(provider -> provider.get(pos, ChunkData.Status.ROCKS)).orElseThrow(() -> new IllegalStateException("Missing rock data, cannot generate boulders."));
        Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
        BlockState baseState = rock.getBlock(config.getBaseType()).defaultBlockState();
        BlockState decorationState = rock.getBlock(config.getDecorationType()).defaultBlockState();
        int size = 2 + rand.nextInt(4);
        for (BlockPos posAt : BlockPos.betweenClosed(pos.getX() - size, pos.getY() - size, pos.getZ() - size, pos.getX() + size, pos.getY() + size, pos.getZ() + size))
        {
            if (posAt.distSqr(pos) <= size * size)
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
        return true;
    }
}