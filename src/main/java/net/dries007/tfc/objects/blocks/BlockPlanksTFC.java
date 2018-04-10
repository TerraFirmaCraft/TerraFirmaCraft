package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.Wood;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import java.util.EnumMap;

public class BlockPlanksTFC extends Block
{
    private static final EnumMap<Wood, BlockPlanksTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockPlanksTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockPlanksTFC(Wood wood)
    {
        super(Material.WOOD);
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        this.wood = wood;
    }
}
