/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.predator;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

import static net.minecraft.world.entity.animal.WolfVariants.*;

public class TFCWolf extends PackPredator implements VariantHolder<Holder<WolfVariant>>
{
    private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(TFCWolf.class, EntityDataSerializers.WOLF_VARIANT);

    public TFCWolf(EntityType<? extends PackPredator> type, Level level)
    {
        super(type, level, false, TFCSounds.TFC_WOLF, true, true);
    }

    @Override
    public void setVariant(Holder<WolfVariant> variant)
    {
        this.entityData.set(DATA_VARIANT_ID, variant);
    }

    @Override
    public Holder<WolfVariant> getVariant()
    {
        return this.entityData.get(DATA_VARIANT_ID);
    }

    public ResourceLocation getTexture() {
        return this.getVariant().value().wildTexture();
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        Registry<WolfVariant> registry = this.registryAccess().registryOrThrow(Registries.WOLF_VARIANT);
        builder.define(DATA_VARIANT_ID, registry.getHolder(DEFAULT).or(registry::getAny).orElseThrow());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        this.getVariant().unwrapKey().ifPresent(variant -> compound.putString("variant", variant.location().toString()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        Optional.ofNullable(ResourceLocation.tryParse(compound.getString("variant")))
            .map(location -> ResourceKey.create(Registries.WOLF_VARIANT, location))
            .flatMap(key -> this.registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getHolder(key))
            .ifPresent(this::setVariant);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        BlockPos pos = blockPosition();
        setVariant(getWolfVariantFromClimate(level, pos));
        return spawnData;
    }

    private Holder<WolfVariant> getWolfVariantFromClimate(ServerLevelAccessor level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);

        final ForestType forestType = data.getForestType();
        final float temp = data.getAverageSeaLevelTemp(pos);
        final float rainfall = data.getAverageRainfall(pos);
        final float rainVariance = data.getRainVariance(pos);
        final float hemisphereScale = Climate.get(level()).hemisphereScale();

        final boolean isForested = forestType.getDensity() > 1;

        final KoppenClimateClassification climate = KoppenClimateClassification.classify(temp, rainfall, rainVariance, SolarCalculator.getInNorthernHemisphere(pos.getZ(), hemisphereScale));

        ResourceKey<WolfVariant> variantKey = switch (climate)
        {
            // Missing STRIPED variant
            case CFA, CFB, CFC, CSA, CSB, CSC -> isForested ? WOODS : PALE; // Subtropical/Oceanic Climates + Mediterranean Climates
            case CWA, CWB, CWC -> isForested ? RUSTY : SPOTTED; // Monsoon-influenced Subtropical Climates
            case DFA, DWA, DSA, DFB, DWB, DSB -> isForested ? BLACK : CHESTNUT; // Humid Continental Climates
            case DFC, DWC, DSC, DFD, DWD, DSD -> isForested ? ASHEN : SNOWY; // Subarctic Continental Climates

            /* Groups A, B, and E are generally outside the climate range in the fauna definition */
            case AF, AM -> RUSTY; // Tropical Rainforest/Monsoon Climates
            case AW, AS -> SPOTTED; // Tropical Savanna Climates
            case BSH, BSK, BWH, BWK -> STRIPED; // Arid/Semi-arid Climates
            case ET, EF -> SNOWY; // Polar Climates
        };

        return registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getHolderOrThrow(variantKey);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent)
    {
        TFCWolf wolf = TFCEntities.WOLF.get().create(level);

        if (wolf != null && otherParent instanceof TFCWolf otherWolf)
        {
            if (this.random.nextBoolean())
            {
                wolf.setVariant(this.getVariant());
            }
            else
            {
                wolf.setVariant(otherWolf.getVariant());
            }
        }
        return wolf;
    }
}
