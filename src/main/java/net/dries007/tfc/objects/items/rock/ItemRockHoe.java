package net.dries007.tfc.objects.items.rock;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemHoe;

import java.util.EnumMap;

public class ItemRockHoe extends ItemHoe
{
    private static final EnumMap<Rock.Category, ItemRockHoe> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockHoe get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockHoe(Rock.Category category)
    {
        super(category.toolMaterial);
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("hoe", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "hoe");
        OreDictionaryHelper.register(this, "hoe", "stone");
        OreDictionaryHelper.register(this, "hoe", "stone", category);
    }
}
