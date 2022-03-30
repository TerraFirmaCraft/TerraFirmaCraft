/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;

/**
 * Interface for capability instances which behave like vessels. Meaning:
 * - They have both fluid, and item inventories.
 * - They can be heated, in both states, and can contain either items, or alloys.
 */
public interface VesselLike extends MoldLike, IItemHandlerModifiable, IFluidHandlerItem, IHeat, EmptyInventory, ISlotCallback
{
    @Nullable
    static VesselLike get(ItemStack stack)
    {
        return stack.getCapability(HeatCapability.CAPABILITY)
            .resolve()
            .map(t -> t instanceof VesselLike v ? v : null)
            .orElse(null);
    }

    /**
     * @return the current mode of the vessel like container.
     */
    Mode mode();

    @Override
    default boolean isMolten()
    {
        return mode() == Mode.MOLTEN_ALLOY;
    }

    /**
     * Need to override to resolve default method conflict
     */
    @Override
    boolean isItemValid(int slot, @NotNull ItemStack stack);

    enum Mode
    {
        INVENTORY,
        MOLTEN_ALLOY,
        SOLID_ALLOY
    }
}
