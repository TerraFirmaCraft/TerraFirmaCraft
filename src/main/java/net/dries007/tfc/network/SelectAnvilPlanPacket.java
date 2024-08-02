/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import java.util.Objects;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.container.AnvilContainer;
import net.dries007.tfc.common.recipes.AnvilRecipe;

public record SelectAnvilPlanPacket(ResourceLocation recipeId) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<SelectAnvilPlanPacket> TYPE = PacketHandler.type("select_anvil_plan");
    public static final StreamCodec<ByteBuf, SelectAnvilPlanPacket> CODEC = ResourceLocation.STREAM_CODEC.map(SelectAnvilPlanPacket::new, c -> c.recipeId);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle(@Nullable ServerPlayer player)
    {
        if (player != null && player.containerMenu instanceof AnvilContainer anvilContainer)
        {
            anvilContainer.getBlockEntity().chooseRecipe(recipeId);
        }
    }
}
