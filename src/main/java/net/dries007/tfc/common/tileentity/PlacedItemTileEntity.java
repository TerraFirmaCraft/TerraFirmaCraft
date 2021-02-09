package net.dries007.tfc.common.tileentity;


import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

//todo: convert to respect item sizes
public class PlacedItemTileEntity extends InventoryTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.placed_item");

    public static final int SLOT_LARGE_ITEM = 0;
    public boolean isHoldingLargeItem;

    public PlacedItemTileEntity()
    {
        this(TFCTileEntities.PLACED_ITEM.get());
    }

    protected PlacedItemTileEntity(TileEntityType<?> type)
    {
        super(type, 4, NAME);
        this.isHoldingLargeItem = false;
    }

    public boolean onRightClick(PlayerEntity player, ItemStack stack, BlockRayTraceResult rayTrace)
    {
        Vector3d location = rayTrace.getLocation();
        return onRightClick(player, stack, Math.round(location.x) < location.x, Math.round(location.z) < location.z);
    }

    public boolean insertItem(PlayerEntity player, ItemStack stack, BlockRayTraceResult rayTrace)
    {
        Vector3d location = rayTrace.getLocation();
        boolean x = Math.round(location.x) < location.x;
        boolean z = Math.round(location.z) < location.z;
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        return insertItem(player, stack, slot);
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(PlayerEntity player, ItemStack stack, boolean x, boolean z)
    {
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        if (player.getItemInHand(Hand.MAIN_HAND).isEmpty() || player.isShiftKeyDown())
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
                player.addItem(current.split(1));
                inventory.setStackInSlot(slot, ItemStack.EMPTY);

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

    public boolean insertItem(PlayerEntity player, ItemStack stack, int slot)
    {
        // Try and insert an item
        // Check the size of item to determine if insertion is possible, or if it requires the large slot
        /*IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
        Size size = Size.NORMAL;
        if (sizeCap != null)
        {
            size = sizeCap.getSize(stack);
        }*/

        if (/*size.isSmallerThan(Size.VERY_LARGE) && */!isHoldingLargeItem)
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
        else if (/*!size.isSmallerThan(Size.VERY_LARGE)*/true) // Very Large or Huge
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

    public boolean holdingLargeItem()
    {
        return isHoldingLargeItem;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        isHoldingLargeItem = nbt.getBoolean("itemSize");
        super.load(state, nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putBoolean("itemSize", isHoldingLargeItem);
        return super.save(nbt);
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
