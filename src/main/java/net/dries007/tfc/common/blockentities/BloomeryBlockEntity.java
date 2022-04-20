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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.container.ISlotCallback;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.BloomeryInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<BloomeryBlockEntity.Inventory>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.bloomery");

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
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
                if (bloomery.cachedRecipe != null)
                {
                    ItemStack result = bloomery.cachedRecipe.assemble(bloomery.inventory);
                    level.setBlockAndUpdate(bloomery.getInternalBlockPos(), TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, Math.min(result.getCount(), 8)));

                    level.getBlockEntity(bloomery.getInternalBlockPos(), TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> bloom.setBloom(result));
                }

                bloomery.inputStacks.clear();
                bloomery.catalystStacks.clear();
                bloomery.cachedRecipe = null; // Clear recipe

                bloomery.updateMoltenBlock(false);
                state = state.setValue(BloomeryBlock.LIT, false);
                level.setBlockAndUpdate(pos, state);
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
                final int maxCatalyst = bloomery.getTotalInput().getAmount() / bloomery.cachedRecipe.getInputFluid().amount();
                bloomery.maxCatalyst = Math.min(maxCatalyst, 128);
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
        updateCachedRecipe();
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        ContainerHelper.saveAllItems(new CompoundTag(), inputStacks);
        ContainerHelper.saveAllItems(new CompoundTag(), catalystStacks);
        nbt.putLong("litTick", litTick);
        super.saveAdditional(nbt);
    }

    public long getRemainingTicks()
    {
        if (cachedRecipe != null)
        {
            return cachedRecipe.getTime() - (Calendars.SERVER.getTicks() - litTick);
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
        if (level.getBlockState(getInternalBlockPos()).is(TFCBlocks.MOLTEN.get()) && cachedRecipe != null && cachedRecipe.matches(inventory, level))
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

    protected void updateMoltenBlock(boolean cooking)
    {
        assert level != null;
        int catalystSlag = (int) Math.ceil(((float) catalystStacks.size() / maxCatalyst) * BloomeryBlock.getChimneyLevels(level, getInternalBlockPos()) * 8);
        int slag = inputStacks.size() + catalystSlag;
        //If there's at least one item, show one layer so player knows that it is holding stacks
        int slagLayers = slag > 0 && slag < 4 ? 1 : slag / 4;
        for (int i = 0; i < 4; i++)
        {
            if (slagLayers > 0)
            {
                if (slagLayers >= 4)
                {
                    slagLayers -= 4;
                    level.setBlockAndUpdate(getInternalBlockPos().above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, 4));
                }
                else
                {
                    level.setBlockAndUpdate(getInternalBlockPos().above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, slagLayers));
                    slagLayers = 0;
                }
            }
            else
            {
                //Remove any surplus slag(ie: after cooking/structure became compromised)
                if (level.getBlockState(getInternalBlockPos().above(i)).is(TFCBlocks.MOLTEN.get()))
                {
                    level.setBlockAndUpdate(getInternalBlockPos().above(i), Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    protected FluidStack getTotalInput()
    {
        Fluid inputFluid = null;
        int totalInput = 0;
        for (ItemStack stack : inputStacks)
        {
            HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(stack);
            if (heatingRecipe != null)
            {
                if (inputFluid == null)
                {
                    inputFluid = heatingRecipe.getOutputFluid(new ItemStackInventory(stack)).getFluid();
                }
                if (inputFluid != null)
                {
                    FluidStack fluidStack = heatingRecipe.getOutputFluid(new ItemStackInventory(stack));
                    if (inputFluid == fluidStack.getFluid())
                    {
                        totalInput += fluidStack.getAmount();
                    }
                }
            }
        }

        if (inputFluid == null)
        {
            return FluidStack.EMPTY;
        }
        else
        {
            return new FluidStack(inputFluid, totalInput);
        }
    }

    protected ItemStack getTotalCatalyst()
    {
        if (catalystStacks.size() == 0)
        {
            return ItemStack.EMPTY;
        }

        int catalystCount = 0;
        Item catalystItem = catalystStacks.get(0).getItem();
        for (ItemStack stack : catalystStacks)
        {
            if (stack.getItem() == catalystItem)
            {
                catalystCount += stack.getCount();
            }
        }
        return new ItemStack(catalystItem, catalystCount);
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
            return bloomery.getTotalInput();
        }

        @Override
        public ItemStack getCatalyst()
        {
            return bloomery.inputStacks.isEmpty() ? ItemStack.EMPTY : bloomery.inputStacks.get(0);
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
