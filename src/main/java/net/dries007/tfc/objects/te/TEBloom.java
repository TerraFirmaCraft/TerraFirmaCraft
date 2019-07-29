/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.forge.CapabilityForgeable;
import net.dries007.tfc.api.capability.forge.IForgeable;
import net.dries007.tfc.api.capability.forge.IForgeableMeasurable;
import net.dries007.tfc.objects.items.ItemsTFC;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TEBloom extends TEInventory
{
    public TEBloom()
    {
        super(1);

        setMetalAmount(100);
    }

    public void setMetalAmount(int metalAmount)
    {
        ItemStack stack = new ItemStack(ItemsTFC.UNREFINED_BLOOM);
        inventory.setStackInSlot(0, stack);
        IForgeable cap = stack.getCapability(CapabilityForgeable.FORGEABLE_CAPABILITY, null);
        if (cap instanceof IForgeableMeasurable)
        {
            ((IForgeableMeasurable) cap).setMetalAmount(metalAmount);
        }
    }
}
