/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.GetHookedGoal;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Helpers;

public class TFCDolphin extends Dolphin implements AquaticMob
{
    public TFCDolphin(EntityType<? extends Dolphin> type, Level level)
    {
        super(type, level);
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(TFCFluids.SALT_WATER.getSource());
    }

    @Override
    protected void registerGoals()
    {
        goalSelector.addGoal(0, new BreathAirGoal(this));
        goalSelector.addGoal(0, new TryFindWaterGoal(this));
        goalSelector.addGoal(1, new GetHookedGoal(this));
        goalSelector.addGoal(4, new RandomSwimmingGoal(this, 1.0D, 10));
        goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(5, new DolphinJumpGoal(this, 10));
        goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2F, true));
        goalSelector.addGoal(8, new FollowBoatGoal(this));

        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, WaterAnimal.class, 1000, true, false, e -> Helpers.isEntity(e, TFCTags.Entities.HUNTED_BY_OCEAN_PREDATORS)));
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.ANIMAL_IGNORED_PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }
}
