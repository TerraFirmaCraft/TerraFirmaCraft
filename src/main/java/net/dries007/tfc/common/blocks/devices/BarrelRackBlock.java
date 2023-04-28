package net.dries007.tfc.common.blocks.devices;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.items.BarrelBlockItem;

public class BarrelRackBlock extends BottomSupportedDeviceBlock
{
    public BarrelRackBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.NOOP, BarrelBlock.RACK_SHAPE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof BarrelBlockItem blockItem && blockItem.getBlock().defaultBlockState().hasProperty(BarrelBlock.RACK))
        {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            final var res = blockItem.place(new BlockPlaceContext(player, hand, item, hit.withDirection(Direction.orderedByNearest(player)[0]).withPosition(pos)));
            final BlockState newState = level.getBlockState(pos);
            if (res.consumesAction() && newState.hasProperty(BarrelBlock.RACK))
            {
                level.setBlockAndUpdate(pos, newState.setValue(BarrelBlock.RACK, true));
            }
            return res;
        }
        return InteractionResult.PASS;
    }

}
