/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.noise.Metaballs3D;

public class PowderSnowFeature extends Feature<BlockStateConfiguration>
{
    public PowderSnowFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final BlockState state = context.config().state;
        final WorldGenLevel level = context.level();
        final int size = 6 + random.nextInt(4);
        final Metaballs3D noise = new Metaballs3D(Helpers.fork(random), 6, 8, -0.15f * size, 0.4f * size, 0.3f * size);

        for (int x = -size; x <= size; x++)
        {
            for (int y = -size; y <= size; y++)
            {
                for (int z = -size; z <= size; z++)
                {
                    if (noise.inside(x, y, z))
                    {
                        mutablePos.setWithOffset(pos, x, y, z);
                        final BlockState foundState = level.getBlockState(mutablePos);
                        if (Helpers.isBlock(foundState, TFCTags.Blocks.POWDER_SNOW_REPLACEABLE))
                        {
                            setBlock(level, mutablePos, state);
                        }
                    }
                }
            }
        }

        return true;
    }
}
