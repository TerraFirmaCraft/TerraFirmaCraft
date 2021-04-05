package net.dries007.tfc.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.container.ItemStackHandlerCallback;
import net.dries007.tfc.util.Helpers;

public abstract class InventoryTileEntity extends TFCTileEntity implements INamedContainerProvider, ISlotCallback, IClearable
{
    protected final ItemStackHandler inventory;
    protected final LazyOptional<IItemHandler> inventoryCapability;
    protected ITextComponent customName, defaultName;

    public InventoryTileEntity(TileEntityType<?> type, int inventorySlots, ITextComponent defaultName)
    {
        super(type);

        this.inventory = new ItemStackHandlerCallback(this, inventorySlots);
        this.inventoryCapability = LazyOptional.of(() -> InventoryTileEntity.this.inventory);
        this.defaultName = defaultName;
    }

    public InventoryTileEntity(TileEntityType<?> type, ItemStackHandler inventory, ITextComponent defaultName)
    {
        super(type);

        this.inventory = inventory;
        this.inventoryCapability = LazyOptional.of(() -> inventory);
        this.defaultName = defaultName;
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return customName != null ? customName : defaultName;
    }

    @Nullable
    public ITextComponent getCustomName()
    {
        return customName;
    }

    public void setCustomName(ITextComponent customName)
    {
        this.customName = customName;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        if (nbt.contains("CustomName"))
        {
            customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
        }
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        super.load(state, nbt);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt)
    {
        if (customName != null)
        {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }
        nbt.put("inventory", inventory.serializeNBT());
        return super.save(nbt);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == null)
        {
            return inventoryCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void clearContent()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void onRemove()
    {
        if (level == null) return;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i));
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        markDirtyFast();
    }

    public boolean canInteractWith(PlayerEntity player)
    {
        return true;
    }

    public void onReplaced()
    {
        inventoryCapability.invalidate();
    }

    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
    {
        return null;
    }
}
