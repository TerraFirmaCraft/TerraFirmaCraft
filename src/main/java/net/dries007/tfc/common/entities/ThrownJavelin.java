/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class ThrownJavelin extends AbstractArrow
{
    private static final EntityDataAccessor<Boolean> DATA_ENCHANT_GLOW = SynchedEntityData.defineId(ThrownJavelin.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrownJavelin.class, EntityDataSerializers.ITEM_STACK);

    private boolean dealtDamage = false;

    public ThrownJavelin(EntityType<? extends ThrownJavelin> type, Level level)
    {
        super(type, level);
    }

    public ThrownJavelin(Level level, LivingEntity entity, ItemStack stack)
    {
        this(TFCEntities.THROWN_JAVELIN.get(), level, entity, stack);
    }

    public ThrownJavelin(EntityType<? extends ThrownJavelin> type, Level level, LivingEntity entity, ItemStack stack)
    {
        super(type, entity, level);
        setItem(stack);
        setIsEnchantGlowing(stack.hasFoil());
    }

    @Override
    public void tick()
    {
        if (this.inGroundTime > 4)
        {
            this.dealtDamage = true;
        }
        super.tick();
    }

    @Override
    protected boolean tryPickup(Player player)
    {
        return super.tryPickup(player) || this.isNoPhysics() && this.ownedBy(player) && player.getInventory().add(this.getPickupItem());
    }

    @Override
    public void tickDespawn()
    {
        if (this.pickup != AbstractArrow.Pickup.ALLOWED)
        {
            super.tickDespawn();
        }
    }

    @Override
    public boolean shouldRender(double x, double y, double z)
    {
        return true;
    }

    @Nullable
    protected EntityHitResult findHitEntity(Vec3 pos1, Vec3 pos2)
    {
        return this.dealtDamage ? null : super.findHitEntity(pos1, pos2);
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        Entity hitEntity = result.getEntity();
        float damage = getItemAttackDamage();
        if (hitEntity instanceof LivingEntity livingentity)
        {
            damage += EnchantmentHelper.getDamageBonus(getItem(), livingentity.getMobType());
        }

        Entity owner = this.getOwner();
        this.dealtDamage = true;
        SoundEvent soundevent = SoundEvents.TRIDENT_HIT;
        if (hitEntity.hurt(DamageSource.trident(this, owner == null ? this : owner), damage))
        {
            if (hitEntity.getType() == EntityType.ENDERMAN)
            {
                return;
            }

            if (hitEntity instanceof LivingEntity livingVictim)
            {
                if (owner instanceof LivingEntity livingOwner)
                {
                    EnchantmentHelper.doPostHurtEffects(livingVictim, owner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingVictim);
                }

                this.doPostHurtEffects(livingVictim);
            }
        }

        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        this.playSound(soundevent, 1F, 1F);
    }


    @Override
    protected float getWaterInertia()
    {
        return 0.99F;
    }

    @Override
    public SoundEvent getDefaultHitGroundSoundEvent()
    {
        return SoundEvents.TRIDENT_HIT_GROUND;
    }

    /**
     * Slightly better than whacking like a sword
     * if not a javelin inside (for some reason), default to 8 which is the trident damage value
     */
    public float getItemAttackDamage()
    {
        return getItem().getItem() instanceof SwordItem javelin ? javelin.getDamage() * 1.1f : 8F;
    }

    @Override
    public ItemStack getPickupItem()
    {
        return getItem().copy();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.contains("item", Tag.TAG_COMPOUND))
        {
            setItem(ItemStack.of(tag.getCompound("item")));
            setIsEnchantGlowing(tag.getBoolean("glow"));
        }
        dealtDamage = tag.getBoolean("dealtDamage");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (!getItem().isEmpty())
        {
            tag.put("item", getItem().save(new CompoundTag()));
            tag.putBoolean("glow", isEnchantGlowing());
        }
        tag.putBoolean("dealtDamage", dealtDamage);
    }


    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_ENCHANT_GLOW, false);
        entityData.define(DATA_ITEM, ItemStack.EMPTY);
    }

    public ItemStack getItem()
    {
        return entityData.get(DATA_ITEM);
    }

    public void setItem(ItemStack item)
    {
        entityData.set(DATA_ITEM, item.copy());
    }

    public boolean isEnchantGlowing()
    {
        return entityData.get(DATA_ENCHANT_GLOW);
    }

    public void setIsEnchantGlowing(boolean glow)
    {
        entityData.set(DATA_ENCHANT_GLOW, glow);
    }

}
