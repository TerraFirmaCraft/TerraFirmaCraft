/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;


import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlacedItemBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static void convertPlacedItemToPitKiln(Level level, BlockPos pos, ItemStack strawStack)
    {
        level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(placedItem -> {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            NonNullList<ItemStack> items = Helpers.extractAllItems(placedItem.inventory);

            // Replace the block
            level.setBlockAndUpdate(pos, TFCBlocks.PIT_KILN.get().defaultBlockState());
            placedItem.setRemoved();
            // Play placement sound
            level.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
            // Copy TE data
            level.getBlockEntity(pos, TFCBlockEntities.PIT_KILN.get()).ifPresent(pitKiln -> {
                // Copy inventory
                Helpers.insertAllItems(pitKiln.inventory, items);
                // Copy misc data
                pitKiln.isHoldingLargeItem = placedItem.isHoldingLargeItem;
                pitKiln.addStraw(strawStack, 0);
            });
        });
    }

    public static int getSlotSelected(BlockHitResult rayTrace)
    {
        Vec3 location = rayTrace.getLocation();
        boolean x = Math.round(location.x) < location.x;
        boolean z = Math.round(location.z) < location.z;
        return (x ? 1 : 0) + (z ? 2 : 0);
    }

    public static final int SLOT_LARGE_ITEM = 0;
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.placed_item");
    public boolean isHoldingLargeItem;

    public PlacedItemBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.PLACED_ITEM.get(), pos, state);
    }

    protected PlacedItemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, self -> new InventoryItemHandler(self, 4), NAME);
        this.isHoldingLargeItem = false;
    }

    public boolean onRightClick(Player player, ItemStack stack, BlockHitResult rayTrace)
    {
        Vec3 location = rayTrace.getLocation();
        return onRightClick(player, stack, Math.round(location.x) < location.x, Math.round(location.z) < location.z);
    }

    public boolean insertItem(Player player, ItemStack stack, BlockHitResult rayTrace)
    {
        return insertItem(player, stack, getSlotSelected(rayTrace));
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(Player player, ItemStack stack, boolean x, boolean z)
    {
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || player.isShiftKeyDown())
        {
            ItemStack current;
            if (isHoldingLargeItem)
            {
                current = inventory.getStackInSlot(SLOT_LARGE_ITEM);
            }
            else
            {
                current = inventory.getStackInSlot(slot);
            }

            // Try and grab the item
            if (!current.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, current.copy());
                inventory.setStackInSlot(isHoldingLargeItem ? SLOT_LARGE_ITEM : slot, ItemStack.EMPTY);

                // This is set to false no matter what happens earlier
                isHoldingLargeItem = false;

                updateBlock();
                return true;
            }
        }
        else if (!stack.isEmpty())
        {
            return insertItem(player, stack, slot);
        }
        return false;
    }

    public boolean insertItem(Player player, ItemStack stack, int slot)
    {
        // Try and insert an item
        // Check the size of item to determine if insertion is possible, or if it requires the large slot
        Size size = ItemSizeManager.get(stack).getSize(stack);
        if (Helpers.isItem(stack, TFCTags.Items.PLACED_ITEM_BLACKLIST) || (TFCConfig.SERVER.usePlacedItemWhitelist.get() && !Helpers.isItem(stack, TFCTags.Items.PLACED_ITEM_WHITELIST))) return false;
        if (size.isEqualOrSmallerThan(TFCConfig.SERVER.maxPlacedItemSize.get()) && !isHoldingLargeItem)
        {
            // Normal and smaller can be placed normally
            if (inventory.getStackInSlot(slot).isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.split(1);
                }
                inventory.setStackInSlot(slot, input);
                updateBlock();
                return true;
            }
        }
        else if (!size.isEqualOrSmallerThan(TFCConfig.SERVER.maxPlacedItemSize.get()) && size.isEqualOrSmallerThan(TFCConfig.SERVER.maxPlacedLargeItemSize.get())) // Very Large or Huge
        {
            // Large items are placed in the single center slot
            if (isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.split(1);
                }
                inventory.setStackInSlot(SLOT_LARGE_ITEM, input);
                isHoldingLargeItem = true;
                updateBlock();
                return true;
            }
        }
        return false;
    }

    public ItemStack getCloneItemStack(BlockState state, BlockHitResult hit)
    {
        return inventory.getStackInSlot(getSlotSelected(hit)).copy();
    }

    public boolean holdingLargeItem()
    {
        return isHoldingLargeItem;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        isHoldingLargeItem = nbt.getBoolean("isHoldingLargeItem");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putBoolean("isHoldingLargeItem", isHoldingLargeItem);
        super.saveAdditional(nbt);
    }

    protected void updateBlock()
    {
        if (isEmpty() && level != null)
        {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
        }
        else
        {
            markForBlockUpdate();
        }
    }

    protected boolean isEmpty()
    {
        if (isHoldingLargeItem && inventory.getStackInSlot(SLOT_LARGE_ITEM).isEmpty())
        {
            return true;
        }
        for (int i = 0; i < 4; i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}
