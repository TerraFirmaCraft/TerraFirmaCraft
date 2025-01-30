/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import java.util.function.IntConsumer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.blocks.devices.PowderkegBlock;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.PowderKegExplosion;

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

    private int fuse = -1;

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
        if (player.isSecondaryUseActive() && player.isShiftKeyDown() && !level().isClientSide)
        {
            ItemHandlerHelper.giveItemToPlayer(player, getPickResult());
            setHoldItem(ItemStack.EMPTY);

            final Minecart minecart = new Minecart(level(), getX(), getY(), getZ());
            copyMinecart(this, minecart);
            discard();
            level().addFreshEntity(minecart);
            return InteractionResult.sidedSuccess(level().isClientSide);
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
        tag.put("holdItem", getHoldItem().save(registryAccess()));
        tag.putInt("TNTFuse", fuse);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setHoldItem(ItemStack.parseOptional(registryAccess(), tag.getCompound("holdItem")));
        fuse = EntityHelpers.getIntOrDefault(tag, "TNTFuse", -1);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_HOLD_ITEM, ItemStack.EMPTY);
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
        final ItemStack stack = getHoldItem();
        if (stack.getItem() instanceof BlockItem blockItem)
        {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (state.hasProperty(SealableDeviceBlock.SEALED) && (stack.has(TFCComponents.BARREL) || stack.has(TFCComponents.CONTENTS)))
            {
                state = state.setValue(SealableDeviceBlock.SEALED, true);
            }
            if (state.hasProperty(PowderkegBlock.LIT) && isPrimed())
            {
                state = state.setValue(PowderkegBlock.LIT, true);
            }
            return state;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void destroy(DamageSource source)
    {
        super.destroy(source);
        if (Helpers.isDamageSource(source, DamageTypeTags.IS_FIRE) || Helpers.isDamageSource(source, DamageTypeTags.IS_EXPLOSION))
        {
            if (ifPowderkeg(this::explode))
            {
                return;
            }
        }
        if (level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS))
        {
            spawnAtLocation(getHoldItem().copy());
        }
    }

    @Override
    protected Item getDropItem()
    {
        return Items.MINECART;
    }

    @Override
    public boolean hasCustomDisplay()
    {
        return true; // tells vanilla to render getDisplayBlockState
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source)
    {
        if (distance >= 3.0F)
        {
            ifPowderkeg(this::explode);
        }
        return super.causeFallDamage(distance, multiplier, source);
    }

    @Override
    public void activateMinecart(int x, int y, int z, boolean powered)
    {
        if (powered && this.fuse < 0)
        {
            this.primeFuse();
        }
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 10) primeFuse();
        else super.handleEntityEvent(id);
    }

    public void primeFuse()
    {
        ifPowderkeg(str -> {
            this.fuse = 80;
            if (!this.level().isClientSide)
            {
                this.level().broadcastEntityEvent(this, (byte) 10);
                if (!this.isSilent())
                {
                    this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        });
    }


    public boolean isPrimed()
    {
        return fuse > -1;
    }

    @Override
    public void tick()
    {
        super.tick();
        ifPowderkeg(strength -> {
            if (this.fuse > 0)
            {
                --this.fuse;
                this.level().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5D, this.getZ(), 0.0D, 0.0D, 0.0D);
            }
            else if (this.fuse == 0)
            {
                this.explode(strength);
            }

            if (this.horizontalCollision)
            {
                final double delta = this.getDeltaMovement().horizontalDistanceSqr();
                if (delta >= (double) 0.01F)
                {
                    this.explode(strength);
                }
            }
        });
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.getDirectEntity() instanceof AbstractArrow arrow && arrow.isOnFire())
        {
            ifPowderkeg(this::explode);
        }
        return super.hurt(source, amount);
    }

    protected void explode(int strength)
    {
        if (!this.level().isClientSide)
        {
            final PowderKegExplosion explosion = new PowderKegExplosion(level(), null, getX(), getY(), getZ(), strength);
            explosion.explode();
            explosion.finalizeExplosion(true);
            this.discard();
        }
    }

    public boolean ifPowderkeg(IntConsumer toRun)
    {
        if (!TFCConfig.SERVER.powderKegEnabled.get())
        {
            return false;
        }
        final int str = getPowderkegStrength();
        if (str != 0)
        {
            toRun.accept(str);
            return true;
        }
        return false;
    }

    public int getPowderkegStrength()
    {
        final ItemStack stack = getHoldItem();
        if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof PowderkegBlock)
        {
            /* todo 1.21, needs saved block entity components
            final CompoundTag tag = stack.getTagElement(Helpers.BLOCK_ENTITY_TAG);
            if (tag != null)
            {
                final CompoundTag inventoryTag = tag.getCompound("inventory");
                final ItemStackHandler inventory = new ItemStackHandler();

                inventory.deserializeNBT(inventoryTag.getCompound("inventory"));

                return PowderkegBlockEntity.getStrength(inventory);
            }*/
        }
        return 0;
    }
}
