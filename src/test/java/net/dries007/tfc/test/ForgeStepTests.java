/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.MutableForgeSteps;

import static org.junit.jupiter.api.Assertions.*;

public class ForgeStepTests extends TestHelper
{
    @Test
    public void testGetOptimalStepsToTarget()
    {
        for (int i = 0; i < ForgeStep.LIMIT; i++)
        {
            final int minStepHeuristic = Mth.ceil(i / 16f);
            final int maxStepHeuristic = minStepHeuristic + 1 + 1; // steps to N % 15 + steps to N % 3 + steps to N

            assertTrue(ForgeStep.getOptimalStepsToTarget(i) >= minStepHeuristic);
            assertTrue(ForgeStep.getOptimalStepsToTarget(i) <= maxStepHeuristic);
        }

        assertEquals(0, ForgeStep.getOptimalStepsToTarget(0));
        assertEquals(2, ForgeStep.getOptimalStepsToTarget(1)); // +16 -15
        assertEquals(1, ForgeStep.getOptimalStepsToTarget(2)); // +2
        assertEquals(3, ForgeStep.getOptimalStepsToTarget(3)); // +2 +7 -6
        assertEquals(2, ForgeStep.getOptimalStepsToTarget(4)); // +7 -3
        assertEquals(3, ForgeStep.getOptimalStepsToTarget(5)); // +7 +7 -9
        assertEquals(3, ForgeStep.getOptimalStepsToTarget(6)); // +2 +7 -3
    }

    @Test
    public void testProperties()
    {
        final MutableForgeSteps steps = exampleSteps();

        assertEquals(ForgeStep.SHRINK, steps.last());
        assertEquals(ForgeStep.HIT_HARD, steps.secondLast());
        assertEquals(ForgeStep.BEND, steps.thirdLast());

        assertEquals(4, steps.total());
    }

    @Test
    public void testForgeStepsMatchesRules()
    {
        final MutableForgeSteps steps = exampleSteps();

        assertTrue(ForgeRule.SHRINK_LAST.matches(steps));
        assertTrue(ForgeRule.HIT_SECOND_LAST.matches(steps));
        assertTrue(ForgeRule.BEND_THIRD_LAST.matches(steps));
    }

    @Test
    public void testWriteReadEmpty()
    {
        final MutableForgeSteps before = new MutableForgeSteps();
        final CompoundTag tag = before.write(new CompoundTag());
        final MutableForgeSteps after = new MutableForgeSteps().read(tag);
        assertEquals(before, after);
    }

    @Test
    public void testWriteReadExample()
    {
        final MutableForgeSteps before = exampleSteps();
        final CompoundTag tag = before.write(new CompoundTag());
        final MutableForgeSteps after = new MutableForgeSteps().read(tag);
        assertEquals(before, after);
    }

    private MutableForgeSteps exampleSteps()
    {
        final MutableForgeSteps steps = new MutableForgeSteps();

        steps.addStep(ForgeStep.UPSET); // Not included
        steps.addStep(ForgeStep.BEND); // Third Last
        steps.addStep(ForgeStep.HIT_HARD); // Second Last
        steps.addStep(ForgeStep.SHRINK); // Last

        return steps;
    }
}
