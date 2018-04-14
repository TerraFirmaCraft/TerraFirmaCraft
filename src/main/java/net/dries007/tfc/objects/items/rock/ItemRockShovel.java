package net.dries007.tfc.objects.items.rock;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemSpade;

import java.util.EnumMap;

public class ItemRockShovel extends ItemSpade
{
    private static final EnumMap<Rock.Category, ItemRockShovel> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockShovel get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockShovel(Rock.Category category)
    {
        super(category.toolMaterial);
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        attackDamage = 1.5f * category.toolMaterial.getAttackDamage();
        setHarvestLevel("shovel", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "shovel");
        OreDictionaryHelper.register(this, "shovel", "stone");
        OreDictionaryHelper.register(this, "shovel", "stone", category);
    }
}
