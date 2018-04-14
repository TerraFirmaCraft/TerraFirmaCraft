package net.dries007.tfc.objects.items.rock;

import com.google.common.collect.ImmutableSet;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemTool;

import java.util.EnumMap;

public class ItemRockKnife extends ItemTool
{
    private static final EnumMap<Rock.Category, ItemRockKnife> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockKnife get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockKnife(Rock.Category category)
    {
        super(0.5f * category.toolMaterial.getAttackDamage(), 3, category.toolMaterial, ImmutableSet.of());
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("knife", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "knife");
        OreDictionaryHelper.register(this, "knife", "stone");
        OreDictionaryHelper.register(this, "knife", "stone", category);
    }
}
