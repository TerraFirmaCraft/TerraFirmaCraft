package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.common.recipes.inventory.ItemStackInventory;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.bloomery");

    //todo: this is basically a straight conversion from 1.12, so I still have to comb through it and figure out if it makes sense
    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
        if (level.getGameTime() % 20 == 0)
        {
            if (state.getValue(BloomeryBlock.LIT))
            {
                if (bloomery.getRemainingTicks() <= 0)
                {
                    if (bloomery.cachedRecipe == null && !bloomery.inputStacks.isEmpty())
                    {
                        bloomery.cachedRecipe = BloomeryRecipe.getRecipe(bloomery.inputStacks.get(0));
                        if (bloomery.cachedRecipe == null)
                        {
                            bloomery.dumpItems();
                        }
                    }
                    if (bloomery.cachedRecipe != null)
                    {
                        //todo: mess with bloom layers and all that business
                        level.setBlockAndUpdate(bloomery.getInternalBlock(), TFCBlocks.BLOOM.get().defaultBlockState());
                        BloomBlockEntity bloom = Helpers.getBlockEntity(level, bloomery.getInternalBlock(), BloomBlockEntity.class);
                        if (bloom != null)
                        {
                            bloom.setBloom(bloomery.cachedRecipe.getResultItem());
                        }
                    }

                    bloomery.inputStacks.clear();
                    bloomery.catalystStacks.clear();
                    bloomery.cachedRecipe = null; // Clear recipe

                    bloomery.updateSlagBlock(false);
                    state = state.setValue(BloomeryBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }
            }

            // Update multiblock status
            int newMaxItems = BloomeryBlock.getChimneyLevels(level, bloomery.getInternalBlock()) * 8;
            Direction direction = state.getValue(BloomeryBlock.FACING);

            if (!BloomeryBlock.isFormed(level, bloomery.getInternalBlock(), direction))
            {
                newMaxItems = 0;
            }

            bloomery.maxFuel = newMaxItems;
            bloomery.maxInput = newMaxItems;
            boolean turnOff = false;
            while (bloomery.maxInput < bloomery.inputStacks.size())
            {
                turnOff = true;
                // Structure lost one or more chimney levels
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.inputStacks.get(0));
                bloomery.inputStacks.remove(0);
                bloomery.markForSync();
            }
            while (bloomery.maxFuel < bloomery.catalystStacks.size())
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
            if (!bloomery.isInternalBlockComplete() && !bloomery.catalystStacks.isEmpty())
            {
                bloomery.dumpItems();
            }

            if (bloomery.isInternalBlockComplete())
            {
                int oldFuel = bloomery.catalystStacks.size();
                int oldOre = bloomery.inputStacks.size();
                bloomery.addItemsFromWorld();
                if (oldFuel != bloomery.catalystStacks.size() || oldOre != bloomery.inputStacks.size())
                {
                    bloomery.markForSync();
                }
            }
            bloomery.updateSlagBlock(state.getValue(BloomeryBlock.LIT));
        }

    }

    protected int maxFuel = 0, maxInput = 0; // Helper variables, not necessary to serialize
    protected final List<ItemStack> inputStacks = new ArrayList<>();
    protected final List<ItemStack> catalystStacks = new ArrayList<>();

    private long litTick;
    @Nullable protected BloomeryRecipe cachedRecipe;
    @Nullable protected BlockPos internalBlock, externalBlock;

    public BloomeryBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOMERY.get(), pos, state, defaultInventory(0), NAME);
    }

    //todo
    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
    }

    //todo
    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
    }

    public long getRemainingTicks()
    {
        assert cachedRecipe != null;
        return cachedRecipe.getTime() - (Calendars.SERVER.getTicks() - litTick);
    }

    /**
     * Gets the internal (charcoal pile / bloom) position
     *
     * @return BlockPos of the internal block
     */
    public BlockPos getInternalBlock()
    {
        if (internalBlock == null)
        {
            /* todo: figure this one out
             * 1.12 code suggests the internal block (and external block, below) is *above* the bloomery block
            EnumFacing direction = world.getBlockState(pos).getValue(FACING);
            internalBlock = pos.up(OFFSET_INTERNAL.getY())
                .offset(direction, OFFSET_INTERNAL.getX())
                .offset(direction.rotateY(), OFFSET_INTERNAL.getZ());

             * but i don't see why that would be - perhaps i misunderstand the method
             */
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
        if (externalBlock == null)
        {
            Direction direction = level.getBlockState(worldPosition).getValue(BloomeryBlock.FACING);
            externalBlock = worldPosition.relative(direction);
        }
        return externalBlock;
    }

    //todo
    public boolean light(BlockState state)
    {
        litTick = Calendars.SERVER.getTicks();
        return false;
    }

    //todo
    protected void dumpItems()
    {
        assert level != null;
        //todo: clear molten blocks
        inputStacks.forEach(i -> Helpers.spawnItem(level, worldPosition, i));
        catalystStacks.forEach(i -> Helpers.spawnItem(level, worldPosition, i));
    }

    //todo: slated for removal
    protected boolean isInternalBlockComplete()
    {
        assert level != null;
        BlockState inside = level.getBlockState(getInternalBlock());
        return inside.getBlock() == TFCBlocks.CHARCOAL_PILE.get() && inside.getValue(CharcoalPileBlock.LAYERS) >= 8;
    }

    protected void addItemsFromWorld()
    {
        if (cachedRecipe == null && !inputStacks.isEmpty())
        {
            cachedRecipe = BloomeryRecipe.getRecipe(inputStacks.get(0));
            if (cachedRecipe == null)
            {
                dumpItems();
            }
        }
        assert level != null;
        //todo: this should check the full interior of the chimney; is it still necessary to check EntitySelectors.IS_ALIVE?
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, new AABB(getInternalBlock()), EntitySelector.ENTITY_STILL_ALIVE))
        {
            ItemStack stack = entity.getItem();
            if (cachedRecipe == null)
            {
                cachedRecipe = BloomeryRecipe.getRecipe(stack);
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
                    if (catalystStacks.size() < maxFuel)
                    {
                        markForSync(); //markDirty
                    }
                    while (catalystStacks.size() < maxFuel)
                    {
                        catalystStacks.add(stack.split(1));
                        if (stack.getCount() <= 0)
                        {
                            entity.discard();
                            break;
                        }
                    }
                }
                //Metal.get(HeatingRecipe.getRecipe(stack).getOutputFluid(new ItemStackInventory(stack)).getFluid());
            }
        }
    }

    //todo
    protected void updateSlagBlock(boolean cooking)
    {

    }

    public class BloomeryInventory implements EmptyInventory
    {
        protected ItemStack inputStack;

        public BloomeryInventory(ItemStack inputStack)
        {
            this.inputStack = inputStack;
        }

    }
}
