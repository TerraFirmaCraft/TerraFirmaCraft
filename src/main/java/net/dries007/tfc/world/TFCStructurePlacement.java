/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.placement.ClimatePlacement;

public record TFCStructurePlacement(StructurePlacement delegate, Optional<ClimatePlacement> climate, PositionalRandomFactory fork, String randomName) implements StructurePlacement
{
    public TFCStructurePlacement(StructurePlacement delegate, Optional<ClimatePlacement> climate, String randomName)
    {
        this(delegate, climate, hash(randomName), randomName);
    }

    public static final Codec<TFCStructurePlacement> CODEC = RecordCodecBuilder.<TFCStructurePlacement>mapCodec(instance ->
        instance.group(
            StructurePlacement.CODEC.fieldOf("placement").forGetter(TFCStructurePlacement::delegate),
            ClimatePlacement.PLACEMENT_CODEC.optionalFieldOf("climate").forGetter(TFCStructurePlacement::climate),
            Codec.STRING.fieldOf("random_name").forGetter(TFCStructurePlacement::randomName)
        ).apply(instance, TFCStructurePlacement::new)).codec();

    private static PositionalRandomFactory hash(String name)
    {
        return new XoroshiroRandomSource(18729341234L, 9182639418231L).forkPositional().fromHashOf(name).forkPositional();
    }

    @Override
    public boolean isFeatureChunk(ChunkGenerator generator, long seed, int x, int z)
    {
        if (!delegate.isFeatureChunk(generator, seed, x, z))
        {
            return false;
        }

        final ChunkDataProvider provider = ChunkDataProvider.get(generator);
        final ChunkPos chunkPos = new ChunkPos(x, z);
        final int blockX = chunkPos.getMinBlockX();
        final int blockZ = chunkPos.getMinBlockZ();
        final BlockPos blockPos = new BlockPos(blockX, 0, blockZ);
        final ChunkData data = provider.get(chunkPos);
        final RandomSource random = getRandom(seed, x, z);
        final WorldgenRandom worldgenRandom = new WorldgenRandom(random);

        return climate.isEmpty() || climate.get().isValid(data, blockPos, worldgenRandom);
    }

    @Override
    public StructurePlacementType<?> type()
    {
        return TFCStructureHooks.TFC_STRUCTURE_PLACEMENT.get();
    }

    private RandomSource getRandom(long levelSeed, int chunkX, int chunkZ)
    {
        return fork.at((int) levelSeed, chunkX, chunkZ);
    }
}
