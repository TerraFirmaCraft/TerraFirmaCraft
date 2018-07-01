/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.EnumMap;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.IMetalObject;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemSmallOre extends ItemTFC implements IMetalObject
{
    private static final EnumMap<Ore, ItemSmallOre> MAP = new EnumMap<>(Ore.class);

    public static ItemSmallOre get(Ore ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Ore ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Ore ore;

    public ItemSmallOre(Ore ore)
    {
        this.ore = ore;
        if (MAP.put(ore, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "ore");
        OreDictionaryHelper.register(this, "ore", ore);
        OreDictionaryHelper.register(this, "ore", ore, "small");
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ore.metal;
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return 10; //todo: config
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
