/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.util.Mth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.ForgeSteps;

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
        final List<Tuple3<ForgeRule>> rules = cross3(ForgeRule.values());
        final List<Tuple3<ForgeStep>> steps = cross3(ForgeStep.values());

        for (Tuple3<ForgeRule> rule : rules)
        {
            if (ForgeRule.isConsistent(rule.e1, rule.e2, rule.e3))
            {
                Assertions.assertTrue(anyStepsMatch(steps, rule), "The rules {" + rule.e1 + ", " + rule.e2 + ", " + rule.e3 + "} were marked consistent but they did not find a matching set of steps.");
            }
            else
            {
                Assertions.assertFalse(anyStepsMatch(steps, rule), "The rules {" + rule.e1 + ", " + rule.e2 + ", " + rule.e3 + "} were marked inconsistent but a set of steps were found matching them");
            }
        }
    }

    @Test
    public void testCalculateOptimalStepsToTarget()
    {
        final List<Tuple3<ForgeRule>> rules = cross3(ForgeRule.values());
        final List<Tuple3<ForgeStep>> steps = cross3(ForgeStep.values());

        for (Tuple3<ForgeRule> rule : rules)
        {
            if (!ForgeRule.isConsistent(rule.e1, rule.e2, rule.e3))
            {
                continue;
            }

            final int target = 50 + new Random().nextInt(50);
            int expected = Integer.MAX_VALUE;
            Tuple3<ForgeStep> expectedStep = null;
            for (Tuple3<ForgeStep> step : steps)
            {
                final int distance = 3 + ForgeStep.getOptimalStepsToTarget(target - step.e1.step() - step.e2.step() - step.e3.step());
                if (stepsMatch(step, rule) && distance < expected)
                {
                    expected = distance;
                    expectedStep = step;
                }
            }

            final int actual = ForgeRule.calculateOptimalStepsToTarget(target, List.of(rule.e1, rule.e2, rule.e3));

            Assertions.assertNotNull(expectedStep, "Expected a minimum to be found");
            Assertions.assertEquals(expected, actual, "Expected minimum : " + expected + " for rules " + rule.e1 + ", " + rule.e2 + ", " + rule.e3 + ", got " + actual + " using the steps " + expectedStep.e1 + ", " + expectedStep.e2 + ", " + expectedStep.e3);
        }
    }

    private boolean anyStepsMatch(List<Tuple3<ForgeStep>> steps, Tuple3<ForgeRule> rule)
    {
        return steps.stream().anyMatch(s -> stepsMatch(s, rule));
    }

    private boolean stepsMatch(Tuple3<ForgeStep> step, Tuple3<ForgeRule> rule)
    {
        final ForgeSteps steps = new ForgeSteps(
            Optional.of(step.e1),
            Optional.of(step.e2),
            Optional.of(step.e3),
            0
        );
        return rule.e1.matches(steps) && rule.e2.matches(steps) && rule.e3.matches(steps);
    }

    private <T> List<Tuple3<T>> cross3(T[] elements)
    {
        return Arrays.stream(elements).flatMap(e1 -> Arrays.stream(elements).flatMap(e2 -> Arrays.stream(elements).map(e3 -> new Tuple3<>(e1, e2, e3)))).toList();
    }

    record Tuple3<T>(T e1, T e2, T e3) {}
}
