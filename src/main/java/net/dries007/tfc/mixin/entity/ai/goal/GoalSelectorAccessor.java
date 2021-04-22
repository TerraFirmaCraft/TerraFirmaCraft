package net.dries007.tfc.mixin.entity.ai.goal;

import java.util.Set;

import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.goal.PrioritizedGoal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GoalSelector.class)
public interface GoalSelectorAccessor
{
    @Accessor("availableGoals")
    Set<PrioritizedGoal> getAvailableGoals();
}
