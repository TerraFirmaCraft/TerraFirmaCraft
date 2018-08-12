/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.Size;
import net.dries007.tfc.api.util.Weight;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }
}
