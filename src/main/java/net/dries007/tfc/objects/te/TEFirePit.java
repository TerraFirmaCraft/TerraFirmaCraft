/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.blocks.BlockFirePit.LIT;

@ParametersAreNonnullByDefault
public class TEFirePit extends TESidedInventory implements ITickable
{
    // To avoid "magic numbers"
    public static final int SLOT_FUEL_INPUT = 3;
    public static final int SLOT_ITEM_INPUT = 4;

    private static boolean isStackFuel(ItemStack stack)
    {
        return Helpers.doesStackMatchOre(stack, "logWood");
    }

    private static boolean isStackCookable(ItemStack stack)
    {
        return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null); // todo
    }

    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private float burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel

    public TEFirePit()
    {
        super(7);
        // Slot 0 - 3 = fuel slots with 3 being input, 4 = normal input slot, 5 and 6 are output slots 1 + 2

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
    }

    @Override
    public void update()
    {
        // do timer things
        IBlockState state = world.getBlockState(pos);
        boolean burning = state.getValue(LIT);
        if (burning)
        {
            if (burnTicks > 0)
            {
                burnTicks--;
                if (burnTicks == 0)
                {
                }

            }
        }
        // This is here to avoid duplication glitches
        if (requiresSlotUpdate)
            cascadeFuelSlots();
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        this.markDirty();
        requiresSlotUpdate = true;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return slot <= 4 ? 1 : 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        switch (slot)
        {
            case SLOT_FUEL_INPUT:
                return isStackFuel(stack); // check if it is a log
            case SLOT_ITEM_INPUT:
                return isStackCookable(stack); // check if it has a fire pit recipe
            default: // Other fuel slots + output slots
                return false;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getFloat("burnTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setFloat("temperature", temperature);
        nbt.setFloat("burnTicks", burnTicks);
        nbt.setFloat("burnTemperature", burnTemperature);
        return super.writeToNBT(nbt);
    }

    private void cascadeFuelSlots()
    {
        // This will cascade all fuel down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i < 4; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest avail slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
        requiresSlotUpdate = false;
    }

}
