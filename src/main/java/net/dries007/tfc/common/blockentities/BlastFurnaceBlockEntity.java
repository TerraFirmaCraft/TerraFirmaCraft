/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendarTickable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BlastFurnaceBlockEntity extends TickableInventoryBlockEntity<BlastFurnaceBlockEntity.Inventory> implements ICalendarTickable
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.blast_furnace");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlastFurnaceBlockEntity forge)
    {
        forge.checkForLastTickSync();
        forge.checkForCalendarUpdate();

        if (state.getValue(BlastFurnaceBlock.LIT))
        {
            // Update fuel
            if (forge.burnTicks > 0)
            {
                forge.burnTicks -= forge.airTicks > 0 ? 2 : 1; // Fuel burns twice as fast using bellows
            }
            if (forge.burnTicks <= 0 && !forge.consumeFuel())
            {
                forge.extinguish(state);
            }
        }
        else if (forge.burnTemperature > 0)
        {
            forge.extinguish(state);
        }
        if (forge.airTicks > 0)
        {
            forge.airTicks--;
        }

        // Always update temperature / cooking, until the device is not hot anymore
        if (forge.temperature > 0 || forge.burnTemperature > 0)
        {
            forge.temperature = HeatCapability.adjustDeviceTemp(forge.temperature, forge.burnTemperature, forge.airTicks, false);

            // Provide heat to blocks below
            final BlockEntity below = level.getBlockEntity(pos.below());
            if (below != null)
            {
                below.getCapability(HeatCapability.BLOCK_CAPABILITY).ifPresent(cap -> cap.setTemperatureIfWarmer(forge.temperature));
            }

            // Ensures that cached recipes are cached properly and 1-1 with input stacks
            // This way we can iterate and remove pairwise
            forge.ensureCachedRecipesAreAligned();

            final List<FluidStack> newInputFluids = new ArrayList<>();

            final Iterator<ItemStack> inputIterator = forge.inputStacks.iterator();
            final Iterator<HeatingRecipe> recipeIterator = forge.inputCachedRecipes.iterator();
            while (inputIterator.hasNext())
            {
                final ItemStack inputStack = inputIterator.next();
                final HeatingRecipe inputRecipe = recipeIterator.next();
                inputStack.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> {

                    // Update temperature of item
                    float itemTemp = cap.getTemperature();
                    if (forge.temperature > itemTemp)
                    {
                        HeatCapability.addTemp(cap, forge.temperature);
                    }

                    // Handle melting of the input. For now, just append results sequentially to a buffer, which will be added to the blast furnace later.
                    if (inputRecipe != null && inputRecipe.isValidTemperature(cap.getTemperature()))
                    {
                        // Only convert fluid output, and append to the buffer
                        final FluidStack fluidStack = inputRecipe.getOutputFluid(new ItemStackInventory(inputStack));
                        newInputFluids.add(fluidStack);

                        // And then remove this item and recipe from the iterators
                        inputIterator.remove();
                        recipeIterator.remove();
                    }
                });
            }

            // Once we're done handling inputs, then we can handle outputs. First, accumulate the result fluids together
            if (!newInputFluids.isEmpty())
            {
                // Insert all new input fluids sequentially
                for (FluidStack newInputFluid : newInputFluids)
                {
                    forge.insertOrReplaceInputFluid(newInputFluid);
                }

                // Then, convert the input to the output via a recipe, just matching fluid ratios
                final BlastFurnaceRecipe recipe = BlastFurnaceRecipe.get(level, forge.inputFluid);
                if (recipe != null)
                {
                    final FluidStack newOutputFluid = recipe.assembleFluidOutput(forge.inputFluid);

                    // And merge into the output fluid stack
                    forge.insertOrReplaceOutputFluid(newOutputFluid);
                }
            }

            forge.markForSync();
        }
    }

    private final List<ItemStack> inputStacks; // Input items, that match any input to a blast furnace recipe
    private final List<HeatingRecipe> inputCachedRecipes; // Input cached recipes, 1-1 with input items
    private final List<ItemStack> catalystStacks; // Catalyst items, 1-1 with input items
    private final List<ItemStack> fuelStacks; // Fuel items, consumed sequentially

    private FluidStack inputFluid; // The fluid, after melting from the input. This is immediately converted to output fluid, unless there isn't enough, in which case it is stored here as excess
    private FluidStack outputFluid; // The output fluid, after converting from the input fluid. This is dripped into the container below, excess is voided.

    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int airTicks; // Ticks of air provided by bellows
    private long lastPlayerTick; // Last player tick this device was ticked (for purposes of catching up)

    public BlastFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLAST_FURNACE.get(), pos, state, Inventory::new, NAME);

        inputStacks = new ArrayList<>();
        inputCachedRecipes = new ArrayList<>();
        catalystStacks = new ArrayList<>();
        fuelStacks = new ArrayList<>();

        inputFluid = FluidStack.EMPTY;
        outputFluid = FluidStack.EMPTY;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        Helpers.readItemStacksFromNbt(inputStacks, nbt.getList("inputStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(catalystStacks, nbt.getList("catalystStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(fuelStacks, nbt.getList("fuelStacks", Tag.TAG_COMPOUND));

        super.saveAdditional(nbt);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        nbt.put("inputStacks", Helpers.writeItemStacksToNbt(inputStacks));
        nbt.put("catalystStacks", Helpers.writeItemStacksToNbt(catalystStacks));
        nbt.put("fuelStacks", Helpers.writeItemStacksToNbt(fuelStacks));

        super.loadAdditional(nbt);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {

    }

    @Override
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    private void insertOrReplaceInputFluid(FluidStack newFluid)
    {
        inputFluid = insertOrReplaceFluid(newFluid, inputFluid);
    }

    private void insertOrReplaceOutputFluid(FluidStack newFluid)
    {
        outputFluid = insertOrReplaceFluid(newFluid, outputFluid);
    }

    private FluidStack insertOrReplaceFluid(FluidStack newFluid, FluidStack currentFluid)
    {
        if (currentFluid.isEmpty())
        {
            // If the current fluid is empty, just take the new fluid
            return newFluid;
        }
        else if (currentFluid.getFluid() == newFluid.getFluid())
        {
            // If the fluids are the same, increment the count of the current input
            currentFluid.grow(newFluid.getAmount());
            return currentFluid;
        }
        else
        {
            // Fluids are different, so we need to void the current input and replace it
            return newFluid;
        }
    }

    private void ensureCachedRecipesAreAligned()
    {
        if (inputStacks.size() != inputCachedRecipes.size())
        {
            // Un-aligned, so re-cache all recipes
            inputCachedRecipes.clear();
            for (ItemStack inputStack : inputStacks)
            {
                inputCachedRecipes.add(HeatingRecipe.getRecipe(inputStack));
            }
        }
    }

    /**
     * Attempts to consume one piece of fuel. Returns if the device consumed any fuel (and so, ended up lit)
     */
    private boolean consumeFuel()
    {
        if (fuelStacks.isEmpty())
        {
            return false;
        }
        final ItemStack fuelStack = fuelStacks.get(0);
        if (!fuelStack.isEmpty())
        {
            // Try and consume a piece of fuel
            fuelStacks.remove(0);

            final Fuel fuel = Fuel.get(fuelStack);
            if (fuel != null)
            {
                burnTicks += fuel.getDuration();
                burnTemperature = fuel.getTemperature();
            }
            markForSync();
        }
        return burnTicks > 0;
    }

    private void extinguish(BlockState state)
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, state.setValue(BlastFurnaceBlock.LIT, false));
        burnTicks = 0;
        burnTemperature = 0;
        markForSync();
    }

    static class Inventory extends ItemStackHandler implements BlastFurnaceRecipe.Inventory
    {
        private final BlastFurnaceBlockEntity blastFurnace;

        Inventory(InventoryBlockEntity<?> entity)
        {
            super(0);

            blastFurnace = (BlastFurnaceBlockEntity) entity;
        }

        @Override
        public FluidStack getFluid()
        {
            return blastFurnace.inputFluid;
        }

        @Override
        public ItemStack getCatalyst()
        {
            return blastFurnace.catalystStacks.isEmpty() ? ItemStack.EMPTY : blastFurnace.catalystStacks.get(0);
        }
    }
}
