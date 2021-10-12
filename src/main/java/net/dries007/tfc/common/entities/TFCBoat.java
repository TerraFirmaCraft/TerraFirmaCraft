package net.dries007.tfc.common.entities;

import java.util.function.Supplier;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class TFCBoat extends Boat
{
    private final Supplier<? extends Item> drop;

    public TFCBoat(EntityType<? extends Boat> type, Level level, Supplier<? extends Item> drop)
    {
        super(type, level);
        this.drop = drop;
    }

    @Override
    public Item getDropItem()
    {
        return drop.get();
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
