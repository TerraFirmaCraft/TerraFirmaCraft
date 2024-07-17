/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.List;
import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
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
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final RandomSource random = context.random();
        final BoulderConfig config = context.config();

        final ChunkData data = ChunkData.get(context.level(), pos);
        final RockSettings rock = data.getRockData().getRock(pos);
        final List<BlockState> states = config.getStates(rock.raw());
        if (states != null)
        {
            Supplier<BlockState> stateSupplier;
            if (states.size() == 1)
            {
                final BlockState onlyState = states.getFirst();
                stateSupplier = () -> onlyState;
            }
            else
            {
                stateSupplier = () -> states.get(random.nextInt(states.size()));
            }
            place(level, pos, stateSupplier, random);
            return true;
        }
        return false;
    }

    protected void place(WorldGenLevel level, BlockPos pos, Supplier<BlockState> state, RandomSource random)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int size = 6 + random.nextInt(4);
        final Metaballs3D noise = new Metaballs3D(Helpers.fork(random), 6, 8, -0.12f * size, 0.3f * size, 0.3f * size);

        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (noise.inside(x, y, z))
                    {
                        mutablePos.setWithOffset(pos, x, y, z);
                        setBlock(level, mutablePos, state.get());
                    }
                }
            }
        }
    }
}