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

import net.dries007.tfc.world.noise.Metaballs3D;

public class ClusterVeinFeature extends VeinFeature<ClusterVeinConfig, ClusterVeinFeature.Vein>
{
    public ClusterVeinFeature(Codec<ClusterVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, ClusterVeinConfig config)
    {
        return vein.metaballs.inside(x, y, z) ? config.config().density() : 0;
    }

    @Override
    protected Vein createVein(WorldGenerationContext context, int chunkX, int chunkZ, RandomSource random, ClusterVeinConfig config)
    {
        return new Vein(defaultPos(chunkX, chunkZ, random, config), Metaballs3D.simple(random, config.size()));
    }

    @Override
    protected BoundingBox getBoundingBox(ClusterVeinConfig config, Vein vein)
    {
        return new BoundingBox(-config.size(), -config.size(), -config.size(), config.size(), config.size(), config.size());
    }

    record Vein(BlockPos pos, Metaballs3D metaballs) implements IVein {}
}
