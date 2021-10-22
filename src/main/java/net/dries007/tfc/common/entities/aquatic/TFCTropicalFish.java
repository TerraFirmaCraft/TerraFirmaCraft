/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class TFCTropicalFish extends TropicalFish
{
    public TFCTropicalFish(EntityType<? extends TropicalFish> type, Level level)
    {
        super(type, level);
        moveControl = new TFCFishMoveControl(this);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Helpers.insertTFCAvoidGoal(this, goalSelector, 2);
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.TROPICAL_FISH_BUCKET.get());
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        return Helpers.bucketMobPickup(player, hand, this).orElse(super.mobInteract(player, hand));
    }
}
