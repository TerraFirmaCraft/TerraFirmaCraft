/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;


import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.Helpers;

public final class PacketHandler
{
    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> type(String id)
    {
        return new CustomPacketPayload.Type<T>(Helpers.identifier(id));
    }

    public static void setup(RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar register = event.registrar(ModList.get().getModFileById(TerraFirmaCraft.MOD_ID).versionString());

        // Server -> Client
        register.playToClient(ChunkWatchPacket.TYPE, ChunkWatchPacket.CODEC, onClient(ChunkWatchPacket::handle));
        register.playToClient(CalendarUpdatePacket.TYPE, CalendarUpdatePacket.CODEC, onClient(CalendarUpdatePacket::handle));
        register.playToClient(FoodDataReplacePacket.TYPE, FoodDataReplacePacket.CODEC, onClient(FoodDataReplacePacket::handle));
        register.playToClient(PlayerInfoPacket.TYPE, PlayerInfoPacket.CODEC, onClient(PlayerInfoPacket::handle));
        register.playToClient(ProspectedPacket.TYPE, ProspectedPacket.CODEC, onClient(ProspectedPacket::handle));
        register.playToClient(EffectExpirePacket.TYPE, EffectExpirePacket.CODEC, onClient(EffectExpirePacket::handle));
        register.playToClient(UpdateClimateModelPacket.TYPE, UpdateClimateModelPacket.CODEC, onClient(UpdateClimateModelPacket::handle));
        register.playToClient(RainfallUpdatePacket.TYPE, RainfallUpdatePacket.CODEC, onClient(RainfallUpdatePacket::handle));
        register.playToClient(DataManagerSyncPacket.TYPE, DataManagerSyncPacket.CODEC, (packet, context) -> context.enqueueWork(() -> packet.handle(context.connection().isMemoryConnection())));

        // Client -> Server
        register.playToServer(SwitchInventoryTabPacket.TYPE, SwitchInventoryTabPacket.CODEC, onServer(SwitchInventoryTabPacket::handle));
        register.playToServer(PlaceBlockSpecialPacket.TYPE, PlaceBlockSpecialPacket.CODEC, onServer(PlaceBlockSpecialPacket::handle));
        register.playToServer(CycleChiselModePacket.TYPE, CycleChiselModePacket.CODEC, onServer(CycleChiselModePacket::handle));
        register.playToServer(ScreenButtonPacket.TYPE, ScreenButtonPacket.CODEC, onServer(ScreenButtonPacket::handle));
        register.playToServer(PlayerDrinkPacket.TYPE, PlayerDrinkPacket.CODEC, onServer(PlayerDrinkPacket::handle));
        register.playToServer(RequestClimateModelPacket.TYPE, RequestClimateModelPacket.CODEC, onServer(RequestClimateModelPacket::handle));
        register.playToServer(ScribingTablePacket.TYPE, ScribingTablePacket.CODEC, onServer(ScribingTablePacket::handle));
        register.playToServer(StackFoodPacket.TYPE, StackFoodPacket.CODEC, onServer(StackFoodPacket::handle));
        register.playToServer(OpenFieldGuidePacket.TYPE, OpenFieldGuidePacket.CODEC, onServer(OpenFieldGuidePacket::handle));
        register.playToServer(PetCommandPacket.TYPE, PetCommandPacket.CODEC, onServer(PetCommandPacket::handle));
        register.playToServer(PourFasterPacket.TYPE, PourFasterPacket.CODEC, onServer(PourFasterPacket::handle));
        register.playToServer(SelectAnvilPlanPacket.TYPE, SelectAnvilPlanPacket.CODEC, onServer(SelectAnvilPlanPacket::handle));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onClient(Consumer<T> handler)
    {
        return (payload, context) -> context.enqueueWork(() -> handler.accept(payload));
    }

    private static <T extends CustomPacketPayload> IPayloadHandler<T> onServer(BiConsumer<T, ServerPlayer> handler)
    {
        return (payload, context) -> context.enqueueWork(() -> handler.accept(payload, (ServerPlayer) context.player()));
    }
}