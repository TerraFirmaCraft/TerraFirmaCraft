/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.network;


import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.mutable.MutableInt;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.util.*;
import net.dries007.tfc.util.climate.ClimateRange;

public final class PacketHandler
{
    private static final String VERSION = TerraFirmaCraft.MOD_VERSION;
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Helpers.identifier("network"), () -> VERSION, VERSION::equals, VERSION::equals);
    private static final MutableInt ID = new MutableInt(0);

    public static void send(PacketDistributor.PacketTarget target, Object message)
    {
        CHANNEL.send(target, message);
    }

    public static void init()
    {
        // Server -> Client
        register(ChunkWatchPacket.class, ChunkWatchPacket::encode, ChunkWatchPacket::new, ChunkWatchPacket::handle);
        register(ChunkUnwatchPacket.class, ChunkUnwatchPacket::encode, ChunkUnwatchPacket::new, ChunkUnwatchPacket::handle);
        register(CalendarUpdatePacket.class, CalendarUpdatePacket::encode, CalendarUpdatePacket::new, CalendarUpdatePacket::handle);
        register(FoodDataReplacePacket.class, FoodDataReplacePacket::new, FoodDataReplacePacket::handle);
        register(FoodDataUpdatePacket.class, FoodDataUpdatePacket::encode, FoodDataUpdatePacket::new, FoodDataUpdatePacket::handle);
        register(PlayerDataUpdatePacket.class, PlayerDataUpdatePacket::encode, PlayerDataUpdatePacket::new, PlayerDataUpdatePacket::handle);
        register(ProspectedPacket.class, ProspectedPacket::encode, ProspectedPacket::new, ProspectedPacket::handle);
        register(EffectExpirePacket.class, EffectExpirePacket::encode, EffectExpirePacket::new, EffectExpirePacket::handle);
        register(UpdateClimateModelPacket.class, UpdateClimateModelPacket::encode, UpdateClimateModelPacket::decode, UpdateClimateModelPacket::handle);
        register(RainfallUpdatePacket.class, RainfallUpdatePacket::encode, RainfallUpdatePacket::new, RainfallUpdatePacket::handle);

        registerDataManager(Metal.Packet.class, Metal.MANAGER);
        registerDataManager(Fuel.Packet.class, Fuel.MANAGER);
        registerDataManager(Fertilizer.Packet.class, Fertilizer.MANAGER);
        registerDataManager(ItemDamageResistance.Packet.class, ItemDamageResistance.MANAGER);
        registerDataManager(FoodCapability.Packet.class, FoodCapability.MANAGER);
        registerDataManager(HeatCapability.Packet.class, HeatCapability.MANAGER);
        registerDataManager(ItemSizeManager.Packet.class, ItemSizeManager.MANAGER);
        registerDataManager(ClimateRange.Packet.class, ClimateRange.MANAGER);
        registerDataManager(Drinkable.Packet.class, Drinkable.MANAGER);
        registerDataManager(LampFuel.Packet.class, LampFuel.MANAGER);

        // Client -> Server
        register(SwitchInventoryTabPacket.class, SwitchInventoryTabPacket::encode, SwitchInventoryTabPacket::new, SwitchInventoryTabPacket::handle);
        register(PlaceBlockSpecialPacket.class, PlaceBlockSpecialPacket::new, PlaceBlockSpecialPacket::handle);
        register(CycleChiselModePacket.class, CycleChiselModePacket::new, CycleChiselModePacket::handle);
        register(ScreenButtonPacket.class, ScreenButtonPacket::encode, ScreenButtonPacket::new, ScreenButtonPacket::handle);
        register(PlayerDrinkPacket.class, PlayerDrinkPacket::new, PlayerDrinkPacket::handle);
        register(RequestClimateModelPacket.class, RequestClimateModelPacket::new, RequestClimateModelPacket::handle);
        register(ScribingTablePacket.class, ScribingTablePacket::encode, ScribingTablePacket::new, ScribingTablePacket::handle);
        register(StackFoodPacket.class, StackFoodPacket::encode, StackFoodPacket::new, StackFoodPacket::handle);
        register(OpenFieldGuidePacket.class, OpenFieldGuidePacket::encode, OpenFieldGuidePacket::new, OpenFieldGuidePacket::handle);
        register(PetCommandPacket.class, PetCommandPacket::encode, PetCommandPacket::new, PetCommandPacket::handle);
        register(PourFasterPacket.class, PourFasterPacket::encode, PourFasterPacket::new, PourFasterPacket::handle);
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataManagerSyncPacket<E>, E> void registerDataManager(Class<T> cls, DataManager<E> manager, SimpleChannel channel, int id)
    {
        channel.registerMessage(id, cls,
            (packet, buffer) -> packet.encode(manager, buffer),
            buffer -> {
                final T packet = (T) manager.createEmptyPacket();
                packet.decode(manager, buffer);
                return packet;
            },
            (packet, context) -> {
                context.get().setPacketHandled(true);
                context.get().enqueueWork(() -> packet.handle(context.get(), manager));
            });
    }

    private static <T extends DataManagerSyncPacket<E>, E> void registerDataManager(Class<T> cls, DataManager<E> manager)
    {
        registerDataManager(cls, manager, CHANNEL, ID.getAndIncrement());
    }

    private static <T> void register(Class<T> cls, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, encoder, decoder, (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }

    private static <T> void register(Class<T> cls, Supplier<T> factory, BiConsumer<T, NetworkEvent.Context> handler)
    {
        CHANNEL.registerMessage(ID.getAndIncrement(), cls, (packet, buffer) -> {}, buffer -> factory.get(), (packet, context) -> {
            context.get().setPacketHandled(true);
            handler.accept(packet, context.get());
        });
    }
}