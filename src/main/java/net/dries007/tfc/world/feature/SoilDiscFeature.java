/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.noise.Metaballs2D;

public class SoilDiscFeature extends Feature<SoilDiscConfig>
{
    public SoilDiscFeature(Codec<SoilDiscConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SoilDiscConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final var random = context.random();
        final SoilDiscConfig config = context.config();

        boolean placed = false;
        final int radius = config.getRadius(random);
        final Metaballs2D noise = Metaballs2D.simple(Helpers.fork(random), radius * 2);

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x)
        {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z)
            {
                final int relX = x - pos.getX();
                final int relZ = z - pos.getZ();
                if (noise.inside(relX, relZ))
                {
                    for (int y = pos.getY() - config.getHeight(); y <= pos.getY() + config.getHeight(); ++y)
                    {
                        if (random.nextFloat() < config.integrity())
                        {
                            mutablePos.set(x, y, z);

                            final BlockState stateAt = level.getBlockState(mutablePos);
                            final BlockState stateReplacement = config.getState(stateAt);
                            if (stateReplacement != null)
                            {
                                level.setBlock(mutablePos, stateReplacement, 2);
                                placed = true;
                            }
                        }
                    }
                }
            }
        }
        return placed;
    }
}
