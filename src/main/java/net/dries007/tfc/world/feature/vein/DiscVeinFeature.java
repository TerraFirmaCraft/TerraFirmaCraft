/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
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
        if (Math.abs(y) <= config.height() && vein.metaballs.inside(x, z))
        {
            return config.config().density();
        }
        return 0;
    }

    @Override
    protected Vein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, DiscVeinConfig config)
    {
        return new Vein(defaultPosRespectingHeight(context, chunkX, chunkZ, random, config), Metaballs2D.simple(random, config.size()));
    }

    private BlockPos defaultPosRespectingHeight(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, DiscVeinConfig config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(context, config.size(), random, config), chunkZ + random.nextInt(16));
    }

    @Override
    protected BoundingBox getBoundingBox(DiscVeinConfig config, Vein vein)
    {
        return new BoundingBox(-config.size(), -config.height(), -config.size(), config.size(), config.height(), config.size());
    }

    record Vein(BlockPos pos, Metaballs2D metaballs) implements IVein {}
}
