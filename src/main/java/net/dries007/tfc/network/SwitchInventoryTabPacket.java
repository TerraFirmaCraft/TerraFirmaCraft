/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import net.dries007.tfc.common.container.TFCContainerProviders;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.util.Helpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public record SwitchInventoryTabPacket(Type type)
{
    SwitchInventoryTabPacket(FriendlyByteBuf buffer)
    {
        this(Type.VALUES[buffer.readByte()]);
    }

    void encode(FriendlyByteBuf buffer)
    {
        buffer.writeByte(type.ordinal());
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null)
        {
            player.doCloseContainer();
            switch (type)
            {
                case INVENTORY -> player.containerMenu = player.inventoryMenu;
                case CALENDAR -> Helpers.openScreen(player, TFCContainerProviders.CALENDAR);
                case NUTRITION -> Helpers.openScreen(player, TFCContainerProviders.NUTRITION);
                case CLIMATE -> Helpers.openScreen(player, TFCContainerProviders.CLIMATE);
                case BOOK -> PatchouliIntegration.openGui(player);
            }
        }
    }

    public enum Type
    {
        INVENTORY, CALENDAR, NUTRITION, CLIMATE, BOOK;

        private static final Type[] VALUES = values();
    }
}