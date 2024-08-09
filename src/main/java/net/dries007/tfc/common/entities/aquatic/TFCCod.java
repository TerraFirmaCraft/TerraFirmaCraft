/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.GetHookedGoal;
import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class TFCCod extends Cod implements AquaticMob
{
    public TFCCod(EntityType<? extends Cod> type, Level level)
    {
        super(type, level);
        moveControl = new TFCFishMoveControl(this);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(1, new GetHookedGoal(this));
        EntityHelpers.replaceAvoidEntityGoal(this, goalSelector, 2);
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, WaterAnimal.class, 8f, 5f, 5.4f, e -> Helpers.isEntity(e, TFCTags.Entities.OCEAN_PREDATORS)));
    }

    @Override
    public ItemStack getBucketItemStack()
    {
        return new ItemStack(TFCItems.COD_BUCKET.get());
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
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.ANIMAL_IGNORED_PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }
}
