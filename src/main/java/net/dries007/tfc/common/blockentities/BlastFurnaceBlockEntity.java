/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.container.BlastFurnaceContainer;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.IntArrayBuilder;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.data.Fuel;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class BlastFurnaceBlockEntity extends TickableInventoryBlockEntity<BlastFurnaceBlockEntity.BlastFurnaceInventory> implements ICalendarTickable
{
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.blast_furnace");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlastFurnaceBlockEntity entity)
    {
        entity.checkForLastTickSync();
        entity.checkForCalendarUpdate();

        if (level.getGameTime() % 20 == 0)
        {
            // Re-check the multiblock state and calculate the total capacity of the device
            final int capacity = entity.calculateCapacity();

            // If we have to, dump items from the device until we're back down at capacity.
            // Pop items off of the end of the list
            final boolean modified = entity.inputStacks.size() > capacity || entity.catalystStacks.size() > capacity || entity.fuelStacks.size() > capacity;

            entity.popItemsOffOverCapacity(entity.inputStacks, capacity);
            entity.popItemsOffOverCapacity(entity.catalystStacks, capacity);
            entity.popItemsOffOverCapacity(entity.fuelStacks, capacity);

            if (modified)
            {
                // If the device structure was compromised, we halt the current recipe
                if (state.getValue(BlastFurnaceBlock.LIT))
                {
                    state = state.setValue(BlastFurnaceBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }

                entity.markForSync();
            }

            // Finally, if we're in a valid state, and we've checked the capacity, then we can attempt to add items in from the world
            entity.addItemsFromWorld(capacity);

            // And refresh the molten block(s) based on the average of inputs and fuel
            MoltenBlock.manageMoltenBlockTower(level, entity.worldPosition.above(), state.getValue(BloomeryBlock.LIT), TFCConfig.SERVER.blastFurnaceMaxChimneyHeight.get(), entity.inputStacks.size() + entity.fuelStacks.size(), 2 * TFCConfig.SERVER.blastFurnaceCapacity.get());
        }

        if (state.getValue(BlastFurnaceBlock.LIT))
        {
            // Update fuel
            if (entity.burnTicks > 0)
            {
                entity.burnTicks -= TFCConfig.SERVER.blastFurnaceFuelConsumptionMultiplier.get() * (entity.airTicks > 0 ? 2 : 1); // Fuel burns twice as fast using bellows
            }
            if (entity.burnTicks <= 0 && !entity.consumeFuel())
            {
                entity.extinguish(state);
            }
        }
        else if (entity.burnTemperature > 0)
        {
            entity.extinguish(state);
        }
        if (entity.airTicks > 0)
        {
            if (entity.airTicks % 20 == 0 && entity.temperature > 400)
            {
                // Damage Tuyere every second of use while consuming bellows air, and the blast furnace is lit and hot
                final ItemStack tuyere = entity.inventory.getStackInSlot(0);
                if (!tuyere.isEmpty())
                {
                    Helpers.damageItem(tuyere, level);
                }
            }
            entity.airTicks--;
        }

        // Always update temperature / cooking, until the device is not hot anymore
        if (entity.temperature > 0 || entity.burnTemperature > 0)
        {
            entity.temperature = HeatCapability.adjustDeviceTemp(entity.temperature, entity.burnTemperature, entity.airTicks, false);

            // Ensures that cached recipes are cached properly and 1-1 with input stacks
            // This way we can iterate and remove pairwise
            entity.ensureCachedRecipesAreAligned();

            final List<FluidStack> newInputFluids = new ArrayList<>();
            final Iterator<ItemStack> inputIterator = entity.inputStacks.iterator();
            final Iterator<ItemStack> catalystIterator = entity.catalystStacks.iterator();
            final Iterator<HeatingRecipe> recipeIterator = entity.inputCachedRecipes.iterator();
            while (inputIterator.hasNext())
            {
                final ItemStack inputStack = inputIterator.next();
                final ItemStack catalystStack = catalystIterator.next();
                final HeatingRecipe inputRecipe = recipeIterator.next();

                final @Nullable IHeat inputHeat = HeatCapability.get(inputStack);
                if (inputHeat != null)
                {
                    // Update temperature of item
                    HeatCapability.addTemp(inputHeat, entity.temperature);

                    // Handle melting of the input. For now, just append results sequentially to a buffer, which will be added to the blast furnace later.
                    if (inputRecipe != null && inputRecipe.isValidTemperature(inputHeat.getTemperature()))
                    {
                        // Only convert fluid output, and append to the buffer
                        final FluidStack fluidStack = inputRecipe.assembleFluid(inputStack);
                        newInputFluids.add(fluidStack);

                        // And then remove this item, it's catalyst, and recipe from the iterators
                        inputIterator.remove();
                        catalystIterator.remove();
                        recipeIterator.remove();
                    }
                }
            }

            // Once we're done handling inputs, then we can handle outputs. First, accumulate the result fluids together
            if (!newInputFluids.isEmpty())
            {
                // Insert all new input fluids sequentially
                for (FluidStack newInputFluid : newInputFluids)
                {
                    entity.insertOrReplaceInputFluid(newInputFluid);
                }

                // Then, convert the input to the output via a recipe, just matching fluid ratios
                final BlastFurnaceRecipe recipe = BlastFurnaceRecipe.get(level, entity.inputFluid);
                if (recipe != null)
                {
                    final FluidStack newOutputFluid = recipe.assembleFluidOutput(entity.inputFluid);

                    // And merge into the output fluid stack
                    entity.outputFluidTank.fill(newOutputFluid, IFluidHandler.FluidAction.EXECUTE);
                }
            }

            entity.markForSync();
        }

        if (!entity.outputFluidTank.isEmpty())
        {
            // If we have output fluid, then try and transfer some, including heat, to the block below
            final @Nullable IFluidHandler belowFluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.below(), Direction.UP);
            if (belowFluidHandler != null && FluidHelpers.transferExact(entity.outputFluidTank, belowFluidHandler, 1))
            {
                // And if transfer happened, provide heat to the container below
                HeatCapability.provideHeatTo(level, pos.below(), Direction.UP, entity.temperature);
            }
            entity.markForSync();
        }
        entity.setChanged();
    }

    private final List<ItemStack> inputStacks; // Input items, that match any input to a blast furnace recipe
    private final List<HeatingRecipe> inputCachedRecipes; // Input cached recipes, 1-1 with input items
    private final List<ItemStack> catalystStacks; // Catalyst items, 1-1 with input items
    private final List<ItemStack> fuelStacks; // Fuel items, consumed sequentially

    private final IntArrayBuilder syncedData;
    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;

    private final FluidTank outputFluidTank; // The output fluid, after converting from the input fluid. This is dripped into the container below, excess is voided.
    private FluidStack inputFluid; // The fluid, after melting from the input. This is immediately converted to output fluid, unless there isn't enough, in which case it is stored here as excess

    @Nullable private BlastFurnaceRecipe cachedRecipe;

    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int airTicks; // Ticks of air provided by bellows
    private long lastPlayerTick = Integer.MIN_VALUE; // Last player tick this device was ticked (for purposes of catching up)
    private int lastKnownCapacity; // Last calculation of capacity (happens every 20 ticks), used by the gui

    public BlastFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLAST_FURNACE.get(), pos, state, BlastFurnaceInventory::new, NAME);

        inputStacks = new ArrayList<>();
        inputCachedRecipes = new ArrayList<>();
        catalystStacks = new ArrayList<>();
        fuelStacks = new ArrayList<>();

        inputFluid = FluidStack.EMPTY;
        outputFluidTank = new FluidTank(TFCConfig.SERVER.blastFurnaceFluidCapacity.get());

        syncedData = new IntArrayBuilder()
            .add(() -> lastKnownCapacity, value -> lastKnownCapacity = value)
            .add(() -> (int) temperature, value -> temperature = value);

        sidedFluidInventory = new SidedHandler.Builder<>(inventory);

        if (TFCConfig.SERVER.blastFurnaceEnableAutomation.get())
        {
            sidedInventory.on(inventory, side -> true); // Insert tuyere from all sides
            sidedFluidInventory.on(new PartialFluidHandler(inventory).extract(), side -> true); // Allow extracting fluid from all sides
        }
    }

    public int getCapacity()
    {
        return lastKnownCapacity;
    }

    public int getAirTicks()
    {
        return airTicks;
    }

    public int getInputCount()
    {
        return inputStacks.size();
    }

    public int getFuelCount()
    {
        return fuelStacks.size();
    }

    public int getCatalystCount()
    {
        return catalystStacks.size();
    }

    public float getTemperature()
    {
        return temperature;
    }

    public ContainerData getSyncedData()
    {
        return syncedData;
    }

    public boolean hasTuyere()
    {
        return !inventory.getStackInSlot(0).isEmpty();
    }

    public void intakeAir(int amount)
    {
        airTicks += amount;
        if (airTicks > BellowsBlockEntity.MAX_DEVICE_AIR_TICKS)
        {
            airTicks = BellowsBlockEntity.MAX_DEVICE_AIR_TICKS;
        }
    }

    public boolean light(Level level, BlockPos pos, BlockState state)
    {
        if (!fuelStacks.isEmpty())
        {
            level.setBlock(pos, state.setValue(BlastFurnaceBlock.LIT, true), Block.UPDATE_ALL);
            return true;
        }
        return false;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        Helpers.readItemStacksFromNbt(provider, inputStacks, nbt.getList("inputStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(provider, catalystStacks, nbt.getList("catalystStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(provider, fuelStacks, nbt.getList("fuelStacks", Tag.TAG_COMPOUND));

        inputFluid = FluidStack.parseOptional(provider, nbt.getCompound("inputFluid"));
        outputFluidTank.readFromNBT(provider, nbt.getCompound("outputFluidTank"));

        temperature = nbt.getFloat("temperature");
        burnTicks = nbt.getInt("burnTicks");
        airTicks = nbt.getInt("airTicks");
        burnTemperature = nbt.getFloat("burnTemperature");
        lastPlayerTick = nbt.getLong("lastPlayerTick");

        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.put("inputStacks", Helpers.writeItemStacksToNbt(provider, inputStacks));
        nbt.put("catalystStacks", Helpers.writeItemStacksToNbt(provider, catalystStacks));
        nbt.put("fuelStacks", Helpers.writeItemStacksToNbt(provider, fuelStacks));

        nbt.put("inputFluid", inputFluid.save(provider));
        nbt.put("outputFluidTank", outputFluidTank.writeToNBT(provider, new CompoundTag()));

        nbt.putFloat("temperature", temperature);
        nbt.putInt("burnTicks", burnTicks);
        nbt.putInt("airTicks", airTicks);
        nbt.putFloat("burnTemperature", burnTemperature);
        nbt.putLong("lastPlayerTick", lastPlayerTick);

        super.saveAdditional(nbt, provider);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;

        final HeatCapability.Remainder remainder = HeatCapability.consumeFuelForTicks(ticks, burnTicks, burnTemperature, fuelStacks);

        burnTicks = remainder.burnTicks();
        burnTemperature = remainder.burnTemperature();

        if (remainder.ticks() > 0)
        {
            // Consumed all fuel, so extinguish and cool instantly
            extinguish(getBlockState());
            for (ItemStack stack : inputStacks)
            {
                HeatCapability.setTemperature(stack, 0);
            }
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return BlastFurnaceContainer.create(this, inventory, containerId);
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.TUYERES);
    }

    @Override
    public void ejectInventory()
    {
        super.ejectInventory();
        dumpItems();
        destroyMolten();
    }

    private void dumpItems()
    {
        assert level != null;

        final BlockPos pos = worldPosition.above();

        // Spawn drops right above the blast furnace
        inputStacks.forEach(stack -> Helpers.spawnDropsAtExactCenter(level, pos, stack));
        catalystStacks.forEach(stack -> Helpers.spawnDropsAtExactCenter(level, pos, stack));
        fuelStacks.forEach(stack -> Helpers.spawnDropsAtExactCenter(level, pos, stack));
        cachedRecipe = null;
    }

    private void insertOrReplaceInputFluid(FluidStack newFluid)
    {
        inputFluid = insertOrReplaceFluid(newFluid, inputFluid);
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

    private void popItemsOffOverCapacity(List<ItemStack> items, int capacity)
    {
        assert level != null;
        while (items.size() > capacity)
        {
            Helpers.spawnItem(level, worldPosition, items.remove(items.size() - 1));
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
                burnTicks += fuel.duration();
                burnTemperature = fuel.temperature();
            }
            markForSync();
        }
        return burnTicks > 0;
    }

    public void extinguish(BlockState state)
    {
        assert level != null;
        level.setBlockAndUpdate(worldPosition, state.setValue(BlastFurnaceBlock.LIT, false));
        burnTicks = 0;
        burnTemperature = 0;
        markForSync();
    }

    /**
     * Attempt to add new items into the bloomery that are tossed into the chimney area
     *
     * @param capacity The maximum capacity (in items) that can be added.
     */
    private void addItemsFromWorld(int capacity)
    {
        assert level != null;

        // Update the current cached recipe, based on the current inputs.
        // If the device is empty, this will set the recipe to null. Otherwise, this should set the device to a valid recipe
        updateCachedRecipe();
        if (cachedRecipe == null && !inputStacks.isEmpty())
        {
            // Somehow, we found ourselves with an invalid recipe but yet we already have items.
            // This could trigger from a /reload, where the recipe was changed or modified.
            // Dump all items and start over
            dumpItems();
            markForSync();
        }

        // If we are already at capacity, we can exit early, as we cannot possibly add any more items
        if (inputStacks.size() == capacity && fuelStacks.size() == capacity)
        {
            return;
        }

        // Next, we need to check for item entities and try and add as many as we can.
        // If we don't have a recipe, we'll find the first recipe which matches one of the inputs, and assign that.
        // Then, assuming we do have a recipe, we'll re-check the inputs for any that can be added, and add up to an equal amount of both.
        final List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, AABB.encapsulatingFullBlocks(worldPosition, worldPosition.offset(1, BlastFurnaceBlock.getChimneyLevels(level, worldPosition) + 2, 1)), EntitySelector.ENTITY_STILL_ALIVE);

        if (cachedRecipe == null)
        {
            for (ItemEntity entity : items)
            {
                final ItemStack stack = entity.getItem();
                final BlastFurnaceRecipe recipe = BlastFurnaceRecipe.get(level, stack);
                if (recipe != null)
                {
                    // A valid recipe was found based on the inputs
                    // We break here, and assume that is our recipe from here out
                    cachedRecipe = recipe;
                    markForSync();
                    break;
                }
            }
        }

        // Check again that the cached recipe is *not* null, so we either have a good existing one, or we've found a new one
        if (cachedRecipe != null)
        {
            // Then iterate through the list of item entities again, and store (counting), all possible items which match our selected recipe
            // This time we match both primary inputs, catalysts, and also fuels.
            final List<ItemEntity> foundInputs = new ArrayList<>(), foundCatalysts = new ArrayList<>(), foundFuels = new ArrayList<>();
            int inputCount = 0, catalystCount = 0, fuelCount = 0;
            for (ItemEntity entity : items)
            {
                // Note here, we check that an item matches the recipe as a primary input first
                // If a specific item counts as both a primary input and a catalyst somehow, we only end up checking it as a primary input
                // We check fuels last
                final ItemStack stack = entity.getItem();
                if (cachedRecipe.matchesInput(stack))
                {
                    foundInputs.add(entity);
                    inputCount += stack.getCount();
                }
                else if (cachedRecipe.matchesCatalyst(stack))
                {
                    foundCatalysts.add(entity);
                    catalystCount += stack.getCount();
                }
                else if (Helpers.isItem(stack, TFCTags.Items.BLAST_FURNACE_FUEL))
                {
                    foundFuels.add(entity);
                    fuelCount += stack.getCount();
                }
            }

            // Now, based on the number of both inputs and catalysts we have identified, we try and insert as much as we can in pairs, up to the capacity
            // Note that the number of items and the number of catalyst items will always match, and each stack in the inputStacks + catalystStacks should have stack size = 1
            final int totalInsertCapacity = IntStream.of(
                capacity - inputStacks.size(),
                inputCount,
                catalystCount
            ).min().orElse(0);

            Helpers.consumeItemsFromEntitiesIndividually(foundInputs, totalInsertCapacity, inputStacks::add);
            Helpers.consumeItemsFromEntitiesIndividually(foundCatalysts, totalInsertCapacity, catalystStacks::add);

            // Fuels aren't restricted from being 1-1 with catalysts or inputs, so they get consumed independently, up to the same capacity
            final int totalFuelCapacity = Math.min(
                capacity - fuelStacks.size(),
                fuelCount
            );

            Helpers.consumeItemsFromEntitiesIndividually(foundFuels, totalFuelCapacity, fuelStacks::add);

            markForSync();
        }
    }

    private void destroyMolten()
    {
        assert level != null;

        MoltenBlock.removeMoltenBlockTower(level, worldPosition.above(), TFCConfig.SERVER.blastFurnaceMaxChimneyHeight.get());
    }

    /**
     * @return The maximum capacity of this bloomery, in number of items, based on the height and formation of the bloomery multiblock.
     */
    private int calculateCapacity()
    {
        assert level != null;
        return lastKnownCapacity = BlastFurnaceBlock.getChimneyLevels(level, worldPosition) * TFCConfig.SERVER.blastFurnaceCapacity.get();
    }

    private void updateCachedRecipe()
    {
        assert level != null;
        if (!inputStacks.isEmpty())
        {
            cachedRecipe = BlastFurnaceRecipe.get(level, inputStacks.get(0));
        }
        else
        {
            cachedRecipe = null;
        }
    }

    public static class BlastFurnaceInventory extends ItemStackHandler implements DelegateFluidHandler
    {
        private final BlastFurnaceBlockEntity blastFurnace;

        BlastFurnaceInventory(InventoryBlockEntity<?> entity)
        {
            super(1);

            blastFurnace = (BlastFurnaceBlockEntity) entity;
        }

        public FluidStack getFluid()
        {
            return blastFurnace.inputFluid;
        }

        public ItemStack getCatalyst()
        {
            return blastFurnace.catalystStacks.isEmpty() ? ItemStack.EMPTY : blastFurnace.catalystStacks.get(0);
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return blastFurnace.outputFluidTank;
        }
    }
}
