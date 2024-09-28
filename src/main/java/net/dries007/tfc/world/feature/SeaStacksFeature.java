/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.noise.Metaballs2D;

public class SeaStacksFeature extends Feature<NoneFeatureConfiguration>
{
    public SeaStacksFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final var random = context.random();
        final RockData data = ChunkData.get(context.level(), pos).getRockData();

        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos().set(pos);

        final int seaLevel = context.chunkGenerator().getSeaLevel();
        if (level.getFluidState(cursor).isEmpty() || pos.getY() > seaLevel)
        {
            return false; // only place in water
        }
        final BlockState rock = data.getRock(cursor).hardened().defaultBlockState();
        if (!Helpers.isBlock(rock, TFCTags.Blocks.SEA_STACK_ROCKS))
        {
            return false;
        }

        final int upOffset = Mth.nextInt(random, 2, 6);
        cursor.move(0, upOffset, 0);
        final int radius = Mth.nextInt(random, 3, 6);
        placeStack(level, cursor.immutable(), cursor, rock, radius, Mth.nextInt(random, 5, 15), random, false);
        cursor.set(pos).move(0, upOffset - 1, 0);
        placeStack(level, cursor.immutable(), cursor, rock, radius / 2, upOffset, random, true);

        return true;
    }

    private void placeStack(WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos cursor, BlockState state, int radius, int height, RandomSource random, boolean inverted)
    {
        final List<Long> acceptedPositions = new ArrayList<>();
        if (inverted)
        {
            for (int y = -height; y <= 0; y++)
            {
                placeStackLayerAt(level, origin, cursor, state, radius, height, random, y, acceptedPositions);
            }
        }
        else
        {
            for (int y = height - 1; y >= 0; y--)
            {
                placeStackLayerAt(level, origin, cursor, state, radius, height, random, y, acceptedPositions);
            }
        }
    }

    private void placeStackLayerAt(WorldGenLevel level, BlockPos origin, BlockPos.MutableBlockPos cursor, BlockState state, int radius, int height, RandomSource random, int y, List<Long> acceptedPositions)
    {
        final int actualRadius = (int) (radius * Mth.abs(height - y) / (float) height);
        final Metaballs2D noise = Metaballs2D.simple(Helpers.fork(random), actualRadius);
        for (int x = origin.getX() - radius; x <= origin.getX() + radius; ++x)
        {
            for (int z = origin.getZ() - radius; z <= origin.getZ() + radius; ++z)
            {
                final int relX = x - origin.getX();
                final int relZ = z - origin.getZ();
                cursor.set(x, 0, z);
                final long code = cursor.asLong();
                cursor.setY(origin.getY() + y);
                final boolean inList = acceptedPositions.contains(code);

                if ((noise.inside(relX, relZ) || inList) && level.getBlockState(cursor).canBeReplaced())
                {
                    setBlock(level, cursor, state);
                    if (!inList)
                        acceptedPositions.add(code);
                    if (actualRadius == 1 && random.nextFloat() < 0.4f)
                        return;
                }
            }
        }
    }

}
