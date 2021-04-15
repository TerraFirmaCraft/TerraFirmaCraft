/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCContainerTypes
{
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<ContainerType<SimpleContainer>> CALENDAR = register("calendar", (windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final RegistryObject<ContainerType<SimpleContainer>> NUTRITION = register("nutrition", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, inv)));
    public static final RegistryObject<ContainerType<SimpleContainer>> CLIMATE = register("climate", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, inv)));
    public static final RegistryObject<ContainerType<TFCWorkbenchContainer>> WORKBENCH = register("workbench", (((windowId, inv, data) -> new TFCWorkbenchContainer(windowId, inv))));

    private static <C extends Container> RegistryObject<ContainerType<C>> register(String name, IContainerFactory<C> factory)
    {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }
}