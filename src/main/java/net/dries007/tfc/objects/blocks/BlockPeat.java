package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockPeat extends Block
{
    public BlockPeat(Material material)
    {
        super(material);
        setSoundType(SoundType.GROUND);
        setHardness(0.6F);
        setHarvestLevel("shovel", 0);
    }
}
