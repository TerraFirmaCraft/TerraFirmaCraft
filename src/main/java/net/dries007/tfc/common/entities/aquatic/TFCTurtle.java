/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class TFCTurtle extends AmphibiousAnimal
{
    public TFCTurtle(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level, TFCSounds.TURTLE);
    }

    @Override
    public int getAmbientSoundInterval()
    {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound()
    {
        return !this.isInWater() && this.onGround() && !this.isBaby() ? SoundEvents.TURTLE_AMBIENT_LAND : super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float volume)
    {
        super.playSwimSound(volume * 1.5F);
    }

    @Override
    protected SoundEvent getSwimSound()
    {
        return SoundEvents.TURTLE_SWIM;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source)
    {
        return this.isBaby() ? SoundEvents.TURTLE_HURT_BABY : SoundEvents.TURTLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound()
    {
        return this.isBaby() ? SoundEvents.TURTLE_DEATH_BABY : SoundEvents.TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block)
    {
        SoundEvent soundevent = this.isBaby() ? SoundEvents.TURTLE_SHAMBLE_BABY : SoundEvents.TURTLE_SHAMBLE;
        this.playSound(soundevent, 0.15F, 1.0F);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.TURTLE_FOOD);
    }

}
