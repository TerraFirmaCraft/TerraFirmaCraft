/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.noise.Metaballs3D;
import net.dries007.tfc.world.settings.RockSettings;

public class BouldersFeature extends Feature<BoulderConfig>
{
    public BouldersFeature(Codec<BoulderConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BoulderConfig> context)
    {
        final WorldGenLevel worldIn = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final BoulderConfig config = context.config();

        final ChunkDataProvider provider = ChunkDataProvider.get(context.chunkGenerator());
        final ChunkData data = provider.get(pos);
        final RockSettings rock = data.getRockData().getRock(pos);
        final List<BlockState> states = config.states().get(rock.raw());
        place(worldIn, pos, states, rand);
        return true;
    }

    private void place(WorldGenLevel worldIn, BlockPos pos, List<BlockState> states, Random rand)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int size = 6 + rand.nextInt(4);
        final Metaballs3D noise = new Metaballs3D(rand, 6, 8, -0.12f * size, 0.3f * size, 0.3f * size);

        Supplier<BlockState> state;
        if (states.size() == 1)
        {
            final BlockState onlyState = states.get(0);
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
                    if (noise.inside(x, y, z))
                    {
                        mutablePos.setWithOffset(pos, x, y, z);
                        setBlock(worldIn, mutablePos, state.get());
                    }
                }
            }
        }
    }
}