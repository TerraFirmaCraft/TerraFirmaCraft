/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.JarShelfBlock;
import net.dries007.tfc.common.blocks.JarsBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class JarsBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public boolean use(Player player, ItemStack held, BlockHitResult result)
    {
        if (result.getLocation().y - result.getBlockPos().getY() > 15 / 16f)
        {
            return false;
        }
        if (held.getItem() instanceof BlockItem bi && bi.getBlock() instanceof JarShelfBlock shelf)
        {
            assert level != null;
            Direction dir = player.getDirection();
            if (dir.getAxis().isVertical())
                dir = Direction.NORTH;
            final BlockPos pos = getBlockPos();
            final BlockState shelfState = shelf.defaultBlockState().setValue(JarShelfBlock.FACING, dir);
            if (!JarShelfBlock.canHangOnWall(level, pos, shelfState))
                return false;
            if (!(level.getBlockState(pos).getBlock() instanceof JarShelfBlock))
            {
                final NonNullList<ItemStack> items = Helpers.extractAllItems(inventory);
                level.setBlockAndUpdate(pos, shelfState);
                if (!player.isCreative())
                    held.shrink(1);
                if (level.getBlockEntity(pos) instanceof JarsBlockEntity shelfBlockEntity)
                {
                    Helpers.insertAllItems(shelfBlockEntity.inventory, items);
                    level.setBlockAndUpdate(pos, JarsBlock.updateStateValues(level, pos, level.getBlockState(pos)));
                }
                return true;
            }
            return false;
        }
        final int slot = PlacedItemBlockEntity.getSlotSelected(result);
        final ItemStack current = inventory.getStackInSlot(slot);
        if (held.isEmpty() && !current.isEmpty())
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(slot, 1, false));
            BlockState state = JarsBlock.updateStateValues(player.level(), getBlockPos(), getBlockState());
            player.level().setBlockAndUpdate(getBlockPos(), JarsBlock.isEmptyContents(state) ? Blocks.AIR.defaultBlockState() : state);
            return true;
        }
        else if (current.isEmpty() && isItemValid(slot, held))
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.insertItem(slot, held.split(1), false));
            BlockState state = JarsBlock.updateStateValues(player.level(), getBlockPos(), getBlockState());
            player.level().setBlockAndUpdate(getBlockPos(), JarsBlock.isEmptyContents(state) ? Blocks.AIR.defaultBlockState() : state);
            return true;
        }
        return false;
    }

    public JarsBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.JARS.get(), pos, state);
    }

    public JarsBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(4));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.SHELF_JARS);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

}
