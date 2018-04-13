package net.dries007.tfc.objects.items;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.EnumMap;

public class ItemRock extends Item
{
    private static final EnumMap<Rock, ItemRock> MAP = new EnumMap<>(Rock.class);

    public static ItemRock get(Rock ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Rock ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Rock ore;

    public ItemRock(Rock rock)
    {
        this.ore = rock;
        if (MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "rock");
        OreDictionaryHelper.register(this, "rock", rock);
    }
}
