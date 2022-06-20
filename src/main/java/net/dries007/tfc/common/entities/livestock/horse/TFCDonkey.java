/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.TFCConfig;

public class TFCDonkey extends TFCChestedHorse
{
    public TFCDonkey(EntityType<? extends TFCDonkey> type, Level level)
    {
        super(type, level, TFCSounds.DONKEY, () -> SoundEvents.DONKEY_EAT, () -> SoundEvents.DONKEY_ANGRY, TFCConfig.SERVER.donkeyConfig);
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        super.createGenes(tag, maleProperties);
        tag.putBoolean("isMule", maleProperties instanceof TFCHorse);
    }

    @Override
    public EntityType<?> getEntityTypeForBaby()
    {
        final CompoundTag genes = getGenes();
        return genes != null && genes.contains("isMule") && genes.getBoolean("isMule") ? TFCEntities.MULE.get() : TFCEntities.DONKEY.get();
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.DONKEY_FOOD;
    }

    @Override
    public boolean checkExtraBreedConditions(TFCAnimalProperties otherAnimal)
    {
        if (super.checkExtraBreedConditions(otherAnimal) && (otherAnimal instanceof TFCDonkey || otherAnimal instanceof Horse))
        {
            AbstractHorse otherHorse = (AbstractHorse) otherAnimal;
            return TFCChestedHorse.vanillaParentingCheck(this) && TFCChestedHorse.vanillaParentingCheck(otherHorse);
        }
        return false;
    }

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        return super.canMate(otherAnimal) && otherAnimal instanceof TFCAnimalProperties props && checkExtraBreedConditions(props);
    }
}
