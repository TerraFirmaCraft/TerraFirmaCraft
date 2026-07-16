/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.pet;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.minecraft.world.entity.animal.WolfVariants.*;

public class Dog extends TamableMammal implements VariantHolder<Holder<WolfVariant>>
{
    private static final EntityDataAccessor<Holder<WolfVariant>> DATA_VARIANT_ID = SynchedEntityData.defineId(Dog.class, EntityDataSerializers.WOLF_VARIANT);

    private float interestedAngle;
    private float interestedAngleO;

    public Dog(EntityType<? extends TFCAnimal> animal, Level level)
    {
        super(animal, level, TFCSounds.DOG, TFCConfig.SERVER.dogConfig);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (isAlive())
        {
            this.interestedAngleO = this.interestedAngle;
            interestedAngle += 0.4f * (isInterested() ? (1f - interestedAngle) : (0f - interestedAngle));
        }
    }

    public float getHeadRollAngle(float partialTick)
    {
        return Mth.lerp(partialTick, this.interestedAngleO, this.interestedAngle) * 0.15F * Mth.PI;
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.DOG_FOOD;
    }

    @Override
    public boolean canAttack(LivingEntity entity)
    {
        return super.canAttack(entity) && (Helpers.isEntity(entity, TFCTags.Entities.HUNTED_BY_DOGS) || entity instanceof Monster);
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

    public ResourceLocation getTexture()
    {
        return this.getVariant().value().tameTexture();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
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
    public void createGenes(CompoundTag tag, TFCAnimalProperties male)
    {
        super.createGenes(tag, male);
        if (male instanceof Dog maleDog)
        {
            this.getVariant().unwrapKey().ifPresent(variant -> tag.putString("variant", variant.location().toString()));
        }
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties baby)
    {
        super.applyGenes(tag, baby);
        if (baby instanceof Dog dog)
        {
            Optional.ofNullable(ResourceLocation.tryParse(tag.getString("variant")))
                .map(location -> ResourceKey.create(Registries.WOLF_VARIANT, location))
                .flatMap(key -> this.registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getHolder(key))
                .ifPresent(dog::setVariant);
        }
    }

    @Override
    public void initCommonAnimalData(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason)
    {
        super.initCommonAnimalData(level, difficulty, reason);

        registryAccess().registryOrThrow(Registries.WOLF_VARIANT)
            .getRandom(random)
            .ifPresent(this::setVariant);
    }
}
