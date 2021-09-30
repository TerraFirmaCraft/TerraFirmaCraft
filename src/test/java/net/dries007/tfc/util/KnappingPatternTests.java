/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.network.FriendlyByteBuf;

import io.netty.buffer.Unpooled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

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
    public Stream<DynamicTest> testGetAndSetIndex()
    {
        return IntStream.range(0, 25)
            .mapToObj(i -> DynamicTest.dynamicTest("index = " + i, () -> {
                KnappingPattern p = new KnappingPattern();
                p.set(i, false);
                assertFalse(p.get(i));
                p.set(i, true);
                assertTrue(p.get(i));
            }));
    }

    @TestFactory
    public Stream<DynamicTest> testGetAndSetCoordinates()
    {
        return IntStream.range(0, 5)
            .mapToObj(x -> IntStream.range(0, 5)
                .mapToObj(y -> DynamicTest.dynamicTest("pos = (" + x + ", " + y + ")", () -> {
                    KnappingPattern p = new KnappingPattern();
                    p.set(x, y, false);
                    assertFalse(p.get(x, y));
                    p.set(x, y, true);
                    assertTrue(p.get(x, y));
                }))).flatMap(t -> t);
    }

    @TestFactory
    public Stream<DynamicTest> testSetFalseLeavesOthersUnchanged()
    {
        return IntStream.range(0, 5)
            .mapToObj(x -> IntStream.range(0, 5)
                .mapToObj(y -> DynamicTest.dynamicTest("pos = (" + x + ", " + y + ")", () -> {
                    KnappingPattern p = new KnappingPattern();
                    p.set(x, y, false);
                    assertFalse(p.get(x, y));
                    for (int x0 = 0; x0 < 5; x0++)
                        for (int y0 = 0; y0 < 5; y0++)
                            if (x0 != x || y0 != y)
                                assertTrue(p.get(x0, y0));
                }))).flatMap(t -> t);
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
