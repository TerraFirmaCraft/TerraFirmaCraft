/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.util.climate.ClimateModels;
import net.dries007.tfc.util.tracker.WorldTracker;

public record UpdateClimateModelPacket(ClimateModel model) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<UpdateClimateModelPacket> TYPE = PacketHandler.type("update_climate_model");
    public static final StreamCodec<ByteBuf, UpdateClimateModelPacket> CODEC = ResourceLocation.STREAM_CODEC.<ClimateModel>dispatch(
        c -> ClimateModels.REGISTRY.getKey(c.type()),
        id -> ClimateModels.REGISTRY.getOptional(id).orElseGet(ClimateModels.BIOME_BASED).codec()
    ).map(UpdateClimateModelPacket::new, c -> c.model);

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    void handle()
    {
        final Level level = ClientHelpers.getLevel();
        if (level != null)
        {
            WorldTracker.get(level).setClimateModel(model);
        }
    }
}
