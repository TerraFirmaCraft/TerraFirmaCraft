/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import java.util.EnumMap;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemPowder extends ItemTFC
{
    private static final EnumMap<Powder, ItemPowder> MAP = new EnumMap<>(Powder.class);

    public static ItemPowder get(Powder Powder)
    {
        return MAP.get(Powder);
    }

    public static ItemStack get(Powder Powder, int amount)
    {
        return new ItemStack(MAP.get(Powder), amount);
    }

    public final Powder Powder;

    public ItemPowder(Powder Powder)
    {
        this.Powder = Powder;
        if (MAP.put(Powder, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "dust");
        OreDictionaryHelper.register(this, "dust", Powder);
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.TINY;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }
}