package net.dries007.tfc.objects.items.rock;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemAxe;

import java.util.EnumMap;

public class ItemRockAxe extends ItemAxe
{
    private static final EnumMap<Rock.Category, ItemRockAxe> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockAxe get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockAxe(Rock.Category category)
    {
        super(category.toolMaterial, 1.5f * category.toolMaterial.getAttackDamage(), -3);
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("axe", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "axe");
        OreDictionaryHelper.register(this, "axe", "stone");
        OreDictionaryHelper.register(this, "axe", "stone", category);
    }
}
