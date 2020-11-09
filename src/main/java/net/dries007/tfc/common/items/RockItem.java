package net.dries007.tfc.common.items;

import java.util.function.Supplier;

import net.dries007.tfc.common.TFCItemGroup;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.PebbleBlock;
import net.dries007.tfc.common.types.Rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RockItem extends Item
{
    private final Supplier<Block> pebble;

    public RockItem(Supplier<Block> pebble)
    {
        super((new Properties()).tab(TFCItemGroup.MISC));
        this.pebble = pebble;
    }

    public PebbleBlock getPebble()
    {
        return (PebbleBlock) pebble.get();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) // this method is specifically when you right-click on a block
    {
        World world = context.getLevel();
        if (!world.isClientSide())
        {
            PlayerEntity player = context.getPlayer();
            if (player != null)
            {
                if (player.isShiftKeyDown())
                {
                    BlockPos pos = context.getClickedPos();
                    BlockState clickedBlockState = world.getBlockState(pos);
                    Direction direction = context.getHorizontalDirection();
                    if (clickedBlockState.getBlock() instanceof PebbleBlock)
                    {
                        PebbleBlock clickedPebble = (PebbleBlock) clickedBlockState.getBlock();
                        if (getPebble().is(clickedPebble))
                        {
                            int rocks = clickedBlockState.getValue(PebbleBlock.ROCKS);
                            if (rocks < 3)
                            {
                                world.setBlock(pos, clickedBlockState.setValue(PebbleBlock.ROCKS, rocks + 1).setValue(PebbleBlock.FACING, direction), 1);
                                player.getMainHandItem().shrink(1);
                                return ActionResultType.CONSUME; // the same thing as PASS but it doesn't swing the player's arm
                            }
                        }
                    }
                    else if (clickedBlockState.isFaceSturdy(world, pos, Direction.UP) && world.getBlockState(pos.above()).isAir(world, pos.above())) // if there's an open block, we'll just place the thing
                    {
                        world.setBlock(pos.above(), getPebble().defaultBlockState().setValue(PebbleBlock.FACING, direction), 1);
                        player.getMainHandItem().shrink(1);
                        return ActionResultType.CONSUME;
                    }
                }
            }

        }
        return ActionResultType.FAIL;
    }
}
