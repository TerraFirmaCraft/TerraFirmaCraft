/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
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
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class BouldersFeature extends Feature<BoulderConfig>
{
    public BouldersFeature(Codec<BoulderConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BoulderConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.ROCKS);
        final Rock rock = data.getRockData().getRock(pos.getX(), pos.getY(), pos.getZ());
        final BlockState baseState = rock.getBlock(config.getBaseType()).getDefaultState();
        final BlockState decorationState = rock.getBlock(config.getDecorationType()).getDefaultState();
        generate(worldIn, baseState, decorationState, pos, rand);
        return true;
    }

    private void generate(ISeedReader worldIn, BlockState baseState, BlockState decorationState, BlockPos pos, Random rand)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final float radius = 1 + rand.nextFloat() * rand.nextFloat() * 3.5f;
        final float radiusSquared = radius * radius;
        final int size = MathHelper.ceil(radius);
        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (x * x + y * y + z * z <= radiusSquared)
                    {
                        mutablePos.setPos(pos).move(x, y, z);
                        if (rand.nextFloat() < 0.4f)
                        {
                            setBlockState(worldIn, mutablePos, decorationState);
                        }
                        else
                        {
                            setBlockState(worldIn, mutablePos, baseState);
                        }
                    }
                }
            }
        }
    }
}