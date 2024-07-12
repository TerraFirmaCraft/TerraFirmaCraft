/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.misc;

import java.util.function.Supplier;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import net.dries007.tfc.common.items.ChestBlockItem;


public class TFCBoat extends Boat
{
    private final Supplier<EntityType<TFCChestBoat>> boatChest;
    private final Supplier<? extends Item> drop;

    public TFCBoat(EntityType<? extends Boat> type, Level level, Supplier<EntityType<TFCChestBoat>> boatChest, Supplier<? extends Item> drop)
    {
        super(type, level);
        this.boatChest = boatChest;
        this.drop = drop;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand)
    {
        final ItemStack item = player.getItemInHand(hand);
        if (item.getItem() instanceof ChestBlockItem)
        {
            final TFCChestBoat boat = boatChest.get().create(player.level());
            if (boat != null)
            {
                boat.setPos(position());
                boat.setYRot(getYRot());
                boat.setXRot(getXRot());
                boat.setDeltaMovement(getDeltaMovement());
                if (hasCustomName())
                {
                    boat.setCustomName(getCustomName());
                }
                boat.setChestItem(item.split(1));
                level().addFreshEntity(boat);
                discard();
                return InteractionResult.sidedSuccess(level().isClientSide);
            }
        }
        return super.interact(player, hand);
    }

    @Override
    public Item getDropItem()
    {
        return drop.get();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
