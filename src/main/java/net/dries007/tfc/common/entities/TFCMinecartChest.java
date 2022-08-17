/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.blockentities.TFCChestBlockEntity;
import net.dries007.tfc.common.container.RestrictedChestContainer;
import net.dries007.tfc.common.container.TFCContainerTypes;
import org.jetbrains.annotations.Nullable;

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
        tag.put("cartItem", (getPickResult() == null ? ItemStack.EMPTY : getPickResult()).save(new CompoundTag()));
        tag.put("chestItem", getChestItem().save(new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setPickResult(ItemStack.of(tag.getCompound("cartItem")));
        setChestItem(ItemStack.of(tag.getCompound("chestItem")));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_CHEST_ITEM, ItemStack.EMPTY);
        entityData.define(DATA_CART_ITEM, ItemStack.EMPTY);
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
    public void destroy(DamageSource source)
    {
        super.destroy(source);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            spawnAtLocation(getChestItem().copy());
        }
    }

    @Override
    public boolean hasCustomDisplay()
    {
        return true; // tells vanilla to render getDisplayBlockState
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemLike item)
    {
        if (item == Blocks.CHEST)
        {
            return null; // prevents drops in the superclass
        }
        return super.spawnAtLocation(item);
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
