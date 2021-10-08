package net.dries007.tfc.common.entities;

import javax.annotation.Nonnull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;

public class TFCBoat extends Boat
{
    public static final EntityDataAccessor<Integer> TFC_WOOD_ID = SynchedEntityData.defineId(TFCBoat.class, EntityDataSerializers.INT);

    public TFCBoat(EntityType<? extends Boat> type, Level level)
    {
        super(type, level);
    }

    public TFCBoat(Level level, double x, double y, double z)
    {
        this(TFCEntities.BOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt)
    {
        nbt.putInt("wood_type", entityData.get(TFC_WOOD_ID));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt)
    {
        if (nbt.contains("wood_type", Constants.NBT.TAG_INT))
        {
            setWood(Wood.VALUES[nbt.getInt("wood_type")]);
        }
    }

    public void setWood(Wood wood)
    {
        entityData.set(TFC_WOOD_ID, wood.ordinal());
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(TFC_WOOD_ID, 0);
    }

    @Override
    public Item getDropItem()
    {
        return TFCItems.BOATS.get(Wood.VALUES[entityData.get(TFC_WOOD_ID)]).get();
    }
}
