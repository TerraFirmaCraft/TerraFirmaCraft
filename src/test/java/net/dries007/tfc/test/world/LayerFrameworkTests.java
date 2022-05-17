/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.dries007.tfc.TestHelper;
import net.dries007.tfc.world.layer.framework.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LayerFrameworkTests extends TestHelper
{
    @Test
    public void testAreaFactoriesReturnNewInstances()
    {
        AreaFactory factory = SourceLayerImpl.INSTANCE.apply(new Random().nextLong());

        Area firstArea = factory.get();
        Area secondArea = factory.get();

        assertNotEquals(firstArea, secondArea);
    }

    @Test
    public void testThreadedAreaAccessWithNewInstances()
    {
        final Random random = new Random();
        final AreaFactory factory = SourceLayerImpl.INSTANCE.apply(random.nextLong());
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final List<Future<Integer>> futures = new ArrayList<>();
        for (int t = 0; t < 10; t++)
        {
            Area threadLocalArea = factory.get();
            futures.add(service.submit(() -> {
                // just query the area a bunch of times
                int result = 0;
                for (int x = 0; x < 100; x++)
                    for (int z = 0; z < 100; z++)
                        result += threadLocalArea.get(x, z);
                System.out.println("Thread: " + Thread.currentThread() + " result: " + result);
                return result;
            }));
        }
        resolve(service, futures);
    }

    @Test
    public void testThreadedAreaAccessWithLayerFactory()
    {
        final Random random = new Random();
        final ConcurrentArea<Integer> layer = new ConcurrentArea<>(SourceLayerImpl.INSTANCE.apply(random.nextLong()), i -> i);
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final List<Future<Integer>> futures = new ArrayList<>();
        for (int t = 0; t < 10; t++)
        {
            futures.add(service.submit(() -> {
                // just query the area a bunch of times
                int result = 0;
                for (int x = 0; x < 100; x++)
                    for (int z = 0; z < 100; z++)
                        result += layer.get(x, z);
                System.out.println("Thread: " + Thread.currentThread() + " result: " + result);
                return result;
            }));
        }
        resolve(service, futures);
    }

    @Test
    public void testSourceLayerIsDeterministic()
    {
        final long seed = System.currentTimeMillis();
        final Random random = new Random(seed);
        final SourceLayer source = (sourceContext, x, z) -> sourceContext.random().nextInt();
        final Area layer = source.apply(seed).get();

        int[] values = new int[10_000], coordinates = new int[20_000];
        for (int i = 0; i < 10_000; i++)
        {
            final int x = random.nextInt(), z = random.nextInt();
            coordinates[i << 1] = x;
            coordinates[(i << 1) | 1] = z;
            values[i] = layer.get(x, z);
        }

        for (int i = 0; i < 10_000; i++)
        {
            final int x = coordinates[i << 1], z = coordinates[(i << 1) | 1];
            Assertions.assertEquals(values[i], layer.get(x, z), () -> "Layer emitted different values at x=" + x + ", z=" + z);
        }
    }

    private void resolve(ExecutorService service, List<Future<Integer>> futures)
    {
        Integer result = null;
        for (Future<Integer> f : futures)
        {
            try
            {
                Integer i = f.get();
                if (result == null) result = i;
                else assertEquals(result, i, "Different threads got different results");
            }
            catch (InterruptedException | ExecutionException e)
            {
                fail("Future: " + f + " died", e);
            }
        }
        service.shutdown();
    }

    enum SourceLayerImpl implements SourceLayer
    {
        INSTANCE;

        @Override
        public int apply(AreaContext context, int x, int z)
        {
            int value = 0;
            for (int i = 0; i < 1000; i++)
            {
                value += context.random().nextBoolean() ? 1 : 0;
            }
            return value;
        }
    }
}
