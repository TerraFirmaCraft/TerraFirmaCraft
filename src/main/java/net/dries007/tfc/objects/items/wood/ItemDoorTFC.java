/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.wood;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemDoor;
import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockDoorTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemDoorTFC extends ItemDoor implements IItemSize
{
    private static final Map<Tree, ItemDoorTFC> MAP = new HashMap<>();

    public static ItemDoorTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

    public ItemDoorTFC(BlockDoorTFC block)
    {
        super(block);
        if (MAP.put(block.wood, this) != null) throw new IllegalStateException("There can only be one.");
        wood = block.wood;
        OreDictionaryHelper.register(this, "door", "wood");
        //noinspection ConstantConditions
        OreDictionaryHelper.register(this, "door", "wood", wood.getRegistryName().getPath());
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.VERY_LARGE; // Can't be stored
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY; // Stacksize = 4
    }

    @Override
    public int getItemStackLimit(ItemStack stack)
    {
        return getStackSize(stack);
    }
}
