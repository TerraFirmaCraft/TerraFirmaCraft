/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.AnimationState;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.Helpers;

public class Penguin extends AmphibiousAnimal
{
    public final AnimationState walkingAnimation = new AnimationState();
    public final AnimationState swimmingAnimation = new AnimationState();

    public Penguin(EntityType<? extends AmphibiousAnimal> type, Level level)
    {
        super(type, level, TFCSounds.PENGUIN);
    }

    @Override
    public boolean isPlayingDeadEffective()
    {
        return false;
    }

    @Override
    public void tick()
    {
        if (level.isClientSide)
        {
            EntityHelpers.startOrStop(walkingAnimation, EntityHelpers.isMovingOnLand(this), tickCount);
            EntityHelpers.startOrStop(swimmingAnimation, EntityHelpers.isMovingInWater(this), tickCount);
        }
        super.tick();
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.PENGUIN_FOOD);
    }
}
