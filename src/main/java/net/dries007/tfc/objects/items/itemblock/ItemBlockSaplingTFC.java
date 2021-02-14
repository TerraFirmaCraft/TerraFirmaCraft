package net.dries007.tfc.objects.items.itemblock;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

public class ItemBlockSaplingTFC extends ItemBlockTFC
{
    public ItemBlockSaplingTFC(BlockSaplingTFC block)
    {
        super(block);
    }

    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem)
    {
        if (!entityItem.world.isRemote && entityItem.getAge() >= entityItem.lifespan)
        {
            final BlockPos pos = entityItem.getPosition();
            if (entityItem.world.mayPlace(block, pos, false, EnumFacing.UP, null) && entityItem.world.setBlockState(pos, block.getDefaultState()))
            {
                entityItem.setDead();
                return true;
            }
            for (EnumFacing face : EnumFacing.HORIZONTALS)
            {
                final BlockPos offsetPos = pos.offset(face);
                if (entityItem.world.mayPlace(block, offsetPos, false, EnumFacing.UP, null) && entityItem.world.setBlockState(offsetPos, block.getDefaultState()))
                {
                    entityItem.setDead();
                    return true;
                }
            }
        }
        return false;
    }

}
