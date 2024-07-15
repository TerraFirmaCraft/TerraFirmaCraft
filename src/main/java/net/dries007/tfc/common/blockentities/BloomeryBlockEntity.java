/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable
{
    private static final Component NAME = Component.translatable(MOD_ID + ".block_entity.bloomery");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
        bloomery.checkForLastTickSync();
        bloomery.checkForCalendarUpdate();

        if (level.getGameTime() % 20 == 0)
        {
            // First, check if lit, and complete, then finalize the recipe and return to unlit state
            if (state.getValue(BloomeryBlock.LIT) && bloomery.getRemainingTicks() <= 0)
            {
                bloomery.completeRecipe();
                state = state.setValue(BloomeryBlock.LIT, false);
            }

            // Re-check the multiblock state and calculate the total capacity of the bloomery
            final Direction direction = state.getValue(BloomeryBlock.FACING);
            final int capacity = bloomery.calculateCapacity();

            // If we have to, dump items from the bloomery until we're back down at capacity.
            // Pop items off of the end of the list
            final boolean modified = bloomery.inputStacks.size() > capacity;

            bloomery.popItemsOffOverCapacity(bloomery.inputStacks, capacity);

            if (modified)
            {
                // If the bloomery structure was compromised, we halt the current recipe
                if (state.getValue(BloomeryBlock.LIT))
                {
                    state = state.setValue(BloomeryBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }

                // Bloomery gate (the front facing) structure became compromised
                if (!BloomeryBlock.canGateStayInPlace(level, pos, direction.getAxis()))
                {
                    level.destroyBlock(pos, true);
                    return;
                }

                bloomery.markForSync();
            }

            // Finally, if we're in a valid state, and we've checked the capacity, then we can attempt to add items in from the world
            final boolean lit = state.getValue(BloomeryBlock.LIT);
            if (!lit)
            {
                bloomery.addItemsFromWorld(capacity);
            }

            // And refresh the molten block(s) based on the current inputs
            MoltenBlock.manageMoltenBlockTower(level, bloomery.getInternalBlockPos(), lit, TFCConfig.SERVER.bloomeryMaxChimneyHeight.get(), bloomery.inputStacks.size(), TFCConfig.SERVER.bloomeryCapacity.get());
        }
    }

    protected final List<ItemStack> inputStacks;
    private long lastPlayerTick = Integer.MIN_VALUE;
    private long litTick;
    @Nullable protected BloomeryRecipe cachedRecipe;

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, defaultInventory(0), NAME);

        inputStacks = new ArrayList<>();
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        Helpers.readItemStacksFromNbt(provider, inputStacks, nbt.getList("inputStacks", Tag.TAG_COMPOUND));
        litTick = nbt.getLong("litTick");
        lastPlayerTick = nbt.getLong("lastTick");
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.put("inputStacks", Helpers.writeItemStacksToNbt(provider, inputStacks));
        nbt.putLong("litTick", litTick);
        nbt.putLong("lastTick", lastPlayerTick);
        super.saveAdditional(nbt, provider);
    }

    public long getRemainingTicks()
    {
        if (cachedRecipe == null)
        {
            updateCachedRecipe(); // this is called on client, allowing jade to show a recipe
        }
        if (cachedRecipe != null)
        {
            return cachedRecipe.getDuration() - getTicksSinceLit();
        }
        return 0;
    }

    public long getTicksSinceLit()
    {
        assert level != null;
        return Calendars.get(level).getTicks() - litTick;
    }

    @Nullable
    public BloomeryRecipe getCachedRecipe()
    {
        return cachedRecipe;
    }

    /**
     * @return The position of the inside of the bloomery structure, where the bloom would form.
     */
    public BlockPos getInternalBlockPos()
    {
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BloomeryBlock.FACING))
        {
            return worldPosition.relative(state.getValue(BloomeryBlock.FACING).getOpposite());
        }
        return worldPosition;
    }

    /**
     * @return The position on the outside of the bloomery block, where items are ejected from the bloomery.
     */
    public BlockPos getExternalBlock()
    {
        assert level != null;
        final BlockState state = level.getBlockState(worldPosition);
        if (state.hasProperty(BloomeryBlock.FACING))
        {
            return worldPosition.relative(state.getValue(BloomeryBlock.FACING));
        }
        return worldPosition;
    }

    /**
     * @return {@code true} if the lighting was successful.
     */
    public boolean light(BlockState state)
    {
        assert level != null;

        updateCachedRecipe();
        if (cachedRecipe != null)
        {
            // The recipe must be legal, so we can start the bloomery
            litTick = Calendars.get(level).getTicks();
            state = state.setValue(BloomeryBlock.LIT, true).setValue(BloomeryBlock.OPEN, false);
            level.setBlockAndUpdate(worldPosition, state);
            return true;
        }
        return false;
    }

    public int getInputCount()
    {
        return inputStacks.size();
    }

    public List<ItemStack> getInputStacks()
    {
        return inputStacks;
    }

    @Override
    public void ejectInventory()
    {
        dumpItems();
        destroyMolten();
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        if (cachedRecipe == null)
        {
            updateCachedRecipe();
        }
        if (level.isClientSide || cachedRecipe == null || !level.getBlockState(worldPosition).getValue(BloomeryBlock.LIT))
        {
            return;
        }
        final long finishTick = cachedRecipe.getDuration() + litTick;
        if (finishTick <= Calendars.SERVER.getTicks())
        {
            final long offset = Calendars.SERVER.getTicks() - finishTick;
            try (CalendarTransaction tr = Calendars.SERVER.transaction())
            {
                tr.add(-offset);
                completeRecipe();
            }
        }
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

    private void dumpItems()
    {
        assert level != null;

        final BlockPos pos = getExternalBlock();
        for (ItemStack stack : inputStacks)
        {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
        }
        cachedRecipe = null;
        inputStacks.clear();
    }

    private void popItemsOffOverCapacity(List<ItemStack> items, int capacity)
    {
        assert level != null;
        while (items.size() > capacity)
        {
            Helpers.spawnItem(level, worldPosition, items.remove(items.size() - 1));
        }
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
        // If the bloomery is empty, this will set the recipe to null. Otherwise, this should set the bloomery to a valid recipe
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
        final BlockPos internalPos = getInternalBlockPos();
        final List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, AABB.encapsulatingFullBlocks(internalPos, internalPos.offset(1, BloomeryBlock.getChimneyLevels(level, internalPos) + 1, 1)), EntitySelector.ENTITY_STILL_ALIVE);

        if (cachedRecipe == null)
        {
            assert inputStacks.isEmpty(); // If the cached recipe is null, we must have no inputs

            final Collection<RecipeHolder<BloomeryRecipe>> recipes = RecipeHelpers.getRecipes(level, TFCRecipeTypes.BLOOMERY);

            loop:
            for (ItemEntity entity : itemEntities)
            {
                // Optimization: pre-melt each input stack, and only check against the bloomery recipe's fluid input
                final @Nullable HeatingRecipe heat = HeatingRecipe.getRecipe(entity.getItem());
                if (heat != null)
                {
                    final FluidStack fluid = heat.assembleFluid(entity.getItem());
                    for (RecipeHolder<BloomeryRecipe> recipe : recipes)
                    {
                        if (recipe.value().matchesInput(fluid))
                        {
                            // Located a recipe that matches a primary input, so break
                            cachedRecipe = recipe.value();
                            markForSync();
                            break loop;
                        }
                    }
                }
            }
        }

        // Check again that the cached recipe is *not* null, so we either have a good existing one, or we've found a new one
        if (cachedRecipe != null)
        {
            // Then iterate through the list of item entities again, and count all possible items we can add, which match the recipe
            // We include both inputs and catalysts
            boolean hasSeenCatalyst = false, hasSeenInput = false;
            final List<ItemEntity> foundInputs = new ArrayList<>();
            for (ItemEntity entity : itemEntities)
            {
                final ItemStack stack = entity.getItem();
                if (cachedRecipe.matchesInput(stack))
                {
                    hasSeenInput = true;
                    foundInputs.add(entity);
                }
                else if (cachedRecipe.matchesCatalyst(stack))
                {
                    hasSeenCatalyst = true;
                    foundInputs.add(entity);
                }
            }
            if (hasSeenCatalyst && hasSeenInput || (!inputStacks.isEmpty() && (hasSeenCatalyst || hasSeenInput)))
            {
                // Now, insert as many items individually as we can, up to capacity
                Helpers.consumeItemsFromEntitiesIndividually(foundInputs, capacity - inputStacks.size(), inputStacks::add);
            }
            markForSync();
        }
    }

    private void destroyMolten()
    {
        assert level != null;

        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            MoltenBlock.removeMoltenBlockTower(level, worldPosition.relative(direction), TFCConfig.SERVER.bloomeryMaxChimneyHeight.get());
        }
    }

    /**
     * @return The maximum capacity of this bloomery, in number of items, based on the height and formation of the bloomery multiblock.
     */
    private int calculateCapacity()
    {
        assert level != null;

        final BlockPos pos = getInternalBlockPos();
        final Direction direction = getBlockState().getValue(BloomeryBlock.FACING);
        if (BloomeryBlock.isFormed(level, pos, direction))
        {
            return BloomeryBlock.getChimneyLevels(level, pos) * TFCConfig.SERVER.bloomeryCapacity.get();
        }
        return 0;
    }

    /**
     * Called upon completion of the bloomery recipe. This consumes all inputs, and produces all results.
     */
    private void completeRecipe()
    {
        assert level != null;
        if (cachedRecipe != null)
        {
            // First, consume all inputs and catalysts, producing two results:
            // 1. A fluid amount, of all the molten inputs, and
            // 2. A count of the catalyst stacks
            int fluidAmount = 0;
            int catalystAmount = 0;
            for (ItemStack stack : inputStacks)
            {
                final @Nullable FluidStack primaryInput = cachedRecipe.consumeInput(stack);
                if (primaryInput != null)
                {
                    fluidAmount += primaryInput.getAmount();
                }
                else // Assume catalyst
                {
                    catalystAmount++;
                }
            }

            // Now, produce output amounts based on the minimum ratio of the recipe, like barrel recipes
            final int producedAmount = Math.min(
                fluidAmount / cachedRecipe.getInputFluid().amount(),
                catalystAmount / cachedRecipe.getCatalyst().count()
            );

            if (producedAmount > 0)
            {
                final ItemStack outputStack = cachedRecipe.assembleOutput();

                // Update the output to be just below the melting temperature of the item
                final @Nullable HeatingRecipe recipe = HeatingRecipe.getRecipe(outputStack);
                if (recipe != null)
                {
                    HeatCapability.setTemperature(outputStack, recipe.getTemperature() - 1f);
                }

                final BlockPos pos = getInternalBlockPos();
                level.setBlockAndUpdate(pos, TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, BloomBlockEntity.TOTAL_LAYERS));
                level.getBlockEntity(pos, TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(outputStack, producedAmount));
            }
        }

        // Consume all inputs, clear the cached recipe, and set the bloomery unlit
        inputStacks.clear();
        cachedRecipe = null;
        level.setBlockAndUpdate(worldPosition, getBlockState().setValue(BloomeryBlock.LIT, false));
        destroyMolten();
    }

    private void updateCachedRecipe()
    {
        assert level != null;

        cachedRecipe = null;

        final Collection<RecipeHolder<BloomeryRecipe>> recipes = RecipeHelpers.getRecipes(level, TFCRecipeTypes.BLOOMERY);
        for (ItemStack stack : inputStacks)
        {
            // Optimization: pre-melt each input stack, and only check against the bloomery recipe's fluid input
            final @Nullable HeatingRecipe heat = HeatingRecipe.getRecipe(stack);
            if (heat != null)
            {
                final FluidStack fluid = heat.assembleFluid(stack);
                for (RecipeHolder<BloomeryRecipe> recipe : recipes)
                {
                    if (recipe.value().matchesInput(fluid))
                    {
                        // Located a recipe that matches one of our primary inputs
                        cachedRecipe = recipe.value();
                        return;
                    }
                }
            }
        }
    }
}
