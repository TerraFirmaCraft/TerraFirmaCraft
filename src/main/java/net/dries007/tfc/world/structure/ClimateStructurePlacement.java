/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.structure;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.placement.ClimatePlacement;

public class ClimateStructurePlacement extends RandomSpreadStructurePlacement
{
    private static final MapCodec<ClimateSettings> CLIMATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ClimateSettings::spacing),
        Codec.intRange(0, 4096).fieldOf("separation").forGetter(ClimateSettings::separation),
        RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ClimateSettings::randomSpreadType),
        ClimatePlacement.CODEC.fieldOf("climate").forGetter(ClimateSettings::climate)
    ).apply(instance, ClimateSettings::new));

    public static final MapCodec<ClimateStructurePlacement> PLACEMENT_CODEC = RecordCodecBuilder.<ClimateStructurePlacement>mapCodec(instance ->
        placementCodec(instance)
            .and(CLIMATE_CODEC.forGetter(c -> c.settings))
            .apply(instance, ClimateStructurePlacement::new)
    ).validate(ClimateStructurePlacement::validate);

    private static DataResult<ClimateStructurePlacement> validate(ClimateStructurePlacement placement)
    {
        return placement.spacing() <= placement.separation() ? DataResult.error(() -> "Spacing has to be larger than separation") : DataResult.success(placement);
    }

    private final ClimateSettings settings;

    @SuppressWarnings("deprecation")
    public ClimateStructurePlacement(Vec3i locateOffset, FrequencyReductionMethod freqReduction, float frequency, int salt, Optional<ExclusionZone> exclusionZone, ClimateSettings settings)
    {
        super(locateOffset, freqReduction, frequency, salt, exclusionZone, settings.spacing, settings.separation, settings.randomSpreadType);
        this.settings = settings;
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z)
    {
        if (!super.isPlacementChunk(state, x, z))
        {
            return false;
        }

        final ChunkGeneratorExtension ex = ChunkGeneratorExtension.getFromStructureState(state);
        if (ex == null)
        {
            return false;
        }

        final ChunkPos chunkPos = new ChunkPos(x, z);
        final int blockX = chunkPos.getMinBlockX();
        final int blockZ = chunkPos.getMinBlockZ();
        final BlockPos pos = new BlockPos(blockX, 0, blockZ);
        final ChunkData data = ex.chunkDataGenerator().createAndGenerate(chunkPos);
        final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));

        random.setLargeFeatureWithSalt(state.getLevelSeed(), x, z, this.salt());

        return settings.climate.isValid(data, pos, random);
    }

    @Override
    public StructurePlacementType<?> type()
    {
        return TFCStructureHooks.CLIMATE.get();
    }

    public record ClimateSettings(int spacing, int separation, RandomSpreadType randomSpreadType, ClimatePlacement climate) {}
}
