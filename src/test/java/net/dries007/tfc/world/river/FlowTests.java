package net.dries007.tfc.world.river;

import net.minecraft.util.Mth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FlowTests
{
    @Test
    public void testFromAngle()
    {
        assertEquals(Flow.NNN, Flow.fromAngle(Mth.PI / 2f));
        assertEquals(Flow.SSS, Flow.fromAngle(-Mth.PI / 2f));
        assertEquals(Flow.EEE, Flow.fromAngle(0));
        assertEquals(Flow.WWW, Flow.fromAngle(Mth.PI));
        assertEquals(Flow.WWW, Flow.fromAngle(-Mth.PI));
    }

    @Test
    public void testCombine()
    {
        for (Flow left : Flow.values())
        {
            for (Flow right : Flow.values())
            {
                for (float f = 0; f < 1; f += 0.001f)
                {
                    Flow.combine(left, right, f, true);
                    Flow.combine(left, right, f, false);
                }
            }
        }
    }
}
