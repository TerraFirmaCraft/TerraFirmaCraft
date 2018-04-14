package net.dries007.tfc.objects.items.rock;

import com.google.common.collect.ImmutableSet;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemTool;

import java.util.EnumMap;

public class ItemRockHammer extends ItemTool
{
    private static final EnumMap<Rock.Category, ItemRockHammer> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockHammer get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockHammer(Rock.Category category)
    {
        super(2f * category.toolMaterial.getAttackDamage(), -3.5f, category.toolMaterial, ImmutableSet.of());
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        setHarvestLevel("hammer", category.toolMaterial.getHarvestLevel());
        OreDictionaryHelper.register(this, "hammer");
        OreDictionaryHelper.register(this, "hammer", "stone");
        OreDictionaryHelper.register(this, "hammer", "stone", category);
    }
}
