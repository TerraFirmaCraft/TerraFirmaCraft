package net.dries007.tfc.objects.items.rock;

import java.util.EnumMap;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemBrickTFC extends Item
{
    private static final EnumMap<Rock, ItemBrickTFC> MAP = new EnumMap<>(Rock.class);

    public static ItemBrickTFC get(Rock ore)
    {
        return MAP.get(ore);
    }

    public static ItemStack get(Rock ore, int amount)
    {
        return new ItemStack(MAP.get(ore), amount);
    }

    public final Rock ore;

    public ItemBrickTFC(Rock rock)
    {
        this.ore = rock;
        if (MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
        setMaxDamage(0);
        OreDictionaryHelper.register(this, "brick");
        OreDictionaryHelper.register(this, "brick", rock);
        OreDictionaryHelper.register(this, "brick", rock.category);
    }
}
