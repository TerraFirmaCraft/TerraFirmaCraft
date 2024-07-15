/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.pet;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.CatVariantTags;
import net.minecraft.tags.StructureTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class TFCCat extends TamableMammal
{
    public static final EntityDataAccessor<Holder<CatVariant>> DATA_VARIANT = SynchedEntityData.defineId(TFCCat.class, EntityDataSerializers.CAT_VARIANT);

    private static final List<ResourceKey<CatVariant>> LEGACY_CAT_VARIANTS = List.of(CatVariant.TABBY, CatVariant.BLACK, CatVariant.RED, CatVariant.SIAMESE, CatVariant.BRITISH_SHORTHAIR, CatVariant.CALICO, CatVariant.PERSIAN, CatVariant.RAGDOLL, CatVariant.WHITE, CatVariant.JELLIE, CatVariant.ALL_BLACK);

    public TFCCat(EntityType<? extends TamableMammal> type, Level level)
    {
        super(type, level, TFCSounds.CAT, TFCConfig.SERVER.catConfig);
    }

    @Override
    public boolean willListenTo(Command command, boolean isClientSide)
    {
        if (!isClientSide && command == Command.SIT && getRandom().nextFloat() < 0.1f)
        {
            return false;
        }
        return super.willListenTo(command, isClientSide);
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties male)
    {
        super.createGenes(tag, male);
        if (male instanceof TFCCat maleCat)
        {
            tag.putString("variant", random.nextBoolean() ? maleCat.getVariant().toString() : getVariant().toString());
        }
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties baby)
    {
        super.applyGenes(tag, baby);
        if (baby instanceof TFCCat cat)
        {
            BuiltInRegistries.CAT_VARIANT.getHolder(CatVariant.BLACK)
                .ifPresent(this::setVariant);
        }
    }

    @Override
    public void initCommonAnimalData(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason)
    {
        super.initCommonAnimalData(level, difficulty, reason);

        final boolean fullMoon = level.getMoonBrightness() > 0.9F;
        final TagKey<CatVariant> key = fullMoon ? CatVariantTags.FULL_MOON_SPAWNS : CatVariantTags.DEFAULT_SPAWNS;

        BuiltInRegistries.CAT_VARIANT.getOrCreateTag(key)
            .getRandomElement(random)
            .ifPresent(this::setVariant);

        final ServerLevel serverlevel = level.getLevel();
        if (serverlevel.structureManager().getStructureWithPieceAt(this.blockPosition(), StructureTags.CATS_SPAWN_AS_BLACK).isValid())
        {
            this.setVariant(BuiltInRegistries.CAT_VARIANT.getHolderOrThrow(CatVariant.ALL_BLACK));
            this.setPersistenceRequired();
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity)
    {
        return super.canAttack(entity) && Helpers.isEntity(entity, TFCTags.Entities.HUNTED_BY_CATS);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_VARIANT, BuiltInRegistries.CAT_VARIANT.getHolderOrThrow(CatVariant.BLACK));
    }

    public CatVariant getVariant()
    {
        return this.entityData.get(DATA_VARIANT).value();
    }

    public void setVariant(Holder<CatVariant> type)
    {
        this.entityData.set(DATA_VARIANT, type);
    }

    public ResourceLocation getTextureLocation()
    {
        return getVariant().texture();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        ResourceLocation key = BuiltInRegistries.CAT_VARIANT.getKey(this.getVariant());
        if (key != null)
        {
            tag.putString("variant", key.toString());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("variant", Tag.TAG_STRING))
        {
            Optional.ofNullable(ResourceLocation.tryParse(tag.getString("variant")))
                .flatMap(BuiltInRegistries.CAT_VARIANT::getHolder)
                .ifPresent(this::setVariant);
        }
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.CAT_FOOD;
    }

    /* todo 1.21 where did standing eye height go
    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions size)
    {
        return size.height * 0.5F;
    }*/

    @Override
    public void receiveCommand(ServerPlayer player, Command command)
    {
        if (getOwner() != null && getOwner().equals(player))
        {
            playSound(SoundEvents.CAT_PURREOW, getSoundVolume(), getVoicePitch());
        }
        super.receiveCommand(player, command);
    }
}
