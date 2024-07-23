package net.dries007.tfc.test;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import net.dries007.tfc.common.component.fluid.FluidComponent;

public class TestAssertions extends Assertions
{
    public static void assertEquals(FluidComponent expected, @Nullable FluidComponent actual)
    {
        assertNotNull(actual);
        if (!FluidStack.matches(expected.content(), actual.content())) fail("expected: " + expected + ", actual: " + actual);
    }

    public static void assertEquals(FluidStack expected, FluidStack actual)
    {
        if (!FluidStack.matches(expected, actual)) fail("expected: " + expected + ", actual: " + actual);
    }

    public static void assertEquals(ItemStack expected, ItemStack actual)
    {
        if (!ItemStack.matches(expected, actual)) fail("expected: " + expected + ", actual: " + actual);
    }

    public static void assertNotEquals(ItemStack expected, ItemStack actual)
    {
        if (ItemStack.matches(expected, actual)) fail("expected: " + expected + ", actual: " + actual);
    }
}
