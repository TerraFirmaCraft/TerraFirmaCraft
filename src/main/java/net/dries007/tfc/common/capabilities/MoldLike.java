package net.dries007.tfc.common.capabilities;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;

// Extends IInventory because we need to be able to use it as a recipe query for casting.
public interface MoldLike extends IFluidHandlerItem, IHeat, EmptyInventory
{
    @Nullable
    static MoldLike get(ItemStack stack)
    {
        return stack.getCapability(HeatCapability.CAPABILITY)
            .resolve()
            .map(t -> t instanceof MoldLike v ? v : null)
            .orElse(null);
    }

    /**
     * @return {@code true} if the fluid contents of the mold is molten.
     */
    boolean isMolten();
}
