/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import org.jetbrains.annotations.NotNull;

public interface HorseProperties extends MammalProperties
{
    float TAMED_FAMILIARITY = 0.15f;

    @Override
    default InteractionResult eatFood(@NotNull ItemStack stack, InteractionHand hand, Player player)
    {
        var res = MammalProperties.super.eatFood(stack, hand, player);
        if (getEntity().isTamed())
        {
            getEntity().tameWithName(player);
        }
        return res;
    }

    @Override
    default AbstractHorse getEntity()
    {
        return (AbstractHorse) MammalProperties.super.getEntity();
    }

    @Override
    default void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        MammalProperties.super.createGenes(tag, maleProperties);
        AbstractHorse female = getEntity();
        AbstractHorse male = (AbstractHorse) maleProperties;
        tag.putDouble("maxHealth", male.getAttributeBaseValue(Attributes.MAX_HEALTH) + female.getAttributeBaseValue(Attributes.MAX_HEALTH));
    }

    @Override
    default void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        MammalProperties.super.applyGenes(tag, babyProperties);
        AbstractHorse baby = (AbstractHorse) babyProperties;
        EntityHelpers.setNullableAttribute(baby, Attributes.MAX_HEALTH, (tag.getDouble("maxHealth") + generateRandomMaxHealth()) / 3);
    }

    @Override
    default SoundEvent eatingSound(ItemStack stack)
    {
        return getEntity().getEatingSound(stack);
    }

    @Override
    default void tickAnimalData()
    {
        MammalProperties.super.tickAnimalData();
        // legacy breeding behavior
        if (!getEntity().getLevel().isClientSide() && getGender() == Gender.MALE && isReadyToMate())
        {
            EntityHelpers.findFemaleMate((Animal & TFCAnimalProperties) this);
        }
    }

    private float generateRandomMaxHealth()
    {
        final Random random = getEntity().getRandom();
        return 15.0F + random.nextInt(8) + random.nextInt(9);
    }
}
