/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.placement.ClimatePlacement;

public final class TFCStructureHooks
{
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENTS = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<StructurePlacementType<ClimateStructurePlacement>> CLIMATE = register("climate", ClimateStructurePlacement.PLACEMENT_CODEC);

    private static <T extends StructurePlacement> RegistryObject<StructurePlacementType<T>> register(String name, Codec<T> codec)
    {
        return STRUCTURE_PLACEMENTS.register(name, () -> () -> codec); // supplier supplier >:)
    }

    public static class ClimateStructurePlacement extends RandomSpreadStructurePlacement
    {
        record ClimateSettings(int spacing, int separation, RandomSpreadType randomSpreadType, ClimatePlacement climate) {}

        private static final MapCodec<ClimateSettings> CLIMATE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, 4096).fieldOf("spacing").forGetter(ClimateSettings::spacing),
            Codec.intRange(0, 4096).fieldOf("separation").forGetter(ClimateSettings::separation),
            RandomSpreadType.CODEC.optionalFieldOf("spread_type", RandomSpreadType.LINEAR).forGetter(ClimateSettings::randomSpreadType),
            ClimatePlacement.PLACEMENT_CODEC.fieldOf("climate").forGetter(ClimateSettings::climate)
        ).apply(instance, ClimateSettings::new));

        public static final Codec<ClimateStructurePlacement> PLACEMENT_CODEC = ExtraCodecs.validate(RecordCodecBuilder.mapCodec((instance) ->
            placementCodec(instance)
            .and(CLIMATE_CODEC.forGetter(c -> c.settings))
            .apply(instance, ClimateStructurePlacement::new)
        ), ClimateStructurePlacement::validate).codec();

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
            final ChunkData data = ex.chunkDataProvider().get(chunkPos);

            final WorldgenRandom random = new WorldgenRandom(new LegacyRandomSource(0L));
            random.setLargeFeatureWithSalt(state.getLevelSeed(), x, z, this.salt());

            return settings.climate.isValid(data, pos, random);
        }

        @Override
        public StructurePlacementType<?> type()
        {
            return CLIMATE.get();
        }
    }

}
