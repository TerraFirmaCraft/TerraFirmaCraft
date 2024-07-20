/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;

/**
 * Interface for capability instances which behave like vessels. Meaning:
 * - They have both fluid, and item inventories.
 * - They can be heated, in both states, and can contain either items, or alloys.
 */
public interface VesselLike extends MoldLike, IItemHandlerModifiable, IFluidHandlerItem, NonEmptyInput, ISlotCallback
{
    @Nullable
    static VesselLike get(ItemStack stack)
    {
        return HeatCapability.get(stack) instanceof VesselLike v ? v : null;
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
