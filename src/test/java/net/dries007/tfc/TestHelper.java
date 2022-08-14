/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

import net.minecraftforge.registries.GameData;

import net.dries007.tfc.common.recipes.ingredients.BlockIngredients;
import net.dries007.tfc.common.recipes.ingredients.TFCIngredients;
import net.dries007.tfc.common.recipes.outputs.ItemStackModifiers;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Ensure that we are bootstrapped before each test runs, to prevent errors from uncertain loading order.
 */
public class TestHelper
{
    private static final Random SEEDS = new Random();
    private static final AtomicBoolean BOOTSTRAP = new AtomicBoolean(false);

    @BeforeAll
    public static void setup()
    {
        bootstrap();
    }

    public synchronized static void bootstrap()
    {
        if (!BOOTSTRAP.get())
        {
            BOOTSTRAP.set(true);

            try
            {
                final Field field = SharedConstants.class.getDeclaredField("CURRENT_VERSION");
                field.setAccessible(true);
                field.set(null, DetectedVersion.BUILT_IN);
            }
            catch (NoSuchFieldException | IllegalAccessException e)
            {
                fail("Unable to set SharedConstants#CURRENT_VERSION", e);
            }

            Bootstrap.bootStrap();
            GameData.unfreezeData();

            // Various TFC bootstraps that we can do
            ItemStackModifiers.registerItemStackModifierTypes();
            BlockIngredients.registerBlockIngredientTypes();
            TFCIngredients.registerIngredientTypes();
        }
    }

    public static long seed()
    {
        long seed = SEEDS.nextLong();
        System.out.println("Seed " + seed);
        return seed;
    }
}
