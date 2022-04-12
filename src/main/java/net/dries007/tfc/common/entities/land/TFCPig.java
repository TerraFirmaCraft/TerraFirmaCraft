/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.land;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.config.TFCConfig;

public class TFCPig extends Mammal
{
    public TFCPig(EntityType<? extends Mammal> animal, Level level)
    {
        super(animal, level,
            () -> SoundEvents.PIG_AMBIENT, () -> SoundEvents.PIG_HURT, () -> SoundEvents.PIG_DEATH, () -> SoundEvents.PIG_STEP,
            TFCConfig.SERVER.pigFamiliarityCap, TFCConfig.SERVER.pigAdulthoodDays, TFCConfig.SERVER.pigUses, TFCConfig.SERVER.pigEatsRottenFood, TFCConfig.SERVER.pigChildCount, TFCConfig.SERVER.pigGestationDays);
    }

    @Override
    public void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.addCommonPreyGoals(this, goalSelector);
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.PIG_FOOD;
    }
}
