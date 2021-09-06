/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.ItemStackHandlerCallback;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;

/**
 * An abstraction for a tile entity containing at least, an inventory (item handler) capability
 * However, the inventory itself is generic.
 */
public abstract class InventoryTileEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TFCTileEntity implements ISlotCallback, Clearable
{
    public static <C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> InventoryFactory<ItemStackHandler> defaultInventory(int slots)
    {
        return self -> new ItemStackHandlerCallback(self, slots);
    }

    protected final C inventory;
    protected final SidedHandler.Builder<IItemHandler> sidedInventory;
    @Nullable protected Component customName;
    protected Component defaultName;

    public InventoryTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component defaultName)
    {
        super(type, pos, state);

        this.inventory = inventoryFactory.create(this);
        this.sidedInventory = new SidedHandler.Builder<>(InventoryTileEntity.this.inventory);
        this.defaultName = defaultName;
    }

    /**
     * Default implementation of {@link net.minecraft.world.MenuProvider#getDisplayName()} but without implementing the interface.
     */
    public Component getDisplayName()
    {
        return customName == null ? defaultName : customName;
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        if (nbt.contains("CustomName"))
        {
            customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        super.load(nbt);
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        if (customName != null)
        {
            nbt.putString("CustomName", Component.Serializer.toJson(customName));
        }
        nbt.put("inventory", inventory.serializeNBT());
        return super.save(nbt);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return sidedInventory.getSidedHandler(side).cast();
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

    public void ejectInventory()
    {
        assert level != null;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i));
        }
    }

    public void invalidateCapabilities()
    {
        for (LazyOptional<IItemHandler> handler : sidedInventory.getHandlers())
        {
            handler.invalidate();
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        markDirtyFast();
    }

    public boolean canInteractWith(Player player)
    {
        if (level == null || level.getBlockEntity(worldPosition) != this)
        {
            return false;
        }
        else
        {
            return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64;
        }
    }

    /**
     * A factory interface for the inventory field, allows self references in the constructor
     */
    @FunctionalInterface
    public interface InventoryFactory<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>>
    {
        C create(InventoryTileEntity<C> entity);
    }
}
