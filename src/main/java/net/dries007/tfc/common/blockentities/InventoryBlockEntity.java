/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.util.Helpers;

/**
 * An abstraction for a tile entity containing at least, an inventory (item handler) capability
 * However, the inventory itself is generic.
 */
public abstract class InventoryBlockEntity<C extends IItemHandlerModifiable & INBTSerializable<CompoundTag>> extends TFCBlockEntity implements ISlotCallback, MenuProvider, Clearable
{
    public static InventoryFactory<ItemStackHandler> defaultInventory(int slots)
    {
        return self -> new InventoryItemHandler(self, slots);
    }

    protected final C inventory;
    protected final SidedHandler.Builder<IItemHandler> sidedInventory;
    @Nullable protected Component customName;
    protected Component defaultName;

    public InventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InventoryFactory<C> inventoryFactory, Component defaultName)
    {
        super(type, pos, state);

        this.inventory = inventoryFactory.create(this);
        this.sidedInventory = new SidedHandler.Builder<>(InventoryBlockEntity.this.inventory);
        this.defaultName = defaultName;
    }

    /**
     * Returns an internal view of the inventory of this block. This is used for internal operations within TFC that are coded specifically to
     * this block, for example, the implementation of a bock entity renderer. <strong>DO NOT</strong> use for interactions that are meant to be
     * interoperable with other mods, or other devices within TFC, for instance inserting on right-click.
     */
    public C getInventory()
    {
        return inventory;
    }

    @Override
    public Component getDisplayName()
    {
        return customName == null ? defaultName : customName;
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return null;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        if (nbt.contains("CustomName"))
        {
            customName = Component.Serializer.fromJson(nbt.getString("CustomName"));
        }
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        if (customName != null)
        {
            nbt.putString("CustomName", Component.Serializer.toJson(customName));
        }
        nbt.put("inventory", inventory.serializeNBT());
        super.saveAdditional(nbt);
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
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                Helpers.spawnItem(level, worldPosition, stack);
            }
        }
    }

    public void invalidateCapabilities()
    {
        sidedInventory.invalidate();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        setChanged();
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
        C create(InventoryBlockEntity<C> entity);
    }
}
