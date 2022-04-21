/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.BloomeryInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<BloomeryBlockEntity.Inventory> implements ICalendarTickable
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.bloomery");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
        bloomery.checkForLastTickSync();
        bloomery.checkForCalendarUpdate();

        if (level.getGameTime() % 20 == 0)
        {
            if (bloomery.cachedRecipe == null && !bloomery.inputStacks.isEmpty())
            {
                bloomery.updateCachedRecipe();
                if (bloomery.cachedRecipe == null && state.getValue(BloomeryBlock.LIT))
                {
                    bloomery.dumpItems();
                    state = state.setValue(BloomeryBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }
            }
            if (state.getValue(BloomeryBlock.LIT) && bloomery.getRemainingTicks() <= 0)
            {
                bloomery.completeRecipe();
                state = state.setValue(BloomeryBlock.LIT, false);
            }
            // Update multiblock status
            Direction direction = state.getValue(BloomeryBlock.FACING);
            bloomery.updateMaxStackValues(bloomery.cachedRecipe);

            boolean turnOff = false;
            while (bloomery.maxInput < bloomery.inputStacks.size())
            {
                turnOff = true;
                // Structure lost one or more chimney levels
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.inputStacks.remove(0));
                bloomery.markForSync();
            }
            while (bloomery.maxCatalyst < bloomery.catalystStacks.size())
            {
                turnOff = true;
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.catalystStacks.remove(0));
                bloomery.markForSync();
            }
            // Structure became compromised, unlit if needed
            if (turnOff && state.getValue(BloomeryBlock.LIT))
            {
                state = state.setValue(BloomeryBlock.LIT, false);
                level.setBlockAndUpdate(pos, state);
            }
            if (!BloomeryBlock.canGateStayInPlace(level, pos, direction.getAxis()))
            {
                // Bloomery gate (the front facing) structure became compromised
                level.destroyBlock(pos, true);
                return;
            }

            int oldCatalyst = bloomery.catalystStacks.size();
            int oldInput = bloomery.inputStacks.size();
            bloomery.addItemsFromWorld();
            if (oldCatalyst != bloomery.catalystStacks.size() || oldInput != bloomery.inputStacks.size())
            {
                bloomery.markForSync();
                bloomery.updateCachedRecipe();
            }
            bloomery.updateMoltenBlock(state.getValue(BloomeryBlock.LIT));
        }
    }

    protected int maxCatalyst = 0, maxInput = 0; // Helper variables, not necessary to serialize
    protected NonNullList<ItemStack> inputStacks = NonNullList.create();
    protected NonNullList<ItemStack> catalystStacks = NonNullList.create();

    private long lastPlayerTick;
    private long litTick;
    @Nullable protected BloomeryRecipe cachedRecipe;
    @Nullable protected BlockPos internalBlock, externalBlock;

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, Inventory::new, NAME);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        CompoundTag inputTag = nbt.getCompound("inputStacks");
        CompoundTag catalystTag = nbt.getCompound("catalystStacks");
        inputStacks = NonNullList.withSize(inputTag.getList("Items", Tag.TAG_COMPOUND).size(), ItemStack.EMPTY);
        catalystStacks = NonNullList.withSize(catalystTag.getList("Items", Tag.TAG_COMPOUND).size(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(inputTag, inputStacks);
        ContainerHelper.loadAllItems(nbt.getCompound("catalystStacks"), catalystStacks);

        litTick = nbt.getLong("litTick");
        lastPlayerTick = nbt.getLong("lastTick");
        updateCachedRecipe();
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inputStacks", ContainerHelper.saveAllItems(new CompoundTag(), inputStacks));
        nbt.put("catalystStacks", ContainerHelper.saveAllItems(new CompoundTag(), catalystStacks));
        nbt.putLong("litTick", litTick);
        nbt.putLong("lastTick", lastPlayerTick);
        super.saveAdditional(nbt);
    }

    public long getRemainingTicks()
    {
        if (cachedRecipe != null)
        {
            return cachedRecipe.getDuration() - (Calendars.SERVER.getTicks() - litTick);
        }
        return 0;
    }

    /**
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
     */
    public BlockPos getInternalBlockPos()
    {
        assert level != null;
        if (internalBlock == null)
        {
            Direction direction = level.getBlockState(worldPosition).getValue(BloomeryBlock.FACING);
            internalBlock = worldPosition.relative(direction.getOpposite());
        }
        return internalBlock;
    }

    /**
     * Gets the external (front facing) position
     *
     * @return BlockPos to dump items in world
     */
    public BlockPos getExternalBlock()
    {
        assert level != null;
        if (externalBlock == null)
        {
            if (getBlockState().hasProperty(BloomeryBlock.FACING))
            {
                Direction direction = getBlockState().getValue(BloomeryBlock.FACING);
                externalBlock = worldPosition.relative(direction);
            }
        }
        return externalBlock;
    }

    public boolean light(BlockState state)
    {
        assert level != null;
        if (Helpers.isBlock(level.getBlockState(getInternalBlockPos()), TFCBlocks.MOLTEN.get()) && cachedRecipe != null && cachedRecipe.matches(inventory, level))
        {
            litTick = Calendars.SERVER.getTicks();
            state = state.setValue(BloomeryBlock.LIT, true).setValue(BloomeryBlock.OPEN, false);
            level.setBlockAndUpdate(worldPosition, state);
            return true;
        }
        return false;
    }

    @Override
    public void ejectInventory()
    {
        super.ejectInventory();
        dumpItems();
        updateMoltenBlock(false);
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;
        if (level.isClientSide || cachedRecipe == null || !level.getBlockState(worldPosition).getValue(BloomeryBlock.LIT))
        {
            return;
        }
        final long finishTick = cachedRecipe.getDuration() + litTick;
        if (finishTick <= Calendars.SERVER.getTicks())
        {
            final long offset = Calendars.SERVER.getTicks() - finishTick;
            Calendars.SERVER.runTransaction(offset, offset, this::completeRecipe);
        }
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

    private void addItemsFromWorld()
    {
        assert level != null;
        if (cachedRecipe == null && !inputStacks.isEmpty() && !catalystStacks.isEmpty())
        {
            updateCachedRecipe();
            if (cachedRecipe == null)
            {
                dumpItems();
            }
        }
        final BlockPos internalPos = getInternalBlockPos();
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(internalPos, internalPos.offset(1, BloomeryBlock.getChimneyLevels(level, internalPos) + 1, 1)), EntitySelector.ENTITY_STILL_ALIVE))
        {
            boolean addOre = false;
            boolean addCatalyst = false;
            ItemStack stack = entity.getItem();
            BloomeryInventory temp = new FluidReaderInventory(stack, this);
            // the case where we already have a contained recipe (an as such non-empty input/catalyst stacks)
            if (cachedRecipe != null)
            {
                if (cachedRecipe.getCatalyst().test(stack)) // marginally less expensive to check catalyst first
                {
                    addCatalyst = true;
                }
                else if (cachedRecipe.getInputFluid().ingredient().test(temp.getFluid().getFluid()))
                {
                    addOre = true;
                }
            }
            else
            {
                // we gave up on having a complete picture of the situation, checking for the possibility of usable ore only
                // first check the inventory
                BloomeryRecipe possibleRecipe = getRecipeForOre(inventory);
                if (possibleRecipe == null)
                {
                    // we don't have any valid recipe internally, so let's add from the environment
                    possibleRecipe = getRecipeForOre(temp);
                }
                if (possibleRecipe != null)
                {
                    updateMaxStackValues(possibleRecipe); // set our max stack values based on the recipe we want to use

                    if (possibleRecipe.getCatalyst().test(stack))
                    {
                        addCatalyst = true;
                    }
                    else if (possibleRecipe.getInputFluid().ingredient().test(temp.getFluid().getFluid()))
                    {
                        addOre = true;
                    }
                }
            }
            if (addOre)
            {
                addWithIncrements(stack, inputStacks, maxInput, entity);
            }
            else if (addCatalyst)
            {
                // we can't really guarantee a max catalyst stack here without a complex heuristic
                // this is OK because the items get dumped on the next server tick in this case.
                addWithIncrements(stack, catalystStacks, Integer.MAX_VALUE, entity);
            }
        }
    }

    private void addWithIncrements(ItemStack toAdd, NonNullList<ItemStack> items, int max, ItemEntity entity)
    {
        if (items.size() < max)
        {
            markForSync();
        }
        while (items.size() < max)
        {
            items.add(toAdd.split(1));
            if (toAdd.getCount() <= 0)
            {
                entity.discard();
                break;
            }
        }
    }

    /**
     * Sets a molten block inside the bloomery structure. If there is nothing in the bloomery, attempts to delete any molten blocks left over.
     */
    private void updateMoltenBlock(boolean cooking)
    {
        assert level != null;
        final BlockPos internalPos = getInternalBlockPos();
        //If there's at least one item, show one layer so player knows that it is holding stacks
        int slagLayers = Math.max(1, inputStacks.size() / 8) * 4;
        for (int i = 0; i < 4; i++)
        {
            if (slagLayers > 0 && !inputStacks.isEmpty())
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
                level.setBlockAndUpdate(internalPos.above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, toPlace));
            }
            else
            {
                //Remove any surplus slag(ie: after cooking/structure became compromised)
                if (Helpers.isBlock(level.getBlockState(internalPos.above(i)), TFCBlocks.MOLTEN.get()))
                {
                    level.setBlockAndUpdate(internalPos.above(i), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private void updateMaxStackValues(@Nullable BloomeryRecipe recipe)
    {
        assert level != null;
        // Update multiblock status
        final BlockPos pos = getInternalBlockPos();
        int newMaxItems = BloomeryBlock.getChimneyLevels(level, pos) * 8;
        Direction direction = level.getBlockState(worldPosition).getValue(BloomeryBlock.FACING);

        if (!BloomeryBlock.isFormed(level, pos, direction))
        {
            newMaxItems = 0;
        }

        maxInput = newMaxItems;
        maxCatalyst = recipe == null ? 0 : getMultiplier() * recipe.getCatalystCount();
    }

    /**
     * @return The number of result items to generate
     */
    private int getMultiplier()
    {
        if (catalystStacks.isEmpty() || inventory.getFluid().isEmpty() || cachedRecipe == null) return 0;
        return Math.min(getTotalCatalyst() / cachedRecipe.getCatalystCount(), inventory.getFluid().getAmount() / cachedRecipe.getInputFluid().amount());
    }

    private int getTotalCatalyst()
    {
        if (catalystStacks.isEmpty())
        {
            return 0;
        }

        int count = 0;
        Item catalystItem = catalystStacks.get(0).getItem();
        for (ItemStack stack : catalystStacks)
        {
            if (stack.getItem() == catalystItem)
            {
                count += stack.getCount();
            }
        }
        return count;
    }

    private void completeRecipe()
    {
        assert level != null;
        if (cachedRecipe != null)
        {
            ItemStack result = cachedRecipe.assemble(inventory);
            // set the output to just below the melt temp
            Metal metal = Metal.get(inventory.getFluid().getFluid());
            if (metal != null)
            {
                result.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(metal.getMeltTemperature() - 1f));
            }
            final BlockPos pos = getInternalBlockPos();
            level.setBlockAndUpdate(pos, TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, BloomBlockEntity.TOTAL_LAYERS));
            level.getBlockEntity(pos, TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(result, getMultiplier()));
        }
        // void the internal stacks, if the ratio mismatched, too bad
        inputStacks.clear();
        catalystStacks.clear();
        cachedRecipe = null;
        level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(BloomeryBlock.LIT, false));
        updateMoltenBlock(false);
    }

    private void updateCachedRecipe()
    {
        cachedRecipe = getRecipe();
    }

    @Nullable
    private BloomeryRecipe getRecipe()
    {
        return getRecipe(inventory);
    }

    @Nullable
    private BloomeryRecipe getRecipe(BloomeryInventory inventory)
    {
        assert level != null;
        return level.getRecipeManager().getRecipeFor(TFCRecipeTypes.BLOOMERY.get(), inventory, level).orElse(null);
    }

    /**
     * @return a BloomeryRecipe possibly matching the inventory, but only checking the ore stack for the correct fluid.
     */
    @Nullable
    private BloomeryRecipe getRecipeForOre(BloomeryInventory inventory)
    {
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(TFCRecipeTypes.BLOOMERY.get()).stream().filter(recipe -> recipe.getInputFluid().test(inventory.getFluid())).findFirst().orElse(null);
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
                HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(stack);
                if (heatingRecipe != null)
                {
                    FluidStack toAdd = heatingRecipe.getOutputFluid(new ItemStackInventory(stack));
                    if (!toAdd.isEmpty())
                    {
                        if (fluid.isEmpty())
                        {
                            fluid = toAdd;
                        }
                        else if (fluid.isFluidEqual(toAdd))
                        {
                            fluid.setAmount(fluid.getAmount() + toAdd.getAmount());
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

    static class FluidReaderInventory extends InventoryItemHandler implements BloomeryInventory
    {
        private final ItemStack ore;

        public FluidReaderInventory(ItemStack ore, ISlotCallback callback)
        {
            super(callback, 0);
            this.ore = ore;
        }

        @Override
        public FluidStack getFluid()
        {
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(ore.copy());
            if (recipe == null) return FluidStack.EMPTY;
            FluidStack fluid = recipe.getOutputFluid(new ItemStackInventory(ore));
            fluid.setAmount(fluid.getAmount() * ore.getCount());
            return fluid;
        }

        @Override
        public ItemStack getCatalyst()
        {
            return ItemStack.EMPTY;
        }
    }
}
