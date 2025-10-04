/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Fluid;

import java.util.Arrays;
import java.util.Map;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class Jellyfish extends AbstractSchoolingFish implements AquaticMob
{
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID_TFC = SynchedEntityData.defineId(Jellyfish.class, EntityDataSerializers.INT);

    public Jellyfish(EntityType<? extends AbstractSchoolingFish> type, Level level)
    {
        super(type, level);
        moveControl = new TFCFishMoveControl(this);
    }

    public Type getVariant()
    {
        return Jellyfish.Type.byId(entityData.get(DATA_TYPE_ID_TFC));
    }

    public void setVariant(Type variant)
    {
        this.entityData.set(DATA_TYPE_ID_TFC, variant.getId());
    }

    @Override
    public void saveToBucketTag(ItemStack stack)
    {
        super.saveToBucketTag(stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack, tag -> tag.putInt("BucketVariantTag", getVariant().getId()));
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.JELLYFISH_BUCKET.get());
    }

    @Override
    public void playerTouch(Player player)
    {
        player.hurt(damageSources().mobAttack(this), 1.0F);
        super.playerTouch(player);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        setVariant(Type.byId(level.getRandom().nextInt(Type.SIZE)));
        return spawnData;
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag)
    {
        super.loadFromBucketTag(tag);
        if (tag.contains("BucketVariantTag", 3))
        {
            this.setVariant(Type.byId(tag.getInt("BucketVariantTag")));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID_TFC, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putString("Variant", this.getVariant().getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.setVariant(Jellyfish.Type.byName(tag.getString("Variant")));
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return TFCSounds.JELLYFISH.flop().get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return TFCSounds.JELLYFISH.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return TFCSounds.JELLYFISH.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return TFCSounds.JELLYFISH.death().get();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return EntityHelpers.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(TFCFluids.SALT_WATER.getSource());
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.ANIMAL_IGNORED_PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }

    public static enum Type implements StringRepresentable
    {
        BLUE(0, "blue"),
        RED(1, "red"),
        YELLOW(2, "yellow"),
        PURPLE(3, "purple"),
        ORANGE(4, "orange");

        public static final int SIZE = values().length;

        private static final IntFunction<Jellyfish.Type> BY_ID = ByIdMap.continuous(Jellyfish.Type::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getSerializedName, v -> v));
        private final int id;
        private final String name;

        private Type(int id, String name)
        {
            this.id = id;
            this.name = name;
        }

        @Override
        public String getSerializedName()
        {
            return this.name;
        }

        public int getId()
        {
            return this.id;
        }

        public static Jellyfish.Type byName(String name)
        {
            return BY_NAME.getOrDefault(name, byId(0));
        }

        public static Jellyfish.Type byId(int index) {
            return BY_ID.apply(index);
        }
    }
}
