/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.util.Helpers;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record SwitchInventoryTabPacket(Tab tab) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SwitchInventoryTabPacket> TYPE = PacketHandler.type("switch_inventory_tab");
    public static final StreamCodec<ByteBuf, SwitchInventoryTabPacket> STREAM = Tab.STREAM.map(SwitchInventoryTabPacket::new, c -> c.tab);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            player.doCloseContainer();
            switch (tab)
            {
                case INVENTORY -> player.containerMenu = player.inventoryMenu;
                case CALENDAR -> Helpers.openScreen(player, TFCContainerProviders.CALENDAR);
                case NUTRITION -> Helpers.openScreen(player, TFCContainerProviders.NUTRITION);
                case CLIMATE -> Helpers.openScreen(player, TFCContainerProviders.CLIMATE);
                case BOOK -> PatchouliIntegration.openGui(player);
            }
        }
    }

    public enum Tab
    {
        INVENTORY, CALENDAR, NUTRITION, CLIMATE, BOOK;

        public static final Tab[] VALUES = values();
        public static final StreamCodec<ByteBuf, Tab> STREAM = ByteBufCodecs.BYTE.map(c -> VALUES[c], c -> (byte) c.ordinal());
    }
}