/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.util.Mth;

import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ForgeStepTests
{
    @Test
    public void testOptimalPaths()
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
}
