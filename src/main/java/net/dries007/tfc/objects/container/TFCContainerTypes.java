/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

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
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<ContainerType<SimpleContainer>> CALENDAR = register("calendar", (windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final RegistryObject<ContainerType<SimpleContainer>> NUTRITION = register("nutrition", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, inv)));

    private static <C extends Container> RegistryObject<ContainerType<C>> register(String name, IContainerFactory<C> factory)
    {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }
}
