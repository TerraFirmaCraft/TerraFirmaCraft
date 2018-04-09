package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class BlockPlanksTFC extends Block
{
    public final BlockLogTFC.Wood wood;

    public BlockPlanksTFC(BlockLogTFC.Wood wood)
    {
        super(Material.WOOD);
        this.wood = wood;
    }
}
