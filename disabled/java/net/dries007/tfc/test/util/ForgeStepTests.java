/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgeSteps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final ForgeSteps steps = exampleSteps();

        assertEquals(ForgeStep.SHRINK, steps.last());
        assertEquals(ForgeStep.HIT_HARD, steps.secondLast());
        assertEquals(ForgeStep.BEND, steps.thirdLast());

        assertEquals(4, steps.total());
    }

    @Test
    public void testForgeStepsMatchesRules()
    {
        final ForgeSteps steps = exampleSteps();

        assertTrue(ForgeRule.SHRINK_LAST.matches(steps));
        assertTrue(ForgeRule.HIT_SECOND_LAST.matches(steps));
        assertTrue(ForgeRule.BEND_THIRD_LAST.matches(steps));
    }

    @Test
    public void testWriteReadEmpty()
    {
        final ForgeSteps before = new ForgeSteps();
        final CompoundTag tag = before.write(new CompoundTag());
        final ForgeSteps after = new ForgeSteps().read(tag);
        assertEquals(before, after);
    }

    @Test
    public void testWriteReadExample()
    {
        final ForgeSteps before = exampleSteps();
        final CompoundTag tag = before.write(new CompoundTag());
        final ForgeSteps after = new ForgeSteps().read(tag);
        assertEquals(before, after);
    }

    private ForgeSteps exampleSteps()
    {
        final ForgeSteps steps = new ForgeSteps();

        steps.addStep(ForgeStep.UPSET); // Not included
        steps.addStep(ForgeStep.BEND); // Third Last
        steps.addStep(ForgeStep.HIT_HARD); // Second Last
        steps.addStep(ForgeStep.SHRINK); // Last

        return steps;
    }
}
