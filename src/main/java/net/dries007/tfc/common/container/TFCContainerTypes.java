/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fmllegacy.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.tileentity.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public final class TFCContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<MenuType<SimpleContainer>> CALENDAR = register("calendar", (windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final RegistryObject<MenuType<SimpleContainer>> NUTRITION = register("nutrition", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.NUTRITION.get(), windowId, inv)));
    public static final RegistryObject<MenuType<SimpleContainer>> CLIMATE = register("climate", ((windowId, inv, data) -> new SimpleContainer(TFCContainerTypes.CLIMATE.get(), windowId, inv)));
    public static final RegistryObject<MenuType<FirepitContainer>> FIREPIT = register("firepit", FirepitTileEntity.class, FirepitContainer::new);
    public static final RegistryObject<MenuType<GrillContainer>> GRILL = register("grill", GrillTileEntity.class, GrillContainer::new);
    public static final RegistryObject<MenuType<PotContainer>> POT = register("pot", PotTileEntity.class, PotContainer::new);
    public static final RegistryObject<MenuType<CharcoalForgeContainer>> CHARCOAL_FORGE = register("charcoal_forge", CharcoalForgeTileEntity.class, CharcoalForgeContainer::new);
    public static final RegistryObject<MenuType<LogPileContainer>> LOG_PILE = register("log_pile", LogPileTileEntity.class, LogPileContainer::new);
    public static final RegistryObject<MenuType<TFCWorkbenchContainer>> WORKBENCH = register("workbench", (((windowId, inv, data) -> new TFCWorkbenchContainer(windowId, inv))));
    public static final RegistryObject<MenuType<KnappingContainer>> CLAY_KNAPPING = register("clay_knapping", ((((windowId, inv, data) -> new KnappingContainer(TFCContainerTypes.CLAY_KNAPPING.get(), TFCRecipeTypes.CLAY_KNAPPING, windowId, inv, 5, true, true, TFCSounds.KNAP_CLAY.get())))));
    public static final RegistryObject<MenuType<KnappingContainer>> FIRE_CLAY_KNAPPING = register("fire_clay_knapping", ((((windowId, inv, data) -> new KnappingContainer(TFCContainerTypes.FIRE_CLAY_KNAPPING.get(), TFCRecipeTypes.FIRE_CLAY_KNAPPING, windowId, inv, 5, true, true, TFCSounds.KNAP_CLAY.get())))));
    public static final RegistryObject<MenuType<LeatherKnappingContainer>> LEATHER_KNAPPING = register("leather_knapping", ((((windowId, inv, data) -> new LeatherKnappingContainer(TFCContainerTypes.LEATHER_KNAPPING.get(), TFCRecipeTypes.LEATHER_KNAPPING, windowId, inv, 1, false, false, TFCSounds.KNAP_LEATHER.get())))));
    public static final RegistryObject<MenuType<KnappingContainer>> ROCK_KNAPPING = register("rock_knapping", ((((windowId, inv, data) -> new KnappingContainer(TFCContainerTypes.ROCK_KNAPPING.get(), TFCRecipeTypes.ROCK_KNAPPING, windowId, inv, 1, false, false, TFCSounds.KNAP_STONE.get())))));


    @SuppressWarnings("SameParameterValue")
    private static <T extends InventoryTileEntity<?>, C extends TileEntityContainer<T>> RegistryObject<MenuType<C>> register(String name, Class<T> tileClass, TileEntityContainer.IFactory<T, C> factory)
    {
        return register(name, (windowId, playerInventory, packetBuffer) -> {
            Level world = playerInventory.player.level;
            BlockPos pos = packetBuffer.readBlockPos();
            return factory.create(Helpers.getTileEntityOrThrow(world, pos, tileClass), playerInventory, windowId);
        });
    }

    private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, IContainerFactory<C> factory)
    {
        return CONTAINERS.register(name, () -> IForgeContainerType.create(factory));
    }
}