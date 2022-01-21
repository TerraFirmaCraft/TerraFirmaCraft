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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;
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
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final BoulderConfig config = context.config();

        final ChunkDataProvider provider = ChunkDataProvider.get(context.chunkGenerator());
        final ChunkData data = provider.get(context.level(), pos);
        final RockSettings rock = data.getRockData().getRock(pos);
        final List<BlockState> states = config.states().get(rock.raw());
        place(level, pos, states, random);
        return true;
    }

    private void place(WorldGenLevel level, BlockPos pos, List<BlockState> states, Random random)
    {
        Supplier<BlockState> state;
        if (states.size() == 1)
        {
            final BlockState onlyState = states.get(0);
            state = () -> onlyState;
        }
        else
        {
            state = () -> states.get(random.nextInt(states.size()));
        }

        final int radius = 3;
        final int diameter = 2 * radius ;
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        Sphere[] spheres = new Sphere[4];
        for (int i = 0; i < 3; i++)
        {
            spheres[i] = new Sphere(pos.getX() + Helpers.triangle(random, diameter),
                pos.getY() + Helpers.triangle(random, diameter),
                pos.getZ() + Helpers.triangle(random, diameter),
                   (int) Math.pow(Mth.nextInt(random, 2, 4), 3));
        }
        spheres[3] = new Sphere(pos.getX(), pos.getY(), pos.getZ(), radius * radius * radius); // prevents extremely blocky edges

        for (int x = -radius; x <= radius; x++)
        {
            for (int y = -radius; y <= radius; y++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    mutablePos.set(pos).move(x, y, z);
                    boolean place = true;
                    for (int i = 0; i < 3; i++)
                    {
                        if (!inside(spheres[i], mutablePos))
                        {
                            place = false;
                            break;
                        }
                    }
                    if (place)
                    {
                        setBlock(level, mutablePos, state.get());
                    }
                }
            }
        }

    }

    private boolean inside(Sphere sphere, BlockPos pos)
    {
        final int x = pos.getX() - sphere.x;
        final int y = pos.getY() - sphere.y;
        final int z = pos.getZ() - sphere.z;
        return x * x + y * y + z * z < sphere.radiusCubed;
    }

    private record Sphere(int x, int y, int z, int radiusCubed) {}

    private void placeO(WorldGenLevel level, BlockPos pos, List<BlockState> states, Random random)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final int size = 6 + random.nextInt(4);
        final Metaballs3D noise = new Metaballs3D(Helpers.fork(random), 6, 8, -0.12f * size, 0.3f * size, 0.3f * size);

        Supplier<BlockState> state;
        if (states.size() == 1)
        {
            final BlockState onlyState = states.get(0);
            state = () -> onlyState;
        }
        else
        {
            state = () -> states.get(random.nextInt(states.size()));
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
                        setBlock(level, mutablePos, state.get());
                    }
                }
            }
        }
    }
}