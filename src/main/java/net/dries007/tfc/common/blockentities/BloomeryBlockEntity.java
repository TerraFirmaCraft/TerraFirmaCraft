/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemStackHandler;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.MoltenBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.bloomery");
    public static final Logger LOGGER = LogManager.getLogger();
    private static final Codec<List<ItemStack>> CODEC = ItemStack.CODEC.listOf();

    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
        if (level.getGameTime() % 20 == 0)
        {
            if (bloomery.cachedRecipe == null && !bloomery.inputStacks.isEmpty())
            {
                bloomery.cachedRecipe = bloomery.getRecipe(bloomery.inputStacks.get(0));
                if (bloomery.cachedRecipe == null && state.getValue(BloomeryBlock.LIT))
                {
                    LOGGER.info("dumping items from serverTick");
                    bloomery.dumpItems();
                    //todo: check if on and turn off?
                }
            }
            if (state.getValue(BloomeryBlock.LIT) && bloomery.getRemainingTicks() <= 0)
            {
                if (bloomery.cachedRecipe != null)
                {
                    ItemStack result = bloomery.cachedRecipe.getResult(bloomery.getTotalInput());
                    level.setBlockAndUpdate(bloomery.getInternalBlock(), TFCBlocks.BLOOM.get().defaultBlockState().setValue(BloomBlock.LAYERS, Math.min(result.getCount(), 8)));

                    level.getBlockEntity(bloomery.getInternalBlock(), TFCBlockEntities.BLOOM.get()).ifPresent(bloom -> {
                        bloom.setBloom(bloomery.cachedRecipe.getResult(bloomery.getTotalInput()));
                    });
                }

                bloomery.inputStacks.clear();
                bloomery.catalystStacks.clear();
                bloomery.cachedRecipe = null; // Clear recipe

                bloomery.updateMoltenBlock(false);
                state = state.setValue(BloomeryBlock.LIT, false);
                level.setBlockAndUpdate(pos, state);
            }

            // Update multiblock status
            int newMaxItems = BloomeryBlock.getChimneyLevels(level, bloomery.getInternalBlock()) * 8;
            Direction direction = state.getValue(BloomeryBlock.FACING);

            //todo: abort process/dump items if !isFormed?
            if (!BloomeryBlock.isFormed(level, bloomery.getInternalBlock(), direction))
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
                int maxCatalyst = bloomery.getTotalInput().getAmount() / bloomery.cachedRecipe.getInputFluid().getAmount() * bloomery.cachedRecipe.getCatalyst().getCount();
                bloomery.maxCatalyst = Math.min(maxCatalyst, 128);
            }

            boolean turnOff = false;
            while (bloomery.maxInput < bloomery.inputStacks.size())
            {
                LOGGER.info("too much input! maxInput is "+bloomery.maxInput+" and inputStacks is "+bloomery.inputStacks.size());
                turnOff = true;
                // Structure lost one or more chimney levels
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.inputStacks.get(0));
                bloomery.inputStacks.remove(0);
                bloomery.markForSync();
            }
            while (bloomery.maxCatalyst < bloomery.catalystStacks.size())
            {
                turnOff = true;
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.catalystStacks.get(0));
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

//            if (!BloomeryBlock.isFormed(level, bloomery.getInternalBlock(), state.getValue(BloomeryBlock.FACING)) || )
//            if (!bloomery.isInternalBlockComplete() && !bloomery.catalystStacks.isEmpty())
//            {
//                bloomery.dumpItems();
//            }

            int oldCatalyst = bloomery.catalystStacks.size();
            int oldInput = bloomery.inputStacks.size();
            bloomery.addItemsFromWorld();
            if (oldCatalyst != bloomery.catalystStacks.size() || oldInput != bloomery.inputStacks.size())
            {
                bloomery.markForSync();
            }
            bloomery.updateMoltenBlock(state.getValue(BloomeryBlock.LIT));
        }

    }

    protected int maxCatalyst = 0, maxInput = 0; // Helper variables, not necessary to serialize
    protected final List<ItemStack> inputStacks = new ArrayList<>();
    protected final List<ItemStack> catalystStacks = new ArrayList<>();

    private long litTick;
    @Nullable protected BloomeryInventory inventory;
    @Nullable protected BloomeryRecipe cachedRecipe;
    @Nullable protected BlockPos internalBlock, externalBlock;

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, defaultInventory(0), NAME);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        DataResult<List<ItemStack>> result = CODEC.parse(NbtOps.INSTANCE, nbt.get("inputStacks"));
        if (result.result().isPresent()) { inputStacks.addAll(result.result().get()); }
        result = CODEC.parse(NbtOps.INSTANCE, nbt.get("catalystStacks"));
        if (result.result().isPresent()) { catalystStacks.addAll(result.result().get()); }

        litTick = nbt.getLong("litTick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, inputStacks);
        if (result.get() instanceof Tag) { nbt.put("inputStacks", (Tag) result.get());}
        result = CODEC.encodeStart(NbtOps.INSTANCE, catalystStacks);
        if (result.get() instanceof Tag) { nbt.put("catalystStacks", (Tag) result.get());}

        nbt.putLong("litTick", litTick);
        super.saveAdditional(nbt);
    }

    public long getRemainingTicks()
    {
        if (cachedRecipe != null)
        {
            return cachedRecipe.getTime() - (Calendars.SERVER.getTicks() - litTick);
        }
        else
        {
            return 0;
        }
    }

    /**
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
     */
    public BlockPos getInternalBlock()
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
        if (level.getBlockState(getInternalBlock()).is(TFCBlocks.MOLTEN.get()) && cachedRecipe != null && cachedRecipe.isValidMixture(getTotalInput(), getTotalCatalyst()))
        {
            litTick = Calendars.SERVER.getTicks();
            state = state.setValue(BloomeryBlock.LIT, true).setValue(BloomeryBlock.OPEN, false);
            level.setBlockAndUpdate(worldPosition, state);
            return true;
        }
        return false;
    }

    public void onRemove()
    {
        dumpItems();
    }

    protected void dumpItems()
    {
        assert level != null;

        //todo: clear molten blocks and extinguish?
        inputStacks.forEach(i -> Helpers.spawnItem(level, worldPosition, i));
        inputStacks.clear();

        catalystStacks.forEach(i -> Helpers.spawnItem(level, worldPosition, i));
        catalystStacks.clear();

        cachedRecipe = null;
    }

    protected void addItemsFromWorld()
    {
        assert level != null;
        if (cachedRecipe == null && !inputStacks.isEmpty())
        {
            cachedRecipe = getRecipe(inputStacks.get(0));
            if (cachedRecipe == null)
            {
                dumpItems();
            }
        }
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(getInternalBlock(), getInternalBlock().offset(1, BloomeryBlock.getChimneyLevels(level, getInternalBlock()) + 1, 1)), EntitySelector.ENTITY_STILL_ALIVE))
        {
            ItemStack stack = entity.getItem();
            if (cachedRecipe == null)
            {
                cachedRecipe = getRecipe(stack);
            }
            if (cachedRecipe != null)
            {
                if (cachedRecipe.isValidInput(stack))
                {
                    if (inputStacks.size() < maxInput)
                    {
                        markForSync(); //markDirty
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
                else if (cachedRecipe.isValidCatalyst(stack))
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
        int catalystSlag = (int) Math.ceil(((float) catalystStacks.size() / maxCatalyst) * BloomeryBlock.getChimneyLevels(level, getInternalBlock()) * 8);
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
                    level.setBlockAndUpdate(getInternalBlock().above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, 4));
                }
                else
                {
                    level.setBlockAndUpdate(getInternalBlock().above(i), TFCBlocks.MOLTEN.get().defaultBlockState().setValue(MoltenBlock.LIT, cooking).setValue(MoltenBlock.LAYERS, slagLayers));
                    slagLayers = 0;
                }
            }
            else
            {
                //Remove any surplus slag(ie: after cooking/structure became compromised)
                if (level.getBlockState(getInternalBlock().above(i)).is(TFCBlocks.MOLTEN.get()))
                {
                    level.setBlockAndUpdate(getInternalBlock().above(i), Blocks.AIR.defaultBlockState());
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
                    if (inputFluid != fluidStack.getFluid())
                    {
                        throw new IllegalArgumentException("Bloomery had input items with different fluid types! That's not good!");
                    }
                    else
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
                catalystCount++;
            }
            else
            {
                throw new IllegalArgumentException("Bloomery had catalyst items of different types! That's not right!");
            }
        }
        return new ItemStack(catalystItem, catalystCount);
    }

    //todo: remove (debug)
    public String printInventory()
    {
        return "input: "+this.inputStacks+" catalyst: "+this.catalystStacks;
    }

    @Nullable
    private BloomeryRecipe getRecipe(ItemStack stack)
    {
        assert level != null;
        return level.getRecipeManager().getRecipeFor(TFCRecipeTypes.BLOOMERY.get(), new BloomeryInventory(stack), level).orElse(null);
    }

    //todo: not convinced this is necessary
    public class BloomeryInventory implements EmptyInventory
    {
        protected ItemStack inputStack;

        public BloomeryInventory(ItemStack inputStack)
        {
            this.inputStack = inputStack;
        }

        public Fluid getInputFluid()
        {
            HeatingRecipe heatingRecipe = HeatingRecipe.getRecipe(inputStack);
            if (heatingRecipe != null)
            {
                return heatingRecipe.getOutputFluid(new ItemStackInventory(inputStack)).getFluid();
            }
            else
            {
                return Fluids.EMPTY;
            }
        }

    }
}
