/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.AquaticMob;
import net.dries007.tfc.common.entities.ai.TFCFishMoveControl;
import net.dries007.tfc.util.Helpers;

public class Manatee extends Cod implements AquaticMob
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return AbstractFish.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    public Manatee(EntityType<? extends Cod> type, Level level)
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
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(Fluids.WATER);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        // no-op vanilla's Bucketable implementation. We don't want to be bucketed at all.
        return Helpers.isItem(player.getItemInHand(hand), Items.WATER_BUCKET) ? InteractionResult.FAIL : super.mobInteract(player, hand);
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level.getBlockState(blockPosition()), TFCTags.Blocks.PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }
}
