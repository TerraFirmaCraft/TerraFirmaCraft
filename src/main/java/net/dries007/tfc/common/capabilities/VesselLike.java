package net.dries007.tfc.common.capabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandlerModifiable;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.container.ISlotCallback;

public interface VesselLike extends MoldLike, IItemHandlerModifiable, IFluidHandlerItem, IHeat, ISlotCallback
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

    @Override
        // Need to override to resolve default method conflict
    boolean isItemValid(int slot, @Nonnull ItemStack stack);

    enum Mode
    {
        INVENTORY,
        MOLTEN_ALLOY,
        SOLID_ALLOY
    }
}
