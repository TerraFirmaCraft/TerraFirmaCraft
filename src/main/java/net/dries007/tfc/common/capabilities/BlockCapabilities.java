/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.BarrelBlockEntity;
import net.dries007.tfc.common.blockentities.BlastFurnaceBlockEntity;
import net.dries007.tfc.common.blockentities.CrucibleBlockEntity;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.component.heat.IHeatConsumer;
import net.dries007.tfc.util.Helpers;

public final class BlockCapabilities
{
    public static final BlockCapability<IItemHandler, @Nullable Direction> ITEM = Capabilities.ItemHandler.BLOCK;
    public static final BlockCapability<IFluidHandler, @Nullable Direction> FLUID = Capabilities.FluidHandler.BLOCK;

    /**
     * A simple capability exposed on blocks which allow them to receive heat.
     */
    public static final BlockCapability<IHeatConsumer, @Nullable Direction> HEAT = BlockCapability.create(Helpers.identifier("heat"), IHeatConsumer.class, Direction.class);

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(ITEM, TFCBlockEntities.ANVIL.get(), (object, context) -> object.getInventory());
        registerInventory(event, TFCBlockEntities.BARREL);
        event.registerBlockEntity(FLUID, TFCBlockEntities.BARREL.get(), BarrelBlockEntity::getSidedFluidInventory);
        registerInventory(event, TFCBlockEntities.BLAST_FURNACE);
        event.registerBlockEntity(FLUID, TFCBlockEntities.BLAST_FURNACE.get(), BlastFurnaceBlockEntity::getSidedFluidInventory);
        registerInventory(event, TFCBlockEntities.BOWL);
        registerInventory(event, TFCBlockEntities.CHARCOAL_FORGE);
        registerInventory(event, TFCBlockEntities.COMPOSTER);
        registerInventory(event, TFCBlockEntities.CRUCIBLE);
        event.registerBlockEntity(FLUID, TFCBlockEntities.CRUCIBLE.get(), CrucibleBlockEntity::getSidedFluidInventory);
        event.registerBlockEntity(HEAT, TFCBlockEntities.CRUCIBLE.get(), (object, context) -> object.getInventory());
        registerInventory(event, TFCBlockEntities.FIREPIT);
        registerInventory(event, TFCBlockEntities.GRILL);
        registerInventory(event, TFCBlockEntities.JARS);
        registerInventory(event, TFCBlockEntities.LARGE_VESSEL);
        registerInventory(event, TFCBlockEntities.LOOM);
        registerInventory(event, TFCBlockEntities.NEST_BOX);
        registerInventory(event, TFCBlockEntities.POT);
        event.registerBlockEntity(FLUID, TFCBlockEntities.POT.get(), PotBlockEntity::getSidedFluidInventory);
        registerInventory(event, TFCBlockEntities.POWDERKEG);
        registerInventory(event, TFCBlockEntities.QUERN);
    }

    private static void registerInventory(RegisterCapabilitiesEvent event, Supplier<? extends BlockEntityType<? extends InventoryBlockEntity<?>>> type)
    {
        event.registerBlockEntity(ITEM, type.get(), InventoryBlockEntity::getSidedInventory);
    }
}
