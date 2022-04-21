/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
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
            int newMaxItems = BloomeryBlock.getChimneyLevels(level, bloomery.getInternalBlockPos()) * 8;
            Direction direction = state.getValue(BloomeryBlock.FACING);

            if (!BloomeryBlock.isFormed(level, bloomery.getInternalBlockPos(), direction))
            {
                newMaxItems = 0;
            }

            bloomery.maxInput = newMaxItems;
            // since amount of catalyst no longer strictly equals number of iron ores/input, maxCatalyst is determined by amount needed to satisfy current amount of input in the bloomery (maxed at 128 just for sanity's sake)
            if (bloomery.cachedRecipe == null)
            {
                bloomery.maxCatalyst = 0;
            }
            else
            {
                bloomery.maxCatalyst = bloomery.getMultiplier() * bloomery.cachedRecipe.getCatalystCount();
            }

            boolean turnOff = false;
            while (bloomery.maxInput < bloomery.inputStacks.size())
            {
                turnOff = true;
                // Structure lost one or more chimney levels
                Helpers.spawnItem(level, bloomery.worldPosition, bloomery.inputStacks.get(0));
                bloomery.inputStacks.remove(0);
                bloomery.markForSync();
            }
            while (bloomery.maxCatalyst < bloomery.catalystStacks.size())
            {
                turnOff = true;
                Helpers.spawnItem(level, bloomery.worldPosition, bloomery.catalystStacks.get(0));
                bloomery.catalystStacks.remove(0);
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
    protected final NonNullList<ItemStack> inputStacks = NonNullList.create();
    protected final NonNullList<ItemStack> catalystStacks = NonNullList.create();

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
        ContainerHelper.loadAllItems(nbt.getCompound("inputStacks"), inputStacks);
        ContainerHelper.loadAllItems(nbt.getCompound("catalystStacks"), catalystStacks);
        litTick = nbt.getLong("litTick");
        lastPlayerTick = nbt.getLong("lastTick");
        updateCachedRecipe();
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        ContainerHelper.saveAllItems(new CompoundTag(), inputStacks);
        ContainerHelper.saveAllItems(new CompoundTag(), catalystStacks);
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
            Direction direction = level.getBlockState(worldPosition).getValue(BloomeryBlock.FACING);
            externalBlock = worldPosition.relative(direction);
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

    protected void dumpItems()
    {
        assert level != null;

        inputStacks.forEach(i -> Helpers.spawnItem(level, getExternalBlock(), i));
        inputStacks.clear();

        catalystStacks.forEach(i -> Helpers.spawnItem(level, getExternalBlock(), i));
        catalystStacks.clear();

        cachedRecipe = null;
    }

    protected void addItemsFromWorld()
    {
        assert level != null;
        if (cachedRecipe == null && !inputStacks.isEmpty())
        {
            updateCachedRecipe();
            if (cachedRecipe == null)
            {
                dumpItems();
            }
        }
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(getInternalBlockPos(), getInternalBlockPos().offset(1, BloomeryBlock.getChimneyLevels(level, getInternalBlockPos()) + 1, 1)), EntitySelector.ENTITY_STILL_ALIVE))
        {
            ItemStack stack = entity.getItem();
            BloomeryRecipe foundRecipe = getRecipe(new TemporaryInventory(stack, this));
            if ((cachedRecipe == null && foundRecipe != null) || (cachedRecipe != null && cachedRecipe == foundRecipe))
            {
                if (foundRecipe.matches(inventory, level))
                {
                    if (inputStacks.size() < maxInput)
                    {
                        markForSync();
                    }
                    while (inputStacks.size() < maxInput)
                    {
                        inputStacks.add(stack.split(1));
                        if (stack.getCount() <= 0)
                        {
                            entity.discard();
                            break;
                        }
                    }
                }
                else if (foundRecipe.getCatalyst().test(stack))
                {
                    if (catalystStacks.size() < maxCatalyst)
                    {
                        markForSync(); //markDirty
                    }
                    while (catalystStacks.size() < maxCatalyst)
                    {
                        catalystStacks.add(stack.split(1));
                        if (stack.getCount() <= 0)
                        {
                            entity.discard();
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets a molten block inside the bloomery structure. If there is nothing in the bloomery, attempts to delete any molten blocks left over.
     */
    protected void updateMoltenBlock(boolean cooking)
    {
        assert level != null;
        final BlockPos internalPos = getInternalBlockPos();
        final int catalystSlag = (int) Math.ceil(((float) catalystStacks.size() / maxCatalyst) * BloomeryBlock.getChimneyLevels(level, internalPos) * 8);
        final int slag = inputStacks.size() + catalystSlag;
        //If there's at least one item, show one layer so player knows that it is holding stacks
        int slagLayers = slag > 0 && slag < 4 ? 1 : slag / 4;
        for (int i = 0; i < 4; i++)
        {
            if (slagLayers > 0)
            {
                if (slagLayers >= 4)
                {
                    slagLayers -= 4;
                    level.setBlockAndUpdate(internalPos.above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, 4));
                }
                else
                {
                    level.setBlockAndUpdate(internalPos.above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, slagLayers));
                    slagLayers = 0;
                }
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
            level.setBlockAndUpdate(getInternalBlockPos(), TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, BloomBlockEntity.TOTAL_LAYERS));
            level.getBlockEntity(getInternalBlockPos(), TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(result, getMultiplier()));
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

    static class TemporaryInventory extends InventoryItemHandler implements BloomeryInventory
    {
        private final ItemStack item;

        public TemporaryInventory(ItemStack item, ISlotCallback callback)
        {
            super(callback, 0);
            this.item = item;
        }

        @Override
        public FluidStack getFluid()
        {
            final HeatingRecipe recipe = HeatingRecipe.getRecipe(item);
            return recipe == null ? FluidStack.EMPTY : recipe.getOutputFluid(new ItemStackInventory(item));
        }

        @Override
        public ItemStack getCatalyst()
        {
            return item;
        }
    }
}
