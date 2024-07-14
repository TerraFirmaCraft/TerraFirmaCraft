/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;

public class TFCMinecartChest extends MinecartChest
{
    public static final EntityDataAccessor<ItemStack> DATA_CART_ITEM = SynchedEntityData.defineId(TFCMinecartChest.class, EntityDataSerializers.ITEM_STACK);
    public static final EntityDataAccessor<ItemStack> DATA_CHEST_ITEM = SynchedEntityData.defineId(TFCMinecartChest.class, EntityDataSerializers.ITEM_STACK);

    public TFCMinecartChest(EntityType<? extends MinecartChest> entityType, Level level)
    {
        super(entityType, level);
    }

    @Override
    public int getContainerSize()
    {
        return 18;
    }

    @Override
    public SlotAccess getSlot(final int index)
    {
        return index >= 0 && index < this.getContainerSize() ? new SlotAccess()
        {
            @Override
            public ItemStack get()
            {
                return getItem(index);
            }

            @Override
            public boolean set(ItemStack stack)
            {
                if (!TFCChestBlockEntity.isValid(stack))
                {
                    return false;
                }
                setItem(index, stack);
                return true;
            }
        } : SlotAccess.NULL;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("cartItem", getPickResult().save(registryAccess()));
        tag.put("chestItem", getChestItem().save(registryAccess()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setPickResult(ItemStack.parseOptional(registryAccess(), tag.getCompound("cartItem")));
        setChestItem(ItemStack.parseOptional(registryAccess(), tag.getCompound("chestItem")));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_CHEST_ITEM, ItemStack.EMPTY);
        builder.define(DATA_CART_ITEM, ItemStack.EMPTY);
    }

    public ItemStack getChestItem()
    {
        return entityData.get(DATA_CHEST_ITEM);
    }

    public void setChestItem(ItemStack item)
    {
        entityData.set(DATA_CHEST_ITEM, item.copy());
    }

    public void setPickResult(ItemStack item)
    {
        entityData.set(DATA_CART_ITEM, item.copy());
    }

    @Override
    @NotNull
    public ItemStack getPickResult()
    {
        return entityData.get(DATA_CART_ITEM).copy();
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory)
    {
        return new RestrictedChestContainer(TFCContainerTypes.CHEST_9x2.get(), windowId, inventory, this, 2);
    }

    @Override
    public BlockState getDisplayBlockState()
    {
        return getChestItem().getItem() instanceof BlockItem blockItem ? blockItem.getBlock().defaultBlockState() : Blocks.AIR.defaultBlockState();
    }

    @Override
    public boolean hasCustomDisplay()
    {
        return true; // tells vanilla to render getDisplayBlockState
    }

    @Override
    protected Item getDropItem()
    {
        return getPickResult().getItem();
    }
}
