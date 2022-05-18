/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import java.util.stream.IntStream;

import net.minecraft.util.Mth;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.river.Flow;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static net.dries007.tfc.world.river.Flow.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowTests extends TestHelper
{
    @Test
    public void testFromAngle()
    {
        Assertions.assertEquals(NNN, Flow.fromAngle(Mth.PI / 2f));
        assertEquals(SSS, Flow.fromAngle(-Mth.PI / 2f));
        assertEquals(EEE, Flow.fromAngle(0));
        assertEquals(WWW, Flow.fromAngle(Mth.PI));
        assertEquals(WWW, Flow.fromAngle(-Mth.PI));
    }

    @Test
    public void testVectorYieldsAngle()
    {
        for (Flow flow : Flow.values())
        {
            if (flow != NONE)
            {
                assertEquals(flow, Flow.fromAngle((float) Mth.atan2(-flow.getVector().z(), flow.getVector().x())));
            }
        }
    }

    @Test
    public void testLerpLeftToNone()
    {
        evaluateLerp(NNN, NONE,
            NNN, NNN, NNN, NONE, NONE);
    }

    @Test
    public void testLerpNoneToRight()
    {
        evaluateLerp(NONE, NNN,
            NONE, NONE, NNN, NNN, NNN);
    }

    @Test
    public void testLerp90CW()
    {
        evaluateLerp(NNN, EEE,
            NNN, NNE, N_E, NEE, EEE);

        evaluateLerp(N_E, S_E,
            N_E, NEE, EEE, SEE, S_E);
    }

    @Test
    public void testLerp90CCW()
    {
        evaluateLerp(NNN, WWW,
            NNN, NNW, N_W, NWW, WWW);

        evaluateLerp(S_E, N_E,
            S_E, SEE, EEE, NEE, N_E);
    }

    @Test
    public void testLerp180()
    {
        evaluateLerp(NNN, SSS,
            NNN, NNN, NONE, SSS, SSS);
    }

    @Test
    public void testLerpSquareAllNone()
    {
        evaluateLerpSquare(
            ___, ___,
            ___, ___,
            ___, ___, ___, ___, ___,
            ___, ___, ___, ___, ___,
            ___, ___, ___, ___, ___,
            ___, ___, ___, ___, ___,
            ___, ___, ___, ___, ___
        );
    }

    @Test
    public void testLerpSquareOneCorner()
    {
        evaluateLerpSquare(
            NNN, ___,
            ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, ___, ___, ___,
            NNN, ___, ___, ___, ___,
            ___, ___, ___, ___, ___,
            ___, ___, ___, ___, ___
        );
    }

    @Test
    public void testLerpSquareThreeCorners()
    {
        evaluateLerpSquare(
            NNN, ___,
            NNN, NNN,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, NNN, NNN,
            NNN, NNN, NNN, NNN, NNN,
            NNN, NNN, NNN, NNN, NNN
        );
    }

    @Test
    public void testLerpSquareAdjacentCorners()
    {
        evaluateLerpSquare(
            NNN, ___,
            NNN, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___
        );
    }

    @Test
    public void testLerpSquareOppositeCorners()
    {
        evaluateLerpSquare(
            NNN, ___,
            ___, NNN,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, ___, ___,
            NNN, NNN, NNN, NNN, NNN,
            ___, ___, NNN, NNN, NNN,
            ___, ___, NNN, NNN, NNN
        );
    }

    @Test
    public void testLerpSquare4x90()
    {
        evaluateLerpSquare(
            NNN, EEE,
            EEE, SSS,
            NNN, NNE, N_E, NEE, EEE,
            NNE, N_E, NEE, EEE, SEE,
            N_E, NEE, EEE, SEE, S_E,
            NEE, EEE, SEE, S_E, SSE,
            EEE, SEE, S_E, SSE, SSS
        );
    }

    private void evaluateLerp(Flow start, Flow end, Flow... expected)
    {
        final Flow[] actual = IntStream.range(0, expected.length)
            .mapToObj(i -> Flow.lerp(start, end, (float) i / (expected.length - 1)))
            .toArray(Flow[]::new);
        assertArrayEquals(expected, actual);
    }

    private void evaluateLerpSquare(Flow topLeft, Flow topRight, Flow bottomLeft, Flow bottomRight, Flow... expected)
    {
        final int width = Math.round((float) Math.sqrt(expected.length));
        for (int i = 0; i < expected.length; i++)
        {
            final int x = i % width, y = i / width;
            final Flow actual = Flow.lerp(topLeft, topRight, bottomLeft, bottomRight, (float) x / (width - 1), (float) y / (width - 1));
            assertEquals(expected[i], actual, "At index " + i + ", (" + x + ", " + y + ")");
        }
    }
}
