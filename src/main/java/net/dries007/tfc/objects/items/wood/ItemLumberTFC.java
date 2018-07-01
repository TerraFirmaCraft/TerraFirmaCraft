/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.wood;

import java.util.EnumMap;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemLumberTFC extends ItemTFC
{
    private static final EnumMap<Wood, ItemLumberTFC> MAP = new EnumMap<>(Wood.class);

    public static ItemLumberTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public static ItemStack get(Wood wood, int amount)
    {
        return new ItemStack(MAP.get(wood), amount);
    }

    public final Wood wood;

    public ItemLumberTFC(Wood wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "lumber");
        OreDictionaryHelper.register(this, "lumber", wood);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }
}