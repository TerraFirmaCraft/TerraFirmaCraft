/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;

public interface HorseProperties extends MammalProperties
{
    @Override
    default AbstractHorse getEntity()
    {
        return (AbstractHorse) MammalProperties.super.getEntity();
    }

    ItemStack getChestItem();

    void setChestItem(ItemStack stack);

    @Override
    default void saveCommonAnimalData(CompoundTag nbt)
    {
        MammalProperties.super.saveCommonAnimalData(nbt);
        nbt.put("chestItem", getChestItem().save(new CompoundTag()));
    }

    @Override
    default void readCommonAnimalData(CompoundTag nbt)
    {
        MammalProperties.super.readCommonAnimalData(nbt);
        setChestItem(ItemStack.of(nbt.getCompound("chestItem")));
    }

    @Override
    default void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        MammalProperties.super.createGenes(tag, maleProperties);
        AbstractHorse female = getEntity();
        AbstractHorse male = (AbstractHorse) maleProperties;
        tag.putDouble("maxHealth", male.getAttributeBaseValue(Attributes.MAX_HEALTH) + female.getAttributeBaseValue(Attributes.MAX_HEALTH));
        tag.putDouble("jumpStrength", male.getAttributeBaseValue(Attributes.JUMP_STRENGTH) + female.getAttributeBaseValue(Attributes.JUMP_STRENGTH));
        tag.putDouble("movementSpeed", male.getAttributeBaseValue(Attributes.MOVEMENT_SPEED) + female.getAttributeBaseValue(Attributes.MOVEMENT_SPEED));
    }

    @Override
    default void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        MammalProperties.super.applyGenes(tag, babyProperties);
        AbstractHorse baby = (AbstractHorse) babyProperties;
        EntityHelpers.setNullableAttribute(baby, Attributes.MAX_HEALTH, (tag.getDouble("maxHealth") + generateRandomMaxHealth()) / 3);
        EntityHelpers.setNullableAttribute(baby, Attributes.JUMP_STRENGTH, (tag.getDouble("jumpStrength") + generateRandomJumpStrength()) / 3);
        EntityHelpers.setNullableAttribute(baby, Attributes.MOVEMENT_SPEED, (tag.getDouble("movementSpeed") + generateRandomSpeed()) / 3);
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
        if (getGender() == Gender.MALE && isReadyToMate())
        {
            EntityHelpers.findFemaleMate((Animal & TFCAnimalProperties) this);
        }
    }

    private float generateRandomMaxHealth()
    {
        final Random random = getEntity().getRandom();
        return 15.0F + random.nextInt(8) + random.nextInt(9);
    }

    private double generateRandomJumpStrength()
    {
        final Random random = getEntity().getRandom();
        return 0.4F + random.nextDouble() * 0.2D + random.nextDouble() * 0.2D + random.nextDouble() * 0.2D;
    }

    private double generateRandomSpeed()
    {
        final Random random = getEntity().getRandom();
        return (0.45F + random.nextDouble() * 0.3D + random.nextDouble() * 0.3D + random.nextDouble() * 0.3D) * 0.25D;
    }

}
