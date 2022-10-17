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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoldingMinecart extends AbstractMinecart
{
    public static void copyMinecart(AbstractMinecart oldCart, AbstractMinecart newCart)
    {
        newCart.setYRot(oldCart.getYRot());
        newCart.setXRot(oldCart.getXRot());
        newCart.setDeltaMovement(oldCart.getDeltaMovement());
        if (oldCart.hasCustomName())
        {
            newCart.setCustomName(oldCart.getCustomName());
        }
    }

    public static final EntityDataAccessor<ItemStack> DATA_HOLD_ITEM = SynchedEntityData.defineId(HoldingMinecart.class, EntityDataSerializers.ITEM_STACK);

    public HoldingMinecart(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    public HoldingMinecart(Level level, double x, double y, double z)
    {
        super(TFCEntities.HOLDING_MINECART.get(), level, x, y, z);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        if (player.isSecondaryUseActive() && player.isShiftKeyDown() && !level.isClientSide)
        {
            ItemHandlerHelper.giveItemToPlayer(player, getPickResult());
            setHoldItem(ItemStack.EMPTY);

            final Minecart minecart = new Minecart(level, getX(), getY(), getZ());
            copyMinecart(this, minecart);
            discard();
            level.addFreshEntity(minecart);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public Type getMinecartType()
    {
        return Type.CHEST;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.put("holdItem", getHoldItem().save(new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setHoldItem(ItemStack.of(tag.getCompound("holdItem")));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_HOLD_ITEM, ItemStack.EMPTY);
    }

    public ItemStack getHoldItem()
    {
        return entityData.get(DATA_HOLD_ITEM);
    }

    public void setHoldItem(ItemStack item)
    {
        entityData.set(DATA_HOLD_ITEM, item.copy());
    }

    @Override
    @NotNull
    public ItemStack getPickResult()
    {
        return entityData.get(DATA_HOLD_ITEM).copy();
    }

    @Override
    public BlockState getDisplayBlockState()
    {
        if (getHoldItem().getItem() instanceof BlockItem blockItem)
        {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.hasProperty(SealableDeviceBlock.SEALED) && getHoldItem().hasTag())
            {
                state = state.setValue(SealableDeviceBlock.SEALED, true);
            }
            return state;
        }
        else
        {
            return Blocks.AIR.defaultBlockState();
        }
    }

    @Override
    public void destroy(DamageSource source)
    {
        super.destroy(source);
        if (level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            spawnAtLocation(getHoldItem().copy());
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
