/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import net.minecraft.Util;
import net.minecraft.world.item.ItemStack;

/**
 * A fixed, immutable, cached implementation of {@link ItemSize}
 */
public record ItemSize(Size size, Weight weight) implements IItemSize
{
    private static final int SIZES = Size.values().length;
    private static final int WEIGHTS = Weight.values().length;

    private static final ItemSize[] CACHE = Util.make(new ItemSize[SIZES * WEIGHTS], array -> {
        for (Size size : Size.values())
        {
            for (Weight weight : Weight.values())
            {
                array[size.ordinal() + SIZES * weight.ordinal()] = new ItemSize(size, weight);
            }
        }
    });

    /**
     * Gets an instance of an {@link ItemSize} with the given size and weight.
     * Since each item gets a capability instance, and this (default) implementation is immutable, we can cache all possible instances and return them on demand here.
     */
    public static ItemSize of(Size size, Weight weight)
    {
        return CACHE[size.ordinal() + SIZES * weight.ordinal()];
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return size;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return weight;
    }
}
