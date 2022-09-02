/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.function.Supplier;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.common.blockentities.*;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("RedundantTypeArguments") // For some reason javac dies on the cases where these are explicitly specified, I have no idea why
public final class TFCContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<MenuType<Container>> CALENDAR = register("calendar", (windowId, inv, data) -> Container.create(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final RegistryObject<MenuType<Container>> NUTRITION = register("nutrition", (windowId, inv, data) -> Container.create(TFCContainerTypes.NUTRITION.get(), windowId, inv));
    public static final RegistryObject<MenuType<Container>> CLIMATE = register("climate", (windowId, inv, data) -> Container.create(TFCContainerTypes.CLIMATE.get(), windowId, inv));
    public static final RegistryObject<MenuType<SaladContainer>> SALAD = register("salad", (windowId, inv, data) -> SaladContainer.create(windowId, inv));

    public static final RegistryObject<MenuType<TFCWorkbenchContainer>> WORKBENCH = register("workbench", (windowId, inv, data) -> new TFCWorkbenchContainer(windowId, inv));
    public static final RegistryObject<MenuType<ScribingTableContainer>> SCRIBING_TABLE = register("scribing_table", ((windowId, inv, data) -> new ScribingTableContainer(inv, windowId)));

    public static final RegistryObject<MenuType<FirepitContainer>> FIREPIT = TFCContainerTypes.<FirepitBlockEntity, FirepitContainer>registerBlock("firepit", TFCBlockEntities.FIREPIT, FirepitContainer::create);
    public static final RegistryObject<MenuType<GrillContainer>> GRILL = TFCContainerTypes.<GrillBlockEntity, GrillContainer>registerBlock("grill", TFCBlockEntities.GRILL, GrillContainer::create);
    public static final RegistryObject<MenuType<PotContainer>> POT = TFCContainerTypes.<PotBlockEntity, PotContainer>registerBlock("pot", TFCBlockEntities.POT, PotContainer::create);
    public static final RegistryObject<MenuType<CharcoalForgeContainer>> CHARCOAL_FORGE = TFCContainerTypes.<CharcoalForgeBlockEntity, CharcoalForgeContainer>registerBlock("charcoal_forge", TFCBlockEntities.CHARCOAL_FORGE, CharcoalForgeContainer::create);
    public static final RegistryObject<MenuType<LogPileContainer>> LOG_PILE = TFCContainerTypes.<LogPileBlockEntity, LogPileContainer>registerBlock("log_pile", TFCBlockEntities.LOG_PILE, LogPileContainer::create);
    public static final RegistryObject<MenuType<CrucibleContainer>> CRUCIBLE = TFCContainerTypes.<CrucibleBlockEntity, CrucibleContainer>registerBlock("crucible", TFCBlockEntities.CRUCIBLE, CrucibleContainer::create);
    public static final RegistryObject<MenuType<BarrelContainer>> BARREL = TFCContainerTypes.<BarrelBlockEntity, BarrelContainer>registerBlock("barrel", TFCBlockEntities.BARREL, BarrelContainer::create);
    public static final RegistryObject<MenuType<PowderkegContainer>> POWDERKEG = TFCContainerTypes.<PowderkegBlockEntity, PowderkegContainer>registerBlock("powderkeg", TFCBlockEntities.POWDERKEG, PowderkegContainer::create);
    public static final RegistryObject<MenuType<NestBoxContainer>> NEST_BOX = TFCContainerTypes.<NestBoxBlockEntity, NestBoxContainer>registerBlock("nest_box", TFCBlockEntities.NEST_BOX, NestBoxContainer::create);
    public static final RegistryObject<MenuType<LargeVesselContainer>> LARGE_VESSEL = TFCContainerTypes.<LargeVesselBlockEntity, LargeVesselContainer>registerBlock("large_vessel", TFCBlockEntities.LARGE_VESSEL, LargeVesselContainer::create);
    public static final RegistryObject<MenuType<AnvilContainer>> ANVIL = TFCContainerTypes.<AnvilBlockEntity, AnvilContainer>registerBlock("anvil", TFCBlockEntities.ANVIL, AnvilContainer::create);
    public static final RegistryObject<MenuType<AnvilPlanContainer>> ANVIL_PLAN = TFCContainerTypes.<AnvilBlockEntity, AnvilPlanContainer>registerBlock("anvil_plan", TFCBlockEntities.ANVIL, AnvilPlanContainer::create);
    public static final RegistryObject<MenuType<BlastFurnaceContainer>> BLAST_FURNACE = TFCContainerTypes.<BlastFurnaceBlockEntity, BlastFurnaceContainer>registerBlock("blast_furnace", TFCBlockEntities.BLAST_FURNACE, BlastFurnaceContainer::create);
    public static final RegistryObject<MenuType<RestrictedChestContainer>> CHEST_9x2 = TFCContainerTypes.<RestrictedChestContainer>register("chest_9x2", RestrictedChestContainer::twoRows);
    public static final RegistryObject<MenuType<RestrictedChestContainer>> CHEST_9x4 = TFCContainerTypes.<RestrictedChestContainer>register("chest_9x4", RestrictedChestContainer::fourRows);

    public static final RegistryObject<MenuType<KnappingContainer>> CLAY_KNAPPING = registerItem("clay_knapping", KnappingContainer::createClay);
    public static final RegistryObject<MenuType<KnappingContainer>> FIRE_CLAY_KNAPPING = registerItem("fire_clay_knapping", KnappingContainer::createFireClay);
    public static final RegistryObject<MenuType<LeatherKnappingContainer>> LEATHER_KNAPPING = registerItem("leather_knapping", KnappingContainer::createLeather);
    public static final RegistryObject<MenuType<KnappingContainer>> ROCK_KNAPPING = registerItem("rock_knapping", KnappingContainer::createRock);
    public static final RegistryObject<MenuType<SmallVesselInventoryContainer>> SMALL_VESSEL_INVENTORY = registerItem("small_vessel_inventory", SmallVesselInventoryContainer::create);
    public static final RegistryObject<MenuType<MoldLikeAlloyContainer>> MOLD_LIKE_ALLOY = registerItem("mold_like_alloy", MoldLikeAlloyContainer::create);

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> RegistryObject<MenuType<C>> registerBlock(String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return RegistrationHelpers.registerBlockEntityContainer(CONTAINERS, name, type, factory);
    }

    private static <C extends ItemStackContainer> RegistryObject<MenuType<C>> registerItem(String name, ItemStackContainer.Factory<C> factory)
    {
        return RegistrationHelpers.registerItemStackContainer(CONTAINERS, name, factory);
    }

    private static <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, IContainerFactory<C> factory)
    {
        return RegistrationHelpers.registerContainer(CONTAINERS, name, factory);
    }
}