package net.dries007.tfc.objects.items.rock;

import com.google.common.collect.ImmutableSet;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.minecraft.item.ItemTool;

import java.util.EnumMap;

public class ItemRockJavelin extends ItemTool
{
    private static final EnumMap<Rock.Category, ItemRockJavelin> MAP = new EnumMap<>(Rock.Category.class);

    public static ItemRockJavelin get(Rock.Category category)
    {
        return MAP.get(category);
    }

    public ItemRockJavelin(Rock.Category category)
    {
        super(1f * category.toolMaterial.getAttackDamage(), -1, category.toolMaterial, ImmutableSet.of());
        if (MAP.put(category, this) != null) throw new IllegalStateException("There can only be one.");
        OreDictionaryHelper.register(this, "javelin");
        OreDictionaryHelper.register(this, "javelin", "stone");
        OreDictionaryHelper.register(this, "javelin", "stone", category);
    }
}
