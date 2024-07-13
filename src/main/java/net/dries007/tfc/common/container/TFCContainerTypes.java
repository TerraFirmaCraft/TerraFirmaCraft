/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blockentities.AnvilBlockEntity;
import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.FirepitBlockEntity;
import net.dries007.tfc.common.blockentities.GrillBlockEntity;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.LargeVesselBlockEntity;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blockentities.PowderkegBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.util.data.KnappingType;
import net.dries007.tfc.util.registry.RegistrationHelpers;
import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("RedundantTypeArguments") // For some reason javac dies on the cases where these are explicitly specified, I have no idea why
public final class TFCContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final Id<Container> CALENDAR = register("calendar", (windowId, inv, data) -> Container.create(TFCContainerTypes.CALENDAR.get(), windowId, inv));
    public static final Id<Container> NUTRITION = register("nutrition", (windowId, inv, data) -> Container.create(TFCContainerTypes.NUTRITION.get(), windowId, inv));
    public static final Id<Container> CLIMATE = register("climate", (windowId, inv, data) -> Container.create(TFCContainerTypes.CLIMATE.get(), windowId, inv));
    public static final Id<SaladContainer> SALAD = register("salad", (windowId, inv, data) -> SaladContainer.create(windowId, inv));

    public static final Id<TFCWorkbenchContainer> WORKBENCH = register("workbench", (windowId, inv, data) -> new TFCWorkbenchContainer(windowId, inv));
    public static final Id<ScribingTableContainer> SCRIBING_TABLE = register("scribing_table", ((windowId, inv, data) -> new ScribingTableContainer(inv, windowId)));
    public static final Id<SewingTableContainer> SEWING_TABLE = register("sewing_table", ((windowId, inv, data) -> SewingTableContainer.create(inv, windowId, ContainerLevelAccess.NULL)));

    public static final Id<FirepitContainer> FIREPIT = TFCContainerTypes.<FirepitBlockEntity, FirepitContainer>registerBlock("firepit", TFCBlockEntities.FIREPIT, FirepitContainer::create);
    public static final Id<GrillContainer> GRILL = TFCContainerTypes.<GrillBlockEntity, GrillContainer>registerBlock("grill", TFCBlockEntities.GRILL, GrillContainer::create);
    public static final Id<PotContainer> POT = TFCContainerTypes.<PotBlockEntity, PotContainer>registerBlock("pot", TFCBlockEntities.POT, PotContainer::create);
    public static final Id<CharcoalForgeContainer> CHARCOAL_FORGE = TFCContainerTypes.<CharcoalForgeBlockEntity, CharcoalForgeContainer>registerBlock("charcoal_forge", TFCBlockEntities.CHARCOAL_FORGE, CharcoalForgeContainer::create);
    public static final Id<LogPileContainer> LOG_PILE = TFCContainerTypes.<LogPileBlockEntity, LogPileContainer>registerBlock("log_pile", TFCBlockEntities.LOG_PILE, LogPileContainer::create);
    public static final Id<CrucibleContainer> CRUCIBLE = TFCContainerTypes.<CrucibleBlockEntity, CrucibleContainer>registerBlock("crucible", TFCBlockEntities.CRUCIBLE, CrucibleContainer::create);
    public static final Id<BarrelContainer> BARREL = TFCContainerTypes.<BarrelBlockEntity, BarrelContainer>registerBlock("barrel", TFCBlockEntities.BARREL, BarrelContainer::create);
    public static final Id<PowderkegContainer> POWDERKEG = TFCContainerTypes.<PowderkegBlockEntity, PowderkegContainer>registerBlock("powderkeg", TFCBlockEntities.POWDERKEG, PowderkegContainer::create);
    public static final Id<NestBoxContainer> NEST_BOX = TFCContainerTypes.<NestBoxBlockEntity, NestBoxContainer>registerBlock("nest_box", TFCBlockEntities.NEST_BOX, NestBoxContainer::create);
    public static final Id<LargeVesselContainer> LARGE_VESSEL = TFCContainerTypes.<LargeVesselBlockEntity, LargeVesselContainer>registerBlock("large_vessel", TFCBlockEntities.LARGE_VESSEL, LargeVesselContainer::create);
    public static final Id<AnvilContainer> ANVIL = TFCContainerTypes.<AnvilBlockEntity, AnvilContainer>registerBlock("anvil", TFCBlockEntities.ANVIL, AnvilContainer::create);
    public static final Id<AnvilPlanContainer> ANVIL_PLAN = TFCContainerTypes.<AnvilBlockEntity, AnvilPlanContainer>registerBlock("anvil_plan", TFCBlockEntities.ANVIL, AnvilPlanContainer::create);
    public static final Id<BlastFurnaceContainer> BLAST_FURNACE = TFCContainerTypes.<BlastFurnaceBlockEntity, BlastFurnaceContainer>registerBlock("blast_furnace", TFCBlockEntities.BLAST_FURNACE, BlastFurnaceContainer::create);
    public static final Id<RestrictedChestContainer> CHEST_9x2 = TFCContainerTypes.<RestrictedChestContainer>register("chest_9x2", RestrictedChestContainer::twoRows);
    public static final Id<RestrictedChestContainer> CHEST_9x4 = TFCContainerTypes.<RestrictedChestContainer>register("chest_9x4", RestrictedChestContainer::fourRows);

    public static final Id<SmallVesselInventoryContainer> SMALL_VESSEL_INVENTORY = registerItem("small_vessel_inventory", SmallVesselInventoryContainer::create);
    public static final Id<MoldLikeAlloyContainer> MOLD_LIKE_ALLOY = registerItem("mold_like_alloy", MoldLikeAlloyContainer::create);

    public static final Id<KnappingContainer> KNAPPING = register("knapping", (windowId, playerInventory, buffer) -> {
        final KnappingType knappingType = KnappingType.MANAGER.getOrThrow(buffer.readResourceLocation());
        final ItemStackContainerProvider.Info info = ItemStackContainerProvider.read(buffer, playerInventory);
        return KnappingContainer.create(info.stack(), knappingType, info.hand(), info.slot(), playerInventory, windowId);
    });

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> Id<C> registerBlock(String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return new Id<>(RegistrationHelpers.registerBlockEntityContainer(CONTAINERS, name, type, factory));
    }

    private static <C extends ItemStackContainer> Id<C> registerItem(String name, ItemStackContainer.Factory<C> factory)
    {
        return new Id<>(RegistrationHelpers.registerItemStackContainer(CONTAINERS, name, factory));
    }

    private static <C extends AbstractContainerMenu> Id<C> register(String name, IContainerFactory<C> factory)
    {
        return new Id<>(RegistrationHelpers.registerContainer(CONTAINERS, name, factory));
    }
    
    public record Id<T extends AbstractContainerMenu>(DeferredHolder<MenuType<?>, MenuType<T>> holder)
        implements RegistryHolder<MenuType<?>, MenuType<T>> {}
}