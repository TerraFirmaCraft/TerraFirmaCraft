/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.blockentities.CrateBlockEntity;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class CrateBlock extends DeviceBlock
{
    public CrateBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (level.getBlockEntity(pos) instanceof CrateBlockEntity crate)
        {
            final ItemStackHandler inv = crate.getInventory();
            if (stack.isEmpty())
            {
                // Hand is empty on a double click because we assume insertion was successful
                // if it wasn't, well, why are we attempting a double click lol
                if (crate.checkDoubleClick(level.getGameTime()))
                {
                    Helpers.mergeInsertAll(inv, player.getInventory());
                    Helpers.playSound(level, pos, SoundEvents.WOOD_PLACE);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                if (player.isShiftKeyDown())
                {
                    for (int i = 0; i < CrateBlockEntity.SLOTS; i++)
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, Helpers.removeStack(inv, i));
                    }
                }
                else
                {
                    ItemStack extracted = ItemStack.EMPTY;
                    for (int i = 0; i < CrateBlockEntity.SLOTS; i++)
                    {
                        final ItemStack slot = inv.getStackInSlot(i);
                        if (slot.isEmpty())
                            continue;
                        final int max = slot.getMaxStackSize();
                        if (extracted.getCount() >= max)
                            break;
                        final ItemStack removed = inv.extractItem(i, max - extracted.getCount(), false);
                        if (extracted.isEmpty())
                        {
                            extracted = removed;
                        }
                        else
                        {
                            extracted.grow(removed.getCount());
                        }
                    }
                    if (!extracted.isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(player, extracted);
                    }
                }

                Helpers.playSound(level, pos, SoundEvents.WOOD_HIT);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (crate.isItemValid(0, stack))
            {
                ItemStack remainder = stack;
                for (int i = 0; i < CrateBlockEntity.SLOTS && !remainder.isEmpty(); i++)
                {
                    remainder = Helpers.mergeInsertStack(inv, i, remainder);
                }
                player.setItemInHand(hand, remainder);
                crate.recordInsertClick(level.getGameTime());

                Helpers.playSound(level, pos, SoundEvents.WOOD_PLACE);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return TFCConfig.SERVER.crateEnableAutomation.get();
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getBlockEntity(pos) instanceof CrateBlockEntity crate ? ItemHandlerHelper.calcRedstoneFromInventory(crate.getInventory()) : 0;
    }
}
