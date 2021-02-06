package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.LogPileContainer;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class LogPileTileEntity extends InventoryTileEntity
{
    private static final ITextComponent NAME = new TranslationTextComponent(MOD_ID + ".tile_entity.log_pile");

    private boolean isContainerOpen;

    public LogPileTileEntity()
    {
        this(TFCTileEntities.LOG_PILE.get(), 4, NAME);
        this.isContainerOpen = false;
    }

    public LogPileTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type, inventorySlots, defaultName);
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        nbt.putBoolean("isContainerOpen", isContainerOpen);
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        isContainerOpen = nbt.getBoolean("isContainerOpen");
        return super.save(nbt);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        if (level != null && !level.isClientSide())
        {
            for (int i = 0; i < 4; i++)
            {
                if (!inventory.getStackInSlot(i).isEmpty())
                {
                    super.setAndUpdateSlots(slot);
                    return;
                }
            }
            if (!isContainerOpen)
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            }
        }
        super.setAndUpdateSlots(slot);
    }

    public void setContainerOpen(boolean isOpen)
    {
        isContainerOpen = isOpen;
        setAndUpdateSlots(-1);
    }

    /**
     * Insert one log into the pile
     *
     * @param stack the log ItemStack to be inserted
     * @return true if one log was inserted, false otherwise
     */
    public boolean insertLog(ItemStack stack)
    {
        stack.setCount(1);
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.insertItem(i, stack, false).isEmpty())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Try to insert logs into every possible slot
     *
     * @param stack the log ItemStack to be inserted
     * @return 0 if none was inserted, number of logs inserted in the pile otherwise
     */
    public int insertLogs(ItemStack stack)
    {
        int start = stack.getCount();
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            stack = inventory.insertItem(i, stack, false);
            if (stack.isEmpty())
            {
                break;
            }
        }
        int remaining = stack.isEmpty() ? 0 : stack.getCount();
        return start - remaining;
    }

    /**
     * @return A single log for the purpose of pick block
     */
    public ItemStack getLog()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return inventory.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    public boolean isFull()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty() || stack.getCount() < 4)
            {
                return false;
            }
        }
        return true;
    }

    public int countLogs()
    {
        int logs = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            logs += inventory.getStackInSlot(i).getCount();
        }
        return logs;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem().is(TFCTags.Items.LOG_PILE_LOGS);
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory inv, PlayerEntity player)
    {
        return new LogPileContainer(this, inv, windowID);
    }
}
