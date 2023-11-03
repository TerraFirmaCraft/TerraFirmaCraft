/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import net.dries007.tfc.world.noise.Metaballs2D;

public class DiscVeinFeature extends VeinFeature<DiscVeinConfig, DiscVeinFeature.Vein>
{
    public DiscVeinFeature(Codec<DiscVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, DiscVeinConfig config)
    {
        final float sample = (float) vein.metaballs.sample(x, z);
        if (Math.abs(y) <= config.height() && sample > 1f)
        {
            return config.config().density()
                * Mth.clampedMap((float) Math.abs(y - config.height()), 0.7f * config.height(), config.height(), 1.0f, 0.4f)
                * Mth.clampedMap(sample, 2f, 1f, 1f, 0.6f);
        }
        return 0;
    }

    @Override
    protected Vein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, DiscVeinConfig config)
    {
        return new Vein(defaultPosRespectingHeight(chunkX, chunkZ, random, config), Metaballs2D.simple(random, config.size()));
    }

    private BlockPos defaultPosRespectingHeight(int chunkX, int chunkZ, RandomSource random, DiscVeinConfig config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(config.size(), random, config), chunkZ + random.nextInt(16));
    }

    @Override
    protected BoundingBox getBoundingBox(DiscVeinConfig config, Vein vein)
    {
        return new BoundingBox(-config.size(), -config.height(), -config.size(), config.size(), config.height(), config.size());
    }

    protected record Vein(BlockPos pos, Metaballs2D metaballs) implements IVein {}
}
