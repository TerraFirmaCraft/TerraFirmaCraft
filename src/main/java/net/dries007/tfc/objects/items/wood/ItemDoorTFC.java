package net.dries007.tfc.objects.items.wood;

import java.util.EnumMap;

import net.minecraft.item.ItemDoor;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockDoorTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class ItemDoorTFC extends ItemDoor
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
}
