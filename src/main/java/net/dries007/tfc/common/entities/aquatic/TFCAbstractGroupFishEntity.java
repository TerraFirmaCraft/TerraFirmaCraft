package net.dries007.tfc.common.entities.aquatic;

import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.fish.AbstractGroupFishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.world.World;

import net.dries007.tfc.common.entities.PlacementPredicate;
import net.dries007.tfc.common.entities.ai.AquaticMovementController;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.mixin.entity.ai.goal.GoalSelectorAccessor;

public abstract class TFCAbstractGroupFishEntity extends AbstractGroupFishEntity
{
    public TFCAbstractGroupFishEntity(EntityType<? extends AbstractGroupFishEntity> type, World worldIn)
    {
        super(type, worldIn);
        moveControl = new AquaticMovementController(this, true, 1);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        Set<PrioritizedGoal> availableGoals = ((GoalSelectorAccessor) goalSelector).getAvailableGoals();
        availableGoals.removeIf(priority -> priority.getGoal() instanceof AvoidEntityGoal);

        goalSelector.addGoal(2, new TFCAvoidEntityGoal<>(this, PlayerEntity.class, 8.0F, 5.0D, 5.4D));
    }

    public static <T extends Entity> PlacementPredicate<T> createSpawnRules(float minTemp, float maxTemp, float minRain, float maxRain, Fluid fluid)
    {
        return new PlacementPredicate<T>().fluid(fluid).simpleClimate(minTemp, maxTemp, minRain, maxRain);
    }
}
