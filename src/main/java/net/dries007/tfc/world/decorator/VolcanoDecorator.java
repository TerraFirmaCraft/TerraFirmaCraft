/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.level.levelgen.placement.DecorationContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.feature.WorldDecoratingHelperAccessor;
import net.dries007.tfc.world.biome.BiomeVariants;
import net.dries007.tfc.world.biome.TFCBiomes;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.Cellular2D;

public class VolcanoDecorator extends SeededDecorator<VolcanoConfig>
{
    private Cellular2D cellNoise;

    public VolcanoDecorator(Codec<VolcanoConfig> codec)
    {
        super(codec);
    }

    @Override
    protected void initSeed(long seed)
    {
        cellNoise = VolcanoNoise.cellNoise(seed);
    }

    @Override
    protected Stream<BlockPos> getSeededPositions(DecorationContext helper, Random rand, VolcanoConfig config, BlockPos pos)
    {
        final WorldGenLevel world = ((WorldDecoratingHelperAccessor) helper).accessor$getLevel();
        final Biome biome = world.getBiome(pos);
        final BiomeVariants variants = TFCBiomes.getExtensionOrThrow(world, biome).getVariants();
        if (variants.isVolcanic())
        {
            // Sample volcano noise
            final float value = cellNoise.noise(pos.getX(), pos.getZ());
            final float distance = cellNoise.f1();
            if (value < variants.getVolcanoChance())
            {
                if (config.useCenter())
                {
                    final BlockPos centerPos = new BlockPos((int) cellNoise.centerX(), pos.getY(), (int) cellNoise.centerZ());
                    if (centerPos.getX() >> 4 == pos.getX() >> 4 && centerPos.getZ() >> 4 == pos.getZ() >> 4)
                    {
                        return Stream.of(centerPos);
                    }
                }
                else
                {
                    final float easing = VolcanoNoise.calculateEasing(distance);
                    if (easing > config.getDistance())
                    {
                        return Stream.of(pos);
                    }
                }
            }
        }
        return Stream.empty();
    }
}
