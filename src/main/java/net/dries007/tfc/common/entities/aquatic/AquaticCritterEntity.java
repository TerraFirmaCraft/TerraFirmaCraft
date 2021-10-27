/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import net.dries007.tfc.common.entities.AquaticMob;
import net.dries007.tfc.common.fluids.TFCFluids;

public class AquaticCritterEntity extends WaterAnimal implements AquaticMob
{
    public AquaticCritterEntity(EntityType<? extends WaterAnimal> type, Level level)
    {
        super(type, level);
    }

    @Override
    public void registerGoals()
    {
        super.registerGoals();
        // don't have the ability to swim, but will path randomly anyway, resulting in them walking around the seafloor.
        goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0F, 30));
    }

    @Override
    public boolean canSpawnIn(Fluid fluid)
    {
        return fluid.isSame(TFCFluids.SALT_WATER.getSource());
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel)
    {
        return new WaterBoundPathNavigation(this, pLevel);
    }
}
