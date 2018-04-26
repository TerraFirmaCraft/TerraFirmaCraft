package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockTrapDoorWoodTFC extends BlockTrapDoor
{
    private static final EnumMap<Wood, BlockTrapDoorWoodTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockTrapDoorWoodTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockTrapDoorWoodTFC(Wood wood)
    {
        super(Material.WOOD);
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        OreDictionaryHelper.register(this, "wood", "trapdoor");
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
