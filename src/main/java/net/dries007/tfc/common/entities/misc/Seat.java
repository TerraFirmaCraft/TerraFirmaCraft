/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.entities.TFCEntities;

public class Seat extends Entity
{
    public static void sit(Level level, BlockPos pos, Entity sitter)
    {
        if (!level.isClientSide)
        {
            Seat seat = TFCEntities.SEAT.get().create(level);
            assert seat != null;
            seat.moveTo(pos, 0f, 0f);
            level.addFreshEntity(seat);
            sitter.startRiding(seat);
        }
    }

    @Nullable
    public static Entity getSittingEntity(Level level, BlockPos pos)
    {
        List<Seat> entities = level.getEntitiesOfClass(Seat.class, new AABB(pos));
        if (!entities.isEmpty())
        {
            List<Entity> passengers = entities.get(0).getPassengers();
            if (!passengers.isEmpty())
            {
                return passengers.get(0);
            }
        }
        return null;
    }

    public Seat(EntityType<?> type, Level level)
    {
        super(type, level);
        noPhysics = true;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (!isVehicle())
        {
            setRemoved(RemovalReason.DISCARDED);
        }
    }

    @Override
    public double getPassengersRidingOffset()
    {
        return -0.25;
    }

    @Override
    protected boolean repositionEntityAfterLoad()
    {
        return false;
    }

    @Override
    protected void defineSynchedData()
    {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
