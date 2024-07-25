/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import java.util.function.DoubleSupplier;
import java.util.function.IntUnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.util.Helpers;

public interface HorseProperties extends MammalProperties
{
    AttributeModifier OLD_AGE_MODIFIER = new AttributeModifier(Helpers.identifier("old_age"), -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    float TAMED_FAMILIARITY = 0.15f;

    float MIN_MOVEMENT_SPEED = (float) generateSpeed(() -> 0.0);
    float MAX_MOVEMENT_SPEED = (float) generateSpeed(() -> 1.0);
    float MIN_JUMP_STRENGTH = (float) generateJumpStrength(() -> 0.0);
    float MAX_JUMP_STRENGTH = (float) generateJumpStrength(() -> 1.0);
    float MIN_HEALTH = generateMaxHealth(v -> 0);
    float MAX_HEALTH = generateMaxHealth(v -> 1);

    static float generateMaxHealth(IntUnaryOperator supplier)
    {
        return 15.0F + (float) supplier.applyAsInt(8) + (float) supplier.applyAsInt(9);
    }

    static double generateJumpStrength(DoubleSupplier supplier)
    {
        return 0.4 + supplier.getAsDouble() * 0.2 + supplier.getAsDouble() * 0.2 + supplier.getAsDouble() * 0.2;
    }

    static double generateSpeed(DoubleSupplier supplier)
    {
        return (0.45 + supplier.getAsDouble() * 0.3 + supplier.getAsDouble() * 0.3 + supplier.getAsDouble() * 0.3) * 0.25;
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
        tag.putDouble("maxHealth1", male.getAttributeBaseValue(Attributes.MAX_HEALTH));
        tag.putDouble("maxHealth2", female.getAttributeBaseValue(Attributes.MAX_HEALTH));
        tag.putDouble("jumpStrength1", male.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
        tag.putDouble("jumpStrength2", female.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
        tag.putDouble("movementSpeed1", male.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
        tag.putDouble("movementSpeed2", male.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
    }

    @Override
    default void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        MammalProperties.super.applyGenes(tag, babyProperties);
        AbstractHorse baby = (AbstractHorse) babyProperties;
        double maxHealth;
        if (tag.contains("maxHealth1", Tag.TAG_DOUBLE))
        {
            maxHealth = EntityHelpers.createOffspringAttribute(tag.getDouble("maxHealth1"), tag.getDouble("maxHealth2"), MIN_HEALTH, MAX_HEALTH, getEntity().getRandom());
        }
        else
        {
            maxHealth = generateMaxHealth(i -> getEntity().getRandom().nextInt());
        }
        double jumpStrength;
        if (tag.contains("jumpStrength1", Tag.TAG_DOUBLE))
        {
            jumpStrength = EntityHelpers.createOffspringAttribute(tag.getDouble("jumpStrength1"), tag.getDouble("jumpStrength2"), MIN_JUMP_STRENGTH, MAX_JUMP_STRENGTH, getEntity().getRandom());
        }
        else
        {
            jumpStrength = HorseProperties.generateJumpStrength(() -> getEntity().getRandom().nextDouble());
        }
        double speed;
        if (tag.contains("movementSpeed1", Tag.TAG_DOUBLE))
        {
            speed = EntityHelpers.createOffspringAttribute(tag.getDouble("movementSpeed1"), tag.getDouble("movementSpeed2"), MIN_MOVEMENT_SPEED, MAX_MOVEMENT_SPEED, getEntity().getRandom());
        }
        else
        {
            speed = HorseProperties.generateSpeed(() -> getEntity().getRandom().nextDouble());
        }
        EntityHelpers.setNullableAttribute(baby, Attributes.JUMP_STRENGTH, jumpStrength);
        EntityHelpers.setNullableAttribute(baby, Attributes.MOVEMENT_SPEED, speed);
        EntityHelpers.setNullableAttribute(baby, Attributes.MAX_HEALTH, maxHealth);
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
        if (!getEntity().level().isClientSide() && isMale() && isReadyToMate())
        {
            EntityHelpers.findFemaleMate((Animal & TFCAnimalProperties) this);
        }

        if (getAgeType() == Age.OLD)
        {
            final var speed = getEntity().getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null && !speed.hasModifier(OLD_AGE_MODIFIER.id()))
            {
                speed.addTransientModifier(OLD_AGE_MODIFIER);
            }
        }

        for (Entity entity : getEntity().getPassengers())
        {
            if (entity instanceof LivingEntity livingEntity && livingEntity.hasEffect(TFCEffects.OVERBURDENED.holder()))
            {
                rejectPassengers();
                if (livingEntity instanceof Player player)
                {
                    player.displayClientMessage(Component.translatable("tfc.tooltip.animal.horse_angry_overburdened"), true);
                }
                break;
            }
        }
    }

    default void rejectPassengers()
    {
        final AbstractHorse horse = getEntity();
        horse.ejectPassengers();
        horse.makeMad();
        horse.level().broadcastEntityEvent(horse, (byte) 6);
    }
}
