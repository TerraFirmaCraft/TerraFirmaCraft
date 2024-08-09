/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import java.util.function.Supplier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.GetHookedGoal;
import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.util.Helpers;

public class FreshwaterFish extends Salmon implements AquaticMob
{
    private final TFCSounds.FishId sound;
    private final Supplier<? extends Item> bucket;

    public FreshwaterFish(EntityType<? extends FreshwaterFish> type, Level level, TFCSounds.FishId sound, Supplier<? extends Item> bucket)
    {
        super(type, level);
        this.sound = sound;
        this.bucket = bucket;
        moveControl = new TFCFishMoveControl(this);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(1, new GetHookedGoal(this));
        EntityHelpers.replaceAvoidEntityGoal(this, goalSelector, 2);
    }

    @Override
    protected SoundEvent getFlopSound()
    {
        return sound.flop().get();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return sound.ambient().get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return sound.hurt().get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return sound.death().get();
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(bucket.get());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return EntityHelpers.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(Fluids.WATER);
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.ANIMAL_IGNORED_PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }

}
