/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.common.tileentity.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCContainerTypes
{
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<ContainerType<SimpleContainer>> CALENDAR = register("calendar", (windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final RegistryObject<ContainerType<SimpleContainer>> NUTRITION = register("nutrition", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, inv)));
    public static final RegistryObject<ContainerType<SimpleContainer>> CLIMATE = register("climate", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, inv)));
    public static final RegistryObject<ContainerType<FirepitContainer>> FIREPIT = register("firepit", FirepitTileEntity.class, FirepitContainer::new);
    public static final RegistryObject<ContainerType<GrillContainer>> GRILL = register("grill", GrillTileEntity.class, GrillContainer::new);
    public static final RegistryObject<ContainerType<PotContainer>> POT = register("pot", PotTileEntity.class, PotContainer::new);
    public static final RegistryObject<ContainerType<LogPileContainer>> LOG_PILE = register("log_pile", LogPileTileEntity.class, LogPileContainer::new);


    @SuppressWarnings("SameParameterValue")
    private static <T extends InventoryTileEntity, C extends TileEntityContainer<T>> RegistryObject<ContainerType<C>> register(String name, Class<T> tileClass, TileEntityContainer.IFactory<T, C> factory)
    {
        return register(name, (windowId, playerInventory, packetBuffer) -> {
            World world = playerInventory.player.level;
            BlockPos pos = packetBuffer.readBlockPos();
            return factory.create(Helpers.getTileEntityOrThrow(world, pos, tileClass), playerInventory, windowId);
        });
    }

    private static <C extends Container> RegistryObject<ContainerType<C>> register(String name, IContainerFactory<C> factory)
    {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }
}