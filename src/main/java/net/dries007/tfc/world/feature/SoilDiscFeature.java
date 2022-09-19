/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class SoilDiscFeature extends Feature<SoilDiscConfig>
{
    public SoilDiscFeature(Codec<SoilDiscConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SoilDiscConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final SoilDiscConfig config = context.config();

        boolean placed = false;
        final int radius = config.getRadius(random);
        final int radiusSquared = radius * radius;
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x)
        {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z)
            {
                final int relX = x - pos.getX();
                final int relZ = z - pos.getZ();
                if (relX * relX + relZ * relZ <= radiusSquared)
                {
                    for (int y = pos.getY() - config.getHeight(); y <= pos.getY() + config.getHeight(); ++y)
                    {
                        mutablePos.set(x, y, z);

                        final BlockState stateAt = world.getBlockState(mutablePos);
                        final BlockState stateReplacement = config.getState(stateAt);
                        if (stateReplacement != null)
                        {
                            world.setBlock(mutablePos, stateReplacement, 2);
                            placed = true;
                        }
                    }
                }
            }
        }
        return placed;
    }
}
