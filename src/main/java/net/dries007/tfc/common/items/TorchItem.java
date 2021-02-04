package net.dries007.tfc.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.WallOrFloorItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.TFCEventFactory;

public class TorchItem extends WallOrFloorItem
{
    public TorchItem(Block floorBlock, Block wallBlockIn, Properties propertiesIn)
    {
        super(floorBlock, wallBlockIn, propertiesIn);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        // we cancel the event every time
        TFCEventFactory.startFire(world, pos, world.getBlockState(pos), context.getClickedFace(), context.getPlayer(), context.getItemInHand());
        return super.useOn(context);
    }
}
