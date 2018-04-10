package net.dries007.tfc.objects.blocks;

import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks;

public class BlockFenceGateTFC extends BlockFenceGate
{
    public final BlockLogTFC.Wood wood;

    public BlockFenceGateTFC(BlockLogTFC.Wood wood)
    {
        super(BlockPlanks.EnumType.OAK);
        this.wood = wood;
    }
}
