/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.rock;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemRockToolHead extends ItemTFC implements IRockObject
{
    private static final EnumMap<Rock.ToolType, Map<RockCategory, ItemRockToolHead>> TABLE = new EnumMap<>(Rock.ToolType.class);

    public static ItemRockToolHead get(RockCategory cat, Rock.ToolType type)
    {
        return TABLE.get(type).get(cat);
    }

    private final RockCategory category;
    private final Rock.ToolType type;

    public ItemRockToolHead(RockCategory category, Rock.ToolType type)
    {
        this.type = type;
        this.category = category;
        if (!TABLE.containsKey(type))
            TABLE.put(type, new HashMap<>());
        TABLE.get(type).put(category, this);

        OreDictionaryHelper.register(this, type, "head");
        OreDictionaryHelper.register(this, type, "head", category);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.SMALL; // Stored everywhere
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.LIGHT; // Stacksize = 32
    }

    @Nullable
    @Override
    public Rock getRock(ItemStack stack)
    {
        return null;
    }

    @Nonnull
    @Override
    public RockCategory getRockCategory(ItemStack stack)
    {
        return category;
    }
}
