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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BlastFurnaceBlock;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.BlastFurnaceContainer;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Fuel;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BlastFurnaceBlockEntity extends TickableInventoryBlockEntity<BlastFurnaceBlockEntity.BlastFurnaceInventory> implements ICalendarTickable
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.blast_furnace");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlastFurnaceBlockEntity forge)
    {
        forge.checkForLastTickSync();
        forge.checkForCalendarUpdate();

        if (level.getGameTime() % 20 == 0)
        {
            // Re-check the multiblock state and calculate the total capacity of the device
            final int capacity = forge.calculateCapacity();

            // If we have to, dump items from the device until we're back down at capacity.
            // Pop items off of the end of the list
            final boolean modified = forge.inputStacks.size() > capacity || forge.catalystStacks.size() > capacity;
            while (forge.inputStacks.size() > capacity)
            {
                Helpers.spawnItem(level, pos, forge.inputStacks.remove(forge.inputStacks.size() - 1));
            }
            while (forge.catalystStacks.size() > capacity)
            {
                Helpers.spawnItem(level, pos, forge.catalystStacks.remove(forge.catalystStacks.size() - 1));
            }
            while (forge.fuelStacks.size() > capacity)
            {
                Helpers.spawnItem(level, pos, forge.fuelStacks.remove(forge.fuelStacks.size() - 1));
            }

            if (modified)
            {
                // If the device structure was compromised, we halt the current recipe
                if (state.getValue(BlastFurnaceBlock.LIT))
                {
                    state = state.setValue(BlastFurnaceBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }

                forge.markForSync();
            }

            // Finally, if we're in a valid state, and we've checked the capacity, then we can attempt to add items in from the world
            final boolean lit = state.getValue(BloomeryBlock.LIT);
            if (!lit)
            {
                forge.addItemsFromWorld(capacity);
            }

            // And refresh the molten block(s) based on the current inputs
            forge.updateMoltenBlock(lit);
        }

        if (state.getValue(BlastFurnaceBlock.LIT))
        {
            // Update fuel
            if (forge.burnTicks > 0)
            {
                forge.burnTicks -= TFCConfig.SERVER.blastFurnaceFuelConsumptionMultiplier.get() * (forge.airTicks > 0 ? 2 : 1); // Fuel burns twice as fast using bellows
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

    @Nullable private BlastFurnaceRecipe cachedRecipe;

    private float temperature; // Current Temperature
    private int burnTicks; // Ticks remaining on the current item of fuel
    private float burnTemperature; // Temperature provided from the current item of fuel
    private int airTicks; // Ticks of air provided by bellows
    private long lastPlayerTick; // Last player tick this device was ticked (for purposes of catching up)

    public BlastFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLAST_FURNACE.get(), pos, state, BlastFurnaceInventory::new, NAME);

        inputStacks = new ArrayList<>();
        inputCachedRecipes = new ArrayList<>();
        catalystStacks = new ArrayList<>();
        fuelStacks = new ArrayList<>();

        inputFluid = FluidStack.EMPTY;
        outputFluid = FluidStack.EMPTY;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        // todo
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return BlastFurnaceContainer.create(this, inventory, containerId);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        Helpers.readItemStacksFromNbt(inputStacks, nbt.getList("inputStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(catalystStacks, nbt.getList("catalystStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(fuelStacks, nbt.getList("fuelStacks", Tag.TAG_COMPOUND));

        super.loadAdditional(nbt);
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

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.TUYERES);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inputStacks", Helpers.writeItemStacksToNbt(inputStacks));
        nbt.put("catalystStacks", Helpers.writeItemStacksToNbt(catalystStacks));
        nbt.put("fuelStacks", Helpers.writeItemStacksToNbt(fuelStacks));

        super.saveAdditional(nbt);
    }

    @Override
    public void ejectInventory()
    {
        destroyMolten();
        dumpItems();
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
        if (inputStacks.size() == capacity)
        {
            return;
        }

        // Next, we need to check for item entities and try and add as many as we can.
        // If we don't have a recipe, we'll find the first recipe which matches one of the inputs, and assign that.
        // Then, assuming we do have a recipe, we'll re-check the inputs for any that can be added, and add up to an equal amount of both.
        final List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition, worldPosition.offset(1, BlastFurnaceBlock.getChimneyLevels(level, worldPosition) + 2, 1)), EntitySelector.ENTITY_STILL_ALIVE);

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

    /**
     * Sets a molten block inside the bloomery structure. If there is nothing in the bloomery, attempts to delete any molten blocks left over.
     */
    private void updateMoltenBlock(boolean cooking)
    {
        assert level != null;

        // If there's at least one item, show one layer so player knows that it is holding stacks
        final BlockPos internalPos = worldPosition.above();

        int slagLayers = 0;
        if (!inputStacks.isEmpty())
        {
            slagLayers = Math.max(1, 4 * inputStacks.size() / TFCConfig.SERVER.blastFurnaceCapacity.get());
        }

        for (int i = 0; i < TFCConfig.SERVER.blastFurnaceMaxChimneyHeight.get(); i++)
        {
            final BlockPos checkPos = internalPos.above(i);
            if (slagLayers > 0)
            {
                int toPlace = 4;
                if (slagLayers >= 4)
                {
                    slagLayers -= 4;
                }
                else
                {
                    toPlace = slagLayers;
                    slagLayers = 0;
                }
                level.setBlockAndUpdate(checkPos, TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, toPlace));
            }
            else
            {
                // Remove any surplus slag(ie: after cooking/structure became compromised)
                if (Helpers.isBlock(level.getBlockState(checkPos), TFCBlocks.MOLTEN.get()))
                {
                    level.setBlockAndUpdate(checkPos, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private void destroyMolten()
    {
        assert level != null;

        for (int i = 0; i < 4; i++)
        {
            final BlockPos moltenPos = worldPosition.above(1 + i);
            if (Helpers.isBlock(level.getBlockState(moltenPos), TFCBlocks.MOLTEN.get()))
            {
                level.destroyBlock(moltenPos, false);
            }
        }
    }

    /**
     * @return The maximum capacity of this bloomery, in number of items, based on the height and formation of the bloomery multiblock.
     */
    private int calculateCapacity()
    {
        assert level != null;
        return BlastFurnaceBlock.getChimneyLevels(level, worldPosition) * TFCConfig.SERVER.blastFurnaceCapacity.get();
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

    static class BlastFurnaceInventory extends ItemStackHandler implements BlastFurnaceRecipe.Inventory
    {
        private final BlastFurnaceBlockEntity blastFurnace;

        BlastFurnaceInventory(InventoryBlockEntity<?> entity)
        {
            super(1);

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
