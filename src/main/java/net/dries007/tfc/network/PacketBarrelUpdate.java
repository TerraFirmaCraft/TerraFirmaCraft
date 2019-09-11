/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.network;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import io.netty.buffer.ByteBuf;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

public class PacketBarrelUpdate implements IMessage
{
    private BlockPos pos;
    private long calendarTick;
    private ResourceLocation recipeName;
    private boolean sealed;

    @SuppressWarnings("unused")
    @Deprecated
    public PacketBarrelUpdate() {}

    public PacketBarrelUpdate(@Nonnull TEBarrel tile, @Nullable BarrelRecipe currentRecipe, long calendarTick, boolean sealed)
    {
        this.pos = tile.getPos();
        this.calendarTick = calendarTick;
        this.recipeName = currentRecipe != null ? currentRecipe.getRegistryName() : null;
        this.sealed = sealed;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        pos = BlockPos.fromLong(buf.readLong());
        calendarTick = buf.readLong();
        recipeName = Helpers.readResourceLocation(buf);
        sealed = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeLong(pos.toLong());
        buf.writeLong(calendarTick);
        Helpers.writeResourceLocation(buf, recipeName);
        buf.writeBoolean(sealed);
    }

    public static final class Handler implements IMessageHandler<PacketBarrelUpdate, IMessage>
    {
        @Override
        public IMessage onMessage(PacketBarrelUpdate message, MessageContext ctx)
        {
            EntityPlayer player = TerraFirmaCraft.getProxy().getPlayer(ctx);
            if (player != null)
            {
                World world = player.getEntityWorld();
                TerraFirmaCraft.getProxy().getThreadListener(ctx).addScheduledTask(() -> {
                    TEBarrel te = Helpers.getTE(world, message.pos, TEBarrel.class);
                    if (te != null)
                    {
                        BarrelRecipe recipe = message.recipeName == null ? null : TFCRegistries.BARREL.getValue(message.recipeName);
                        te.onReceivePacket(recipe, message.calendarTick, message.sealed);
                    }
                });
            }
            return null;
        }
    }
}
