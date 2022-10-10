/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.BloomeryInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<BloomeryBlockEntity.Inventory> implements ICalendarTickable
{
    private static final Component NAME = Helpers.translatable(MOD_ID + ".block_entity.bloomery");

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
            final boolean modified = bloomery.inputStacks.size() > capacity || bloomery.catalystStacks.size() > capacity;

            bloomery.popItemsOffOverCapacity(bloomery.inputStacks, capacity);
            bloomery.popItemsOffOverCapacity(bloomery.catalystStacks, capacity);

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
    protected final List<ItemStack> catalystStacks;

    private long lastPlayerTick = Integer.MIN_VALUE;
    private long litTick;
    @Nullable protected BloomeryRecipe cachedRecipe;

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, Inventory::new, NAME);

        inputStacks = new ArrayList<>();
        catalystStacks = new ArrayList<>();
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        Helpers.readItemStacksFromNbt(inputStacks, nbt.getList("inputStacks", Tag.TAG_COMPOUND));
        Helpers.readItemStacksFromNbt(catalystStacks, nbt.getList("catalystStacks", Tag.TAG_COMPOUND));
        litTick = nbt.getLong("litTick");
        lastPlayerTick = nbt.getLong("lastTick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inputStacks", Helpers.writeItemStacksToNbt(inputStacks));
        nbt.put("catalystStacks", Helpers.writeItemStacksToNbt(catalystStacks));
        nbt.putLong("litTick", litTick);
        nbt.putLong("lastTick", lastPlayerTick);
        super.saveAdditional(nbt);
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
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
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
     * Gets the external (front facing) position
     *
     * @return BlockPos to dump items in world
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

    public boolean light(BlockState state)
    {
        assert level != null;
        if (Helpers.isBlock(level.getBlockState(getInternalBlockPos()), TFCBlocks.MOLTEN.get()) && cachedRecipe != null && cachedRecipe.matches(inventory, level))
        {
            litTick = Calendars.get(level).getTicks();
            state = state.setValue(BloomeryBlock.LIT, true).setValue(BloomeryBlock.OPEN, false);
            level.setBlockAndUpdate(worldPosition, state);
            return true;
        }
        return false;
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
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    @Deprecated
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    private void dumpItems()
    {
        assert level != null;

        BlockPos pos = getExternalBlock();
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        inputStacks.forEach(i -> Containers.dropItemStack(level, x, y, z, i));
        catalystStacks.forEach(i -> Containers.dropItemStack(level, x, y, z, i));
        cachedRecipe = null;
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
        final List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, new AABB(internalPos, internalPos.offset(1, BloomeryBlock.getChimneyLevels(level, internalPos) + 1, 1)), EntitySelector.ENTITY_STILL_ALIVE);

        if (cachedRecipe == null)
        {
            for (ItemEntity entity : items)
            {
                final ItemStack stack = entity.getItem();
                final BloomeryRecipe recipe = BloomeryRecipe.get(level, stack);
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
            // This time we match both primary inputs, and catalysts.
            final List<ItemEntity> foundInputs = new ArrayList<>(), foundCatalysts = new ArrayList<>();
            int inputCount = 0, catalystCount = 0;
            for (ItemEntity entity : items)
            {
                // Note here, we check that an item matches the recipe as a primary input first
                // If a specific item counts as both a primary input and a catalyst somehow, we only end up checking it as a primary input
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
            }

            // Now, based on the number of both inputs and catalysts we have identified, we try and insert as much as we can in pairs, up to the capacity
            // Note that the number of items and the number of catalyst items will always match, and each stack in the inputStacks + catalystStacks should have stack size = 1
            final int totalInsertCapacity = Math.min(capacity - inputStacks.size(), Math.min(inputCount, catalystCount));

            Helpers.consumeItemsFromEntitiesIndividually(foundInputs, totalInsertCapacity, inputStacks::add);
            Helpers.consumeItemsFromEntitiesIndividually(foundCatalysts, totalInsertCapacity, catalystStacks::add);

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

    private void completeRecipe()
    {
        assert level != null;
        if (cachedRecipe != null)
        {
            final ItemStack result = cachedRecipe.assemble(inventory);

            final FluidStack inputFluid = inventory.getFluid();
            if (!catalystStacks.isEmpty() && !inputFluid.isEmpty())
            {
                final int producedAmount = inputFluid.getAmount() / cachedRecipe.getInputFluid().amount();

                // set the output to just below the melt temp
                Metal metal = Metal.get(inventory.getFluid().getFluid());
                if (metal != null)
                {
                    result.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(metal.getMeltTemperature() - 1f));
                }
                final BlockPos pos = getInternalBlockPos();
                level.setBlockAndUpdate(pos, TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, BloomBlockEntity.TOTAL_LAYERS));
                level.getBlockEntity(pos, TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(result, producedAmount));
            }
        }
        // void the internal stacks, if the ratio mismatched, too bad
        inputStacks.clear();
        catalystStacks.clear();
        cachedRecipe = null;
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(BloomeryBlock.LIT, false));
        destroyMolten();
    }

    private void updateCachedRecipe()
    {
        assert level != null;
        if (!inputStacks.isEmpty())
        {
            cachedRecipe = BloomeryRecipe.get(level, inputStacks.get(0));
        }
        else
        {
            cachedRecipe = null;
        }
    }

    public List<ItemStack> getInputStacks()
    {
        return inputStacks;
    }

    public List<ItemStack> getCatalystStacks()
    {
        return catalystStacks;
    }

    static class Inventory extends InventoryItemHandler implements BloomeryInventory
    {
        private final BloomeryBlockEntity bloomery;

        public Inventory(ISlotCallback callback)
        {
            super(callback, 0);
            bloomery = (BloomeryBlockEntity) callback;
        }

        @Override
        public FluidStack getFluid()
        {
            FluidStack fluid = FluidStack.EMPTY;
            for (ItemStack stack : bloomery.inputStacks)
            {
                final ItemStackInventory inventory = new ItemStackInventory(stack);
                final HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(inventory);
                if (heatingRecipe != null)
                {
                    final FluidStack toAdd = heatingRecipe.assembleFluid(inventory);
                    if (!toAdd.isEmpty())
                    {
                        if (fluid.isEmpty())
                        {
                            fluid = toAdd;
                        }
                        else if (fluid.isFluidEqual(toAdd))
                        {
                            fluid.grow(toAdd.getAmount());
                        }
                    }
                }
            }
            return fluid;
        }

        @Override
        public ItemStack getCatalyst()
        {
            return bloomery.catalystStacks.isEmpty() ? ItemStack.EMPTY : bloomery.catalystStacks.get(0);
        }
    }
}
