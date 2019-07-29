/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;

public final class ItemFlat extends ItemTFC
{
    private static final Map<Rock, ItemFlat> ROCK_MAP = new HashMap<>();

    public ItemFlat()
    {
        setMaxStackSize(0);
        setNoRepair();
        setHasSubtypes(false);
    }

    public ItemFlat(Rock rock)
    {
        this();
        if (ROCK_MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
