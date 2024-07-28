/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.util;

import java.util.List;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;

import static org.junit.jupiter.api.Assertions.*;

public class ForgeRuleTest
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
    public void testIsConsistent()
    {
        for (ForgeRule r1 : ForgeRule.VALUES)
            for (ForgeRule r2 : ForgeRule.VALUES)
                for (ForgeRule r3 : ForgeRule.VALUES)
                    assertEquals(
                        ForgeRule.isConsistent(r1, r2, r3),
                        anyMatch(r1, r2, r3)
                    );
    }

    private boolean anyMatch(ForgeRule r1, ForgeRule r2, ForgeRule r3)
    {
        for (ForgeStep s1 : ForgeStep.VALUES)
            for (ForgeStep s2 : ForgeStep.VALUES)
                for (ForgeStep s3 : ForgeStep.VALUES)
                    if (r1.matches(s1, s2, s3) && r2.matches(s1, s2, s3) && r3.matches(s1, s2, s3))
                        return true;
        return false;
    }

    @Test
    public void testCalculateOptimalStepsToTarget()
    {
        for (ForgeRule r1 : ForgeRule.VALUES)
            for (ForgeRule r2 : ForgeRule.VALUES)
                for (ForgeRule r3 : ForgeRule.VALUES)
                    if (ForgeRule.isConsistent(r1, r2, r3))
                        assertEquals(
                            calculateExpectedOptimalSteps(r1, r2, r3),
                            ForgeRule.calculateOptimalStepsToTarget(75, List.of(r1, r2, r3))
                        );
    }

    private int calculateExpectedOptimalSteps(ForgeRule r1, ForgeRule r2, ForgeRule r3)
    {
        int expected = Integer.MAX_VALUE;
        for (ForgeStep s1 : ForgeStep.VALUES)
            for (ForgeStep s2 : ForgeStep.VALUES)
                for (ForgeStep s3 : ForgeStep.VALUES)
                {
                    final int distance = 3 + ForgeStep.getOptimalStepsToTarget(75 - s1.step() - s2.step() - s3.step());
                    if (distance < expected && r1.matches(s1, s2, s3) && r2.matches(s1, s2, s3) && r3.matches(s1, s2, s3))
                    {
                        expected = distance;
                    }
                }
        return expected;
    }
}
