/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.IForgeableHandler;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.objects.recipes.anvil.AnvilRecipe;

@ParametersAreNonnullByDefault
public class TEAnvilTFC extends TEInventory
{
    public static final int WORK_MAX = 150;

    public static final int SLOT_INPUT_1 = 0;
    public static final int SLOT_INPUT_2 = 1;
    public static final int SLOT_HAMMER = 2;
    public static final int SLOT_FLUX = 3;
    public static final int SLOT_OUTPUT = 4;
    public static final int SLOT_DISPLAY = 5;

    public TEAnvilTFC()
    {
        // todo: lots of stuff
        super(1);
    }

    public AnvilRecipe getRecipe()
    {
        return null;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_INPUT_1:
            case SLOT_INPUT_2:
                return stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null) instanceof IForgeableHandler;
            default:
                return false;
        }
    }


}
