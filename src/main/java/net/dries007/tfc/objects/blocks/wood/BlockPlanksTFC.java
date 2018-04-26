package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;

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
        setSoundType(SoundType.WOOD);
        setHardness(2.0F).setResistance(5.0F);
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "plank", "wood");
        OreDictionaryHelper.register(this, "plank", "wood", wood);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
