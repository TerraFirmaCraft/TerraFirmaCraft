/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.effect.TFCEffects;
import net.dries007.tfc.common.entities.IGlow;
import net.dries007.tfc.common.entities.TFCEntities;

public class GlowArrow extends AbstractArrow implements IGlow
{
    public static final EntityDataAccessor<BlockPos> DATA_LIGHT_POS = SynchedEntityData.defineId(GlowArrow.class, EntityDataSerializers.BLOCK_POS);

    public GlowArrow(Level level)
    {
        this(TFCEntities.GLOW_ARROW.get(), level);
    }

    public GlowArrow(Level level, LivingEntity entity)
    {
        super(TFCEntities.GLOW_ARROW.get(), entity, level);
    }

    public GlowArrow(Level level, double x, double y, double z)
    {
        super(TFCEntities.GLOW_ARROW.get(), x, y, z, level);
    }

    public GlowArrow(EntityType<? extends AbstractArrow> entity, Level level)
    {
        super(entity, level);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity entity)
    {
        super.doPostHurtEffects(entity);
        entity.addEffect(new MobEffectInstance(TFCEffects.GLOW_INK.get(), 200, 0), this.getEffectSource());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        saveLight(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        readLight(tag);
    }

    @Override
    public void remove(Entity.RemovalReason reason)
    {
        tryRemoveLight();
        super.remove(reason);
    }

    @Nullable
    @Override
    public Entity changeDimension(ServerLevel level, ITeleporter teleporter)
    {
        tryRemoveLight();
        return super.changeDimension(level, teleporter);
    }


    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_LIGHT_POS, BlockPos.ZERO);
    }

    @Override
    public void setLightPos(BlockPos pos)
    {
        entityData.set(DATA_LIGHT_POS, pos);
    }

    @Override
    public BlockPos getLightPos()
    {
        return entityData.get(DATA_LIGHT_POS);
    }

    @Override
    public int getLightUpdateDistanceSqr()
    {
        return 2 * 2;
    }

    @Override
    public int getLightUpdateInterval()
    {
        return getDeltaMovement().lengthSqr() < 0.01D ? 20 : 5;
    }

    @Override
    public void tick()
    {
        super.tick();
        IGlow.super.tickGlow();
        if (level().isClientSide && !inGround)
        {
            level().addParticle(ParticleTypes.GLOW_SQUID_INK, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public ItemStack getPickupItem()
    {
        return new ItemStack(Items.ARROW);
    }
}
