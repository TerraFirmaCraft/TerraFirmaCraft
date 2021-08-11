/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

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
    public boolean place(WorldGenLevel worldIn, ChunkGenerator generator, Random rand, BlockPos pos, BoulderConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.get(generator);
        final ChunkData data = provider.get(pos);
        final Rock rock = data.getRockDataOrThrow().getRock(pos.getX(), pos.getY(), pos.getZ());
        final List<BlockState> states = config.getStates(rock);
        place(worldIn, pos, states, rand);
        return true;
    }

    private void place(WorldGenLevel worldIn, BlockPos pos, List<BlockState> states, Random rand)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final float radius = 1 + rand.nextFloat() * rand.nextFloat() * 3.5f;
        final float radiusSquared = radius * radius;
        final int size = Mth.ceil(radius);

        Supplier<BlockState> state;
        if (states.size() == 1)
        {
            BlockState onlyState = states.get(0);
            state = () -> onlyState;
        }
        else
        {
            state = () -> states.get(rand.nextInt(states.size()));
        }

        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (x * x + y * y + z * z <= radiusSquared)
                    {
                        mutablePos.set(pos).move(x, y, z);
                        if (rand.nextFloat() < 0.4f)
                        {
                            setBlock(worldIn, mutablePos, state.get());
                        }
                        else
                        {
                            setBlock(worldIn, mutablePos, state.get());
                        }
                    }
                }
            }
        }
    }
}