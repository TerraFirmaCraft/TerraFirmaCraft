package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.BloomeryBlock;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomeryBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.bloomery");

    //todo: this is basically a straight conversion from 1.12, so I still have to comb through it and figure out if it makes sense
    public static void serverTick(Level level, BlockPos pos, BlockState state, BloomeryBlockEntity bloomery)
    {
        if (!level.isClientSide() && level.getGameTime() % 20 == 0)
        {
            if (state.getValue(BloomeryBlock.LIT))
            {
                if (bloomery.getRemainingTicks() <= 0)
                {
                    if (bloomery.cachedRecipe == null && !bloomery.oreStacks.isEmpty())
                    {
                        bloomery.cachedRecipe = BloomeryRecipe.getRecipe(bloomery.oreStacks.get(0));
                        if (bloomery.cachedRecipe == null)
                        {
                            bloomery.dumpItems();
                        }
                    }
                    if (bloomery.cachedRecipe != null)
                    {
                        level.setBlockAndUpdate(bloomery.getInternalBlock(), TFCBlocks.BLOOM.get().defaultBlockState());
                        BloomBlockEntity bloom = Helpers.getBlockEntity(level, bloomery.getInternalBlock(), BloomBlockEntity.class);
                        if (bloom != null)
                        {
                            bloom.setBloom(bloomery.cachedRecipe.getResultItem(bloomery.oreStacks));
                        }
                    }

                    bloomery.oreStacks.clear();
                    bloomery.fuelStacks.clear();
                    bloomery.cachedRecipe = null; // Clear recipe

                    bloomery.updateSlagBlock(false);
                    state = state.setValue(BloomeryBlock.LIT, false);
                    level.setBlockAndUpdate(pos, state);
                }
            }

            // Update multiblock status
            int newMaxItems = BloomeryBlock.getChimneyLevels(level, bloomery.getInternalBlock()) * 8;
            Direction direction = state.getValue(BloomeryBlock.FACING);
            //todo: isFormed may need to be either static, or a BlockEntity method, as this does not resolve
            //would it be better to do all of this in Block#randomTick, like the forge block does? - would also solve above problem
            if (!TFCBlocks.BLOOMERY.isFormed(level, bloomery.getInternalBlock(), direction))
            {
                newMaxItems = 0;
            }

            bloomery.maxFuel = newMaxItems;
            bloomery.maxOre = newMaxItems;
            boolean turnOff = false;
            while (bloomery.maxOre < bloomery.oreStacks.size())
            {
                turnOff = true;
                // Structure lost one or more chimney levels
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.oreStacks.get(0));
                bloomery.oreStacks.remove(0);
                bloomery.markForSync();
            }
            while (bloomery.maxFuel < bloomery.fuelStacks.size())
            {
                turnOff = true;
                Helpers.spawnItem(level, bloomery.getExternalBlock(), bloomery.fuelStacks.get(0));
                bloomery.fuelStacks.remove(0);
                bloomery.markForSync();
            }
            // Structure became compromised, unlit if needed
            if (turnOff && state.getValue(BloomeryBlock.LIT))
            {
                state = state.setValue(BloomeryBlock.LIT, false);
                level.setBlockAndUpdate(pos, state);
            }
            //todo: also will not resolve - static? move all this to Block#randomTick?
            if (!TFCBlocks.BLOOMERY.canGateStayInPlace(level, pos, direction.getAxis()))
            {
                // Bloomery gate (the front facing) structure became compromised
                level.destroyBlock(pos, true);
                return;
            }
            if (!bloomery.isInternalBlockComplete() && !bloomery.fuelStacks.isEmpty())
            {
                bloomery.dumpItems();
            }

            if (bloomery.isInternalBlockComplete())
            {
                int oldFuel = bloomery.fuelStacks.size();
                int oldOre = bloomery.oreStacks.size();
                bloomery.addItemsFromWorld();
                if (oldFuel != bloomery.fuelStacks.size() || oldOre != bloomery.oreStacks.size())
                {
                    bloomery.markForSync();
                }
            }
            bloomery.updateSlagBlock(state.getValue(BloomeryBlock.LIT));
        }

    }

    protected int maxFuel = 0, maxOre = 0; // Helper variables, not necessary to serialize
    protected final List<ItemStack> oreStacks = new ArrayList<>();
    protected final List<ItemStack> fuelStacks = new ArrayList<>();

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
        return TFCConfig.SERVER.bloomeryTicks.get() - (Calendars.SERVER.getTicks() - litTick);
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
        //correct way to get ticks?
        litTick = Calendars.SERVER.getTicks();
        return false;
    }

    //todo
    protected void dumpItems()
    {

    }

    protected boolean isInternalBlockComplete()
    {
        assert level != null;
        BlockState inside = level.getBlockState(getInternalBlock());
        return inside.getBlock() == TFCBlocks.CHARCOAL_PILE.get() && inside.getValue(CharcoalPileBlock.LAYERS) >= 8;
    }

    //todo
    protected void addItemsFromWorld()
    {

    }

    //todo
    protected void updateSlagBlock(boolean cooking)
    {

    }
}
