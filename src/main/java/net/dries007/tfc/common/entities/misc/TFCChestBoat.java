/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforge.neoforged.common.capabilities.Capability;
import net.neoforge.neoforged.common.util.LazyOptional;
import net.neoforge.neoforged.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.InventoryWrapper;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;

public class TFCChestBoat extends ChestBoat implements ISlotCallback
{
    public static final EntityDataAccessor<ItemStack> CHEST_ITEM = SynchedEntityData.defineId(TFCChestBoat.class, EntityDataSerializers.ITEM_STACK);

    private final Supplier<? extends Item> drop;
    private @Nullable LazyOptional<IItemHandler> inventoryHandler;

    public TFCChestBoat(EntityType<? extends Boat> type, Level level, Supplier<? extends Item> drop)
    {
        super(type, level);
        this.drop = drop;
    }

    @Nullable
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        if (this.getLootTable() != null && player.isSpectator())
        {
            return null;
        }
        else
        {
            this.unpackLootTable(inv.player);
            return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x2.get(), windowId, inv, this, 2);
        }
    }

    @Override
    public void destroy(DamageSource source)
    {
        super.destroy(source);
        spawnAtLocation(getChestItem().copy());
    }

    @Override
    public int getContainerSize()
    {
        return 18;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return TFCChestBlockEntity.isValid(stack);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return canPlaceItem(slot, stack);
    }

    @Override
    public void invalidateCaps()
    {
        invalidateInventoryHandler();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap)
    {
        return getCapability(cap, null);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.ITEM)
        {
            return getInventoryHandler().cast();
        }
        return LazyOptional.empty();
    }

    private void invalidateInventoryHandler()
    {
        if (inventoryHandler != null)
        {
            inventoryHandler.invalidate();
            inventoryHandler = null;
        }
    }

    private LazyOptional<IItemHandler> getInventoryHandler()
    {
        if (inventoryHandler != null)
        {
            return inventoryHandler;
        }
        final InventoryWrapper itemHandler = new InventoryWrapper(this, this);
        inventoryHandler = LazyOptional.of(() -> itemHandler);
        return inventoryHandler;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(CHEST_ITEM, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setChestItem(ItemStack.of(tag.getCompound("chestItem")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("chestItem", getChestItem().save(new CompoundTag()));
    }

    @Override
    public EntityType<?> getType()
    {
        return super.getType();
    }

    @Override
    public Item getDropItem()
    {
        return drop.get();
    }

    public ItemStack getChestItem()
    {
        return entityData.get(CHEST_ITEM);
    }

    public void setChestItem(ItemStack stack)
    {
        entityData.set(CHEST_ITEM, stack.copy());
    }

}
