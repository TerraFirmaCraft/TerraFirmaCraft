package net.dries007.tfc.util;

import java.util.Random;

import net.minecraft.network.FriendlyByteBuf;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class KnappingPatternTests
{
    @Test
    public void testTrueByDefault()
    {
        KnappingPattern p = new KnappingPattern();
        for (int i = 0; i < 25; i++) assertTrue(p.get(i), "index=" + i);
    }

    @Test
    public void testSetAll()
    {
        KnappingPattern p = new KnappingPattern();
        p.setAll(true);
        for (int i = 0; i < 25; i++) assertTrue(p.get(i), "index = " + i);
        p.setAll(false);
        for (int i = 0; i < 25; i++) assertFalse(p.get(i), "index = " + i);
    }

    @Test
    public void testGetAndSet()
    {
        for (int i = 0; i < 25; i++)
        {
            KnappingPattern p = new KnappingPattern();
            p.set(i, false);
            assertFalse(p.get(i));
            p.set(i, true);
            assertTrue(p.get(i));
        }
    }

    @Test
    public void testGetAndSetCoordinates()
    {
        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                KnappingPattern p = new KnappingPattern();
                p.set(x, y, false);
                assertFalse(p.get(x, y));
                p.set(x, y, true);
                assertTrue(p.get(x, y));
            }
        }
    }

    @RepeatedTest(value = 10)
    public void testNetworkEncodeDecode()
    {
        Random random = new Random();
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        int width = random.nextInt(5), height = random.nextInt(5);
        KnappingPattern p = new KnappingPattern(width, height, random.nextBoolean());
        for (int i = 0; i < width * height; i++) p.set(i, random.nextBoolean());

        p.toNetwork(buffer);
        KnappingPattern p0 = KnappingPattern.fromNetwork(buffer);

        assertEquals(p, p0);
    }
}
