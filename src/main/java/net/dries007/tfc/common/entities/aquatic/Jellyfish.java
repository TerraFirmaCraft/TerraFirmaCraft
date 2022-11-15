/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;

public class Jellyfish extends AbstractSchoolingFish implements AquaticMob
{
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Jellyfish.class, EntityDataSerializers.INT);

    private static final ResourceLocation[] LOCATIONS = {
        Helpers.animalTexture("jellyfish_blue"),
        Helpers.animalTexture("jellyfish_red"),
        Helpers.animalTexture("jellyfish_yellow"),
        Helpers.animalTexture("jellyfish_purple"),
        Helpers.animalTexture("jellyfish_orange"),
    };

    public Jellyfish(EntityType<? extends AbstractSchoolingFish> type, Level level)
    {
        super(type, level);
        moveControl = new TFCFishMoveControl(this);
    }

    public int getVariant()
    {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    public void setVariant(int variant)
    {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant);
    }

    @Override
    public void saveToBucketTag(ItemStack stack)
    {
        super.saveToBucketTag(stack);
        CompoundTag compoundtag = stack.getOrCreateTag();
        compoundtag.putInt("BucketVariantTag", this.getVariant());
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.JELLYFISH_BUCKET.get());
    }

    public ResourceLocation getTextureLocation()
    {
        return LOCATIONS[getVariant()];
    }

    @Override
    public void playerTouch(Player player)
    {
        player.hurt(DamageSource.mobAttack(this), 1.0F);
        super.playerTouch(player);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance diff, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        data = super.finalizeSpawn(level, diff, type, data, tag);
        if (tag != null && tag.contains("BucketVariantTag", Tag.TAG_INT))
        {
            setVariant(tag.getInt("BucketVariantTag"));
        }
        else
        {
            final int length = LOCATIONS.length;
            setVariant(random.nextInt(length));
        }
        return data;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        this.setVariant(tag.getInt("Variant"));
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
        return Helpers.isBlock(level.getBlockState(blockPosition()), TFCTags.Blocks.PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }
}
