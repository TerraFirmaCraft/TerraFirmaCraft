/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class IceAndSnowFeature extends Feature<NoneFeatureConfiguration>
{
    public IceAndSnowFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final ChunkPos chunkPos = new ChunkPos(pos);
        final ChunkGeneratorExtension extension = (ChunkGeneratorExtension) context.chunkGenerator();
        final ChunkData chunkData = extension.chunkDataProvider().get(level, chunkPos);
        Climate.onChunkLoad(level, level.getChunk(pos), chunkData);
        return true;
    }
}
