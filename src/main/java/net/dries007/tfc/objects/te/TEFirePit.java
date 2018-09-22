/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.objects.blocks.wood.BlockLogTFC;
import net.dries007.tfc.objects.recipes.firepit.FirePitRecipe;
import net.dries007.tfc.objects.recipes.firepit.FirePitRecipeManager;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.blocks.BlockFirePit.LIT;

@ParametersAreNonnullByDefault
public class TEFirePit extends TESidedInventory implements ITickable
{
    // To avoid "magic numbers"
    public static final int SLOT_FUEL_CONSUME = 0;
    public static final int SLOT_FUEL_INPUT = 3;
    public static final int SLOT_ITEM_INPUT = 4;
    public static final int SLOT_OUTPUT_1 = 5;
    public static final int SLOT_OUTPUT_2 = 6;

    // todo: adjust this to change how fast firepit heats up items (item_heating_mod) or how fast it heats up (temperature_modifier)
    private static final float TEMPERATURE_MODIFIER = 1f;
    private static final float ITEM_HEATING_MODIFIER = 2f;

    public static int getFuelAmount(ItemStack stack)
    {
        // todo: make this a proper registry or lookup somewhere
        if (stack.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockLogTFC)
            {
                return ((BlockLogTFC) block).getWood().getBurnTicks();
            }
        }
        return 0;
    }

    public static float getFuelTemperature(ItemStack stack)
    {
        // todo: make this a proper registry or lookup somewhere
        if (stack.getItem() instanceof ItemBlock)
        {
            Block block = ((ItemBlock) stack.getItem()).getBlock();
            if (block instanceof BlockLogTFC)
            {
                return ((BlockLogTFC) block).getWood().getBurnTemp();
            }
        }
        return 0f;
    }

    private static boolean isStackFuel(ItemStack stack)
    {
        return getFuelAmount(stack) != 0;
    }

    private static boolean isStackCookable(ItemStack stack)
    {
        return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
    }

    private boolean requiresSlotUpdate = false;
    private float temperature; // Current Temperature
    private float burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int pickupTimer;

    public TEFirePit()
    {
        super(7);
        // Slot 0 - 3 = fuel slots with 3 being input, 4 = normal input slot, 5 and 6 are output slots 1 + 2

        temperature = 0;
        burnTemperature = 0;
        burnTicks = 0;
        pickupTimer = 0;
    }

    @Override
    public void update()
    {
        // do timer things
        if (world.isRemote) return;
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT))
        {
            // Update fuel
            if (burnTicks > 0)
            {
                burnTicks--;
            }
            if (burnTicks == 0)
            {
                // Consume fuel
                ItemStack stack = inventory.getStackInSlot(SLOT_FUEL_CONSUME);
                if (stack.isEmpty())
                {
                    world.setBlockState(pos, state.withProperty(LIT, false));
                    burnTicks = 0;
                    burnTemperature = 0;
                }
                else
                {
                    inventory.setStackInSlot(SLOT_FUEL_CONSUME, ItemStack.EMPTY);
                    burnTicks += getFuelAmount(stack);
                    burnTemperature = getFuelTemperature(stack);
                }
            }
        }

        // Always update temperature / cooking, until the fire pit is not hot anymore
        if (temperature > 0 || burnTemperature > 0)
        {
            // Update temperature
            if (temperature < burnTemperature)
            {
                temperature += TEMPERATURE_MODIFIER;
            }
            else if (temperature > burnTemperature)
            {
                temperature -= TEMPERATURE_MODIFIER;
            }

            // Update items in slots
            // Loop through input + 2 output slots
            for (int i = SLOT_ITEM_INPUT; i < SLOT_ITEM_INPUT + 3; i++)
            {
                ItemStack stack = inventory.getStackInSlot(i);
                IItemHeat cap = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (cap != null)
                {
                    float itemTemp = cap.getTemperature();
                    if (temperature > itemTemp)
                    {
                        CapabilityItemHeat.addTemp(cap, ITEM_HEATING_MODIFIER);
                        stack.setTagCompound(cap.serializeNBT());
                    }

                    // For now only check the input slot
                    if (cap.isMolten() && i == SLOT_ITEM_INPUT)
                    {
                        // todo: This is the bit that needs to be more advanced than this
                        FirePitRecipe recipe = FirePitRecipeManager.get(stack);
                        if (recipe != null)
                        {
                            inventory.setStackInSlot(SLOT_ITEM_INPUT, Helpers.consumeItem(stack, 1));
                            ItemStack outStack = recipe.getOutput();
                            outStack = inventory.insertItem(SLOT_OUTPUT_1, outStack, false);
                            if (!outStack.isEmpty())
                            {
                                inventory.insertItem(SLOT_OUTPUT_2, outStack, false);
                            }
                        }
                    }
                }
            }
        }

        // This is here to avoid duplication glitches
        if (requiresSlotUpdate)
        {
            cascadeFuelSlots();
        }

        // Pick up fuel items in the world
        pickupTimer--;
        if (pickupTimer <= 0)
        {
            if (pickupItemsFromWorld())
                pickupTimer = 20;
        }
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

    public void onCreate(ItemStack log)
    {
        burnTicks += getFuelAmount(log);
        burnTemperature += getFuelTemperature(log);
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

    public void debug()
    {
        TerraFirmaCraft.getLog().debug("Debugging Fire pit:");
        TerraFirmaCraft.getLog().debug("Temp {} | Burn Temp {} | Fuel Ticks {}", temperature, burnTemperature, burnTicks);
        TerraFirmaCraft.getLog().debug("Burning? {}", world.getBlockState(pos).getValue(LIT));
    }

    // Return false if it should not reset the count (AKA should it try and pick up next tick as well)
    private boolean pickupItemsFromWorld()
    {
        final List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)));
        for (EntityItem entity : items)
        {
            if (isStackFuel(entity.getItem()) && inventory.getStackInSlot(SLOT_FUEL_INPUT).isEmpty())
            {
                ItemStack toAdd = entity.getItem();
                toAdd.setCount(1);
                ItemStack leftover = inventory.insertItem(SLOT_FUEL_INPUT, toAdd, false);
                if (leftover.isEmpty())
                    entity.setDead();
                else
                    entity.setItem(leftover);
                return false;
            }
        }
        return true;
    }

}
