/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.MutableForgeSteps;

public class ForgeRuleTests
{
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

            final int actual = ForgeRule.calculateOptimalStepsToTarget(target, rule.e1, rule.e2, rule.e3);

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
        final MutableForgeSteps instance = new MutableForgeSteps();
        instance.addStep(step.e1);
        instance.addStep(step.e2);
        instance.addStep(step.e3);
        return rule.e1.matches(instance) && rule.e2.matches(instance) && rule.e3.matches(instance);
    }

    private <T> List<Tuple3<T>> cross3(T[] elements)
    {
        return Arrays.stream(elements).flatMap(e1 -> Arrays.stream(elements).flatMap(e2 -> Arrays.stream(elements).map(e3 -> new Tuple3<>(e1, e2, e3)))).toList();
    }

    record Tuple3<T>(T e1, T e2, T e3) {}
}
