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
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.predator.PackPredator;
import net.dries007.tfc.world.chunkdata.ChunkData;

import static net.minecraft.world.entity.animal.WolfVariants.*;

public class TFCWolf extends PackPredator implements VariantHolder<Holder<WolfVariant>>
{
    private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(TFCWolf.class, EntityDataSerializers.WOLF_VARIANT);

    public TFCWolf(EntityType<? extends PackPredator> type, Level level)
    {
        super(type, level, false, TFCSounds.TFC_WOLF, true);
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
        // TODO: setup more variants
        Registry<WolfVariant> registry = registryAccess().registryOrThrow(Registries.WOLF_VARIANT);
        final BlockPos pos = blockPosition();
        final ChunkData data = ChunkData.get(level, pos);
        final float temp = data.getAverageSeaLevelTemp(pos);

        this.setVariant(registry.getHolderOrThrow(temp < 0 ? SNOWY : temp > 18 ? SPOTTED : PALE));
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
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
