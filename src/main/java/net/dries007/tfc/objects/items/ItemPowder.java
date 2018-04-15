package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Powder;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;

public class ItemPowder extends Item
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
}