/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.wood;

import java.util.EnumMap;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Size;
import net.dries007.tfc.objects.Weight;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockDoorTFC;
import net.dries007.tfc.util.IItemSize;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemDoorTFC extends ItemDoor implements IItemSize
{
    private static final EnumMap<Wood, ItemDoorTFC> MAP = new EnumMap<>(Wood.class);

    public static ItemDoorTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public ItemDoorTFC(BlockDoorTFC block)
    {
        super(block);
        if (MAP.put(block.wood, this) != null) throw new IllegalStateException("There can only be one.");
        wood = block.wood;
        OreDictionaryHelper.register(this, "door");
        OreDictionaryHelper.register(this, "door", wood);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.MEDIUM;
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
    }
}
