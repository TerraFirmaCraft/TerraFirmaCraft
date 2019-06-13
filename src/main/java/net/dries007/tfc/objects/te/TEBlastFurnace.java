/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.util.IMetalObject;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.util.ILightableBlock.LIT;
import static net.dries007.tfc.util.FuelManager.CHARCOAL_BURN_TEMPERATURE;

@ParametersAreNonnullByDefault
public class TEBlastFurnace extends TEInventory implements ITickable
{
    public static final int SLOT_TUYERE = 0;

    private List<ItemStack> oreStacks = new ArrayList<>();
    private int maxOreItems = 0; // Max ore stacks in the blast furnace (if it ever goes below, the top will be ejected)
    private int delayTimer = 0; // Time before checking the multiblock status of the blast furnace

    private int fuelTicks = 0;
    private int fluxAmount = 0;
    private int maxFluxAmount = 0;
    private int airTicks = 0;

    private float temperature; // Current Temperature

    public TEBlastFurnace()
    {
        super(1);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "tuyere");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        oreStacks.clear();
        NBTTagList ores = nbt.getTagList("ores", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < ores.tagCount(); i++)
        {
            oreStacks.add(new ItemStack(ores.getCompoundTagAt(i)));
        }
        fluxAmount = nbt.getInteger("flux");
        fuelTicks = nbt.getInteger("fuel");
        airTicks = nbt.getInteger("air");
        temperature = nbt.getFloat("temperature");
        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList ores = new NBTTagList();
        for (ItemStack stack : oreStacks)
        {
            ores.appendTag(stack.serializeNBT());
        }
        nbt.setTag("ores", ores);
        nbt.setInteger("flux", fluxAmount);
        nbt.setInteger("fuel", fuelTicks);
        nbt.setInteger("air", airTicks);
        nbt.setFloat("temperature", temperature);
        return super.writeToNBT(nbt);
    }

    @Override
    public void update()
    {
        if (world.isRemote) return;
        IBlockState state = world.getBlockState(pos);
        if (state.getValue(LIT))
        {
            if (delayTimer <= 0)
            {
                delayTimer = 20;
                // Update multiblock status
                int newMaxOreItems = BlocksTFC.BLAST_FURNACE.getChimneyLevels(world, pos) * 4;
                if (newMaxOreItems != maxOreItems)
                {
                    // Change happened here
                    maxOreItems = newMaxOreItems;
                    if (maxOreItems == 0)
                    {
                        // No items, so unlight and break the blast furnace
                    }
                    else
                    {
                        //
                    }
                }

                // Try and pull items inside
                addItemsFromWorld();

                // Update the slag stack
                updateSlagStack(newMaxOreItems);
            }
            else
            {
                delayTimer--;
            }

            // Update bellows air
            if (airTicks > 0)
            {
                airTicks--;
            }
            else
            {
                airTicks = 0;
            }

            // Update temperature - require a tuyere to take advantage of bellows, which will allow it to reach melting temperature
            float targetTemperature = CHARCOAL_BURN_TEMPERATURE;
            if (!inventory.getStackInSlot(SLOT_TUYERE).isEmpty())
            {
                targetTemperature += airTicks;
                if (targetTemperature > MAX_TEMPERATURE)
                {
                    targetTemperature = MAX_TEMPERATURE;
                }
            }
            if (temperature < targetTemperature)
            {
                // Modifier for heating = 2x for bellows
                temperature += (airTicks > 0 ? 2 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }
            else if (temperature > targetTemperature)
            {
                // Modifier for cooling = 0.5x for bellows
                temperature -= (airTicks > 0 ? 0.5 : 1) * ConfigTFC.GENERAL.temperatureModifierHeating;
            }

            // Update temperature of internal items
            oreStacks.removeIf(stack -> {
                IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                if (heat != null)
                {
                    heat.setTemperature(temperature);

                    if (heat.isMolten() && fluxAmount > 0)
                    {
                        IBlockState stateDown = world.getBlockState(pos.down());
                        if (stateDown.getBlock() == BlocksTFC.CRUCIBLE)
                        {
                            TECrucible crucible = Helpers.getTE(world, pos.down(), TECrucible.class);
                            if (crucible != null)
                            {
                                // Add molten steel alloy to stack
                                int metalAmount = convertToMoltenSteel(stack);
                                crucible.getAlloy().add(Metal.PIG_IRON, metalAmount);
                                // Damage the tuyere one point per item smelted
                                ItemStack tuyereStack = inventory.getStackInSlot(0);
                                if (!tuyereStack.isEmpty())
                                {
                                    //noinspection ConstantConditions
                                    tuyereStack.damageItem(1, null);
                                }
                                return true;
                            }
                        }
                    }
                }
                return false;
            });
        }
    }

    /**
     * Passed from BlockBlastFurnace's IBellowsConsumerBlock
     *
     * @param airAmount the air amount
     */
    public void onAirIntake(int airAmount)
    {
        ItemStack stack = inventory.getStackInSlot(SLOT_TUYERE);
        if (!stack.isEmpty())
        {
            airTicks += airAmount;
            if (airTicks > 600)
            {
                airTicks = 600;
            }
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

    /**
     * Gets the metal amount from an item stack in the blast furnace
     *
     * @param stack A stack in the oreStacks list
     * @return the amount of pig iron liquid metal that should be produced
     */
    private int convertToMoltenSteel(ItemStack stack)
    {
        if (!stack.isEmpty() && stack.getItem() instanceof IMetalObject)
        {
            IMetalObject metal = (IMetalObject) stack.getItem();
            if (metal.getMetal(stack) == Metal.WROUGHT_IRON)
            {
                return metal.getSmeltAmount(stack);
            }
        }
        return 0;
    }

    /**
     * Check for valid iron ore items
     *
     * @param stack the item stack to check
     * @return true if the item has the ore name "oreIron", can be heated, and has a metal object ({@link IMetalObject}
     */
    private boolean isValidIronOre(ItemStack stack)
    {
        if (!stack.isEmpty() && OreDictionaryHelper.doesStackMatchOre(stack, "oreIron"))
        {
            return stack.hasCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null) && stack.getItem() instanceof IMetalObject;
        }
        return false;
    }

    private void addItemsFromWorld()
    {
        for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(getPos().up(), getPos().add(1, 5, 1)), EntitySelectors.IS_ALIVE))
        {
            ItemStack stack = entityItem.getItem();
            if (OreDictionaryHelper.doesStackMatchOre(stack, "dustFlux"))
            {
                // Add flux
                if (fluxAmount < maxFluxAmount)
                {
                    fluxAmount += Math.min(stack.getCount(), maxFluxAmount - fluxAmount);
                    entityItem.setDead();
                }
            }
            else if (isValidIronOre(stack))
            {
                // Add ore
                if (oreStacks.size() < maxOreItems)
                {
                    ItemStack singleItem = stack.copy();
                    singleItem.setCount(1);
                    stack.shrink(1);
                    if (stack.getCount() <= 0)
                    {
                        entityItem.setDead();
                    }
                    oreStacks.add(singleItem);
                }
            }
        }
    }

    /**
     * Updates the slag blocks in the column of the blast furnace
     *
     * @param maxLevels the max amount of ore to be held
     */
    private void updateSlagStack(int maxLevels)
    {
        // Slag is purely an effect - the blocks do not hold any material or items, nor does breaking them affect the operation of the blast furnace
        for (int i = 0; i < maxLevels / 4; i++)
        {
            // If the items exist
        }
    }
}
