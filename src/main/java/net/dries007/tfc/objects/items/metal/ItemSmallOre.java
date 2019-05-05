/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemSmallOre extends ItemTFC implements IMetalObject
{
    private static final Map<Ore, ItemSmallOre> MAP = new HashMap<>();

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
        OreDictionaryHelper.register(this, "ore", ore.getRegistryName().getPath());
        OreDictionaryHelper.register(this, "ore", ore.getRegistryName().getPath(), "small");
    }

    @Override
    public Metal getMetal(ItemStack stack)
    {
        return ore.getMetal();
    }

    @Override
    public int getSmeltAmount(ItemStack stack)
    {
        return 10; //todo: config
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
