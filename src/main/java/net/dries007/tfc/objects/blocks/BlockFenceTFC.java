package net.dries007.tfc.objects.blocks;

import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;

public class BlockFenceTFC extends BlockFence
{
    public final BlockLogTFC.Wood wood;

    public BlockFenceTFC(BlockLogTFC.Wood wood)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        this.wood = wood;
    }
}
