/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class PitKilnBlockEntity extends PlacedItemBlockEntity
{
    public static final Vec3i[] DIAGONALS = new Vec3i[] {new Vec3i(1, 0, 1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, -1)};
    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;

    public static void serverTick(Level level, BlockPos pos, BlockState state, PitKilnBlockEntity pitKiln)
    {
        if (pitKiln.isLit)
        {
            BlockPos above = pos.above();
            if (level.isEmptyBlock(above))
            {
                level.setBlockAndUpdate(above, Blocks.FIRE.defaultBlockState());
            }
            else
            {
                BlockState stateAbove = level.getBlockState(above);
                if (stateAbove.getBlock() != Blocks.FIRE)
                {
                    // consume contents, don't cook items, convert to placed item
                    pitKiln.emptyFuelContents();
                    convertPitKilnToPlacedItem(level, pos);
                    return;
                }
            }

            if (!isValid(level, pos))
            {
                // consume contents, don't cook items, convert to placed item
                pitKiln.emptyFuelContents();
                convertPitKilnToPlacedItem(level, pos);
                return;
            }

            final long remainingTicks = TFCConfig.SERVER.pitKilnTicks.get() - (Calendars.SERVER.getTicks() - pitKiln.litTick);
            if (remainingTicks <= 0)
            {
                pitKiln.updateCache();
            }
            pitKiln.cookContents();

            if (remainingTicks <= 0)
            {
                pitKiln.emptyFuelContents();
                level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
                pitKiln.markForBlockUpdate();

                convertPitKilnToPlacedItem(level, pos);
            }
        }
    }

    public static void convertPitKilnToPlacedItem(Level level, BlockPos pos)
    {
        Helpers.playSound(level, pos, SoundEvents.FIRE_EXTINGUISH);
        level.getBlockEntity(pos, TFCBlockEntities.PIT_KILN.get()).ifPresent(pitKiln -> {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            NonNullList<ItemStack> items = Helpers.extractAllItems(pitKiln.inventory);

            // Replace the block
            level.setBlock(pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState(), 3);

            // Replace inventory items
            level.getBlockEntity(pos, TFCBlockEntities.PLACED_ITEM.get()).ifPresent(placedItem -> {
                Helpers.insertAllItems(placedItem.inventory, items);

                // Copy misc data
                placedItem.isHoldingLargeItem = pitKiln.isHoldingLargeItem;
            });
        });
    }

    public static boolean isValid(Level level, BlockPos worldPosition)
    {
        for (Direction face : Direction.Plane.HORIZONTAL)
        {
            BlockPos relativePos = worldPosition.relative(face);
            BlockState relativeState = level.getBlockState(relativePos);
            Direction opposite = face.getOpposite();
            if (!relativeState.isFaceSturdy(level, relativePos, opposite) || relativeState.isFlammable(level, relativePos, opposite))
            {
                return false;
            }
        }
        return level.getBlockState(worldPosition.below()).isFaceSturdy(level, worldPosition.below(), Direction.UP);
    }

    private final NonNullList<ItemStack> logItems = NonNullList.withSize(WOOD_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> strawItems = NonNullList.withSize(STRAW_NEEDED, ItemStack.EMPTY);
    private final HeatingRecipe[] cachedRecipes;
    private long litTick;
    private boolean isLit;

    public PitKilnBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.PIT_KILN.get(), pos, state);
    }

    protected PitKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        cachedRecipes = new HeatingRecipe[4];
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        isLit = nbt.getBoolean("isLit");
        litTick = nbt.getLong("litTick");
        ContainerHelper.loadAllItems(nbt.getCompound("strawItems"), strawItems, provider);
        ContainerHelper.loadAllItems(nbt.getCompound("logItems"), logItems, provider);
        updateCache();
        super.loadAdditional(nbt, provider);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putBoolean("isLit", isLit);
        nbt.putLong("litTick", litTick);
        nbt.put("strawItems", ContainerHelper.saveAllItems(new CompoundTag(), strawItems, provider));
        nbt.put("logItems", ContainerHelper.saveAllItems(new CompoundTag(), logItems, provider));
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void ejectInventory()
    {
        assert level != null;

        super.ejectInventory();
        if (!isLit)
        {
            strawItems.forEach(stack -> Helpers.spawnItem(level, worldPosition, stack));
            logItems.forEach(stack -> Helpers.spawnItem(level, worldPosition, stack));
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, BlockHitResult hit)
    {
        if (state.hasProperty(PitKilnBlock.STAGE) && state.getValue(PitKilnBlock.STAGE) > 0)
        {
            for (ItemStack item : logItems)
            {
                if (!item.isEmpty())
                {
                    return item.copy();
                }
            }
            for (ItemStack item : strawItems)
            {
                if (!item.isEmpty())
                {
                    return item.copy();
                }
            }
        }
        return super.getCloneItemStack(state, hit);
    }

    public void deleteStraw(int slot)
    {
        strawItems.set(slot, ItemStack.EMPTY);
    }

    public void deleteLog(int slot)
    {
        logItems.set(slot, ItemStack.EMPTY);
    }

    public boolean isLit()
    {
        return isLit;
    }

    public long getLitTick()
    {
        return litTick;
    }

    public boolean hasFuel()
    {
        return !(logItems.stream().anyMatch(ItemStack::isEmpty) || strawItems.stream().anyMatch(ItemStack::isEmpty));
    }

    public boolean tryLight()
    {
        updateCache();
        if (hasFuel() && level != null && isValid(level, worldPosition) && !isLit())
        {
            BlockPos above = worldPosition.above();
            if (BaseFireBlock.canBePlacedAt(level, above, Direction.UP))
            {
                for (Direction facing : Direction.Plane.HORIZONTAL)
                {
                    if (!level.getBlockState(worldPosition.relative(facing)).isFaceSturdy(level, worldPosition.relative(facing), facing.getOpposite()))
                    {
                        return false;
                    }
                }
                light();
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PitKilnBlock.STAGE, PitKilnBlock.LIT));
                level.setBlockAndUpdate(above, Blocks.FIRE.defaultBlockState());

                // Light other adjacent pit kilns
                for (Vec3i diagonal : DIAGONALS)
                {
                    BlockPos pitPos = worldPosition.offset(diagonal);
                    if (level.getBlockEntity(pitPos) instanceof PitKilnBlockEntity kiln)
                    {
                        kiln.tryLight();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @VisibleForTesting
    public void light()
    {
        isLit = true;
        litTick = Calendars.SERVER.getTicks();
        markForBlockUpdate();
    }

    public void emptyFuelContents()
    {
        strawItems.clear();
        logItems.clear();
    }

    public void addStraw(ItemStack stack, int slot)
    {
        strawItems.set(slot, stack);
    }

    public void addLog(ItemStack stack, int slot)
    {
        logItems.set(slot, stack);
    }

    public NonNullList<ItemStack> getLogs()
    {
        return logItems;
    }

    public NonNullList<ItemStack> getStraws()
    {
        return strawItems;
    }

    @VisibleForTesting
    public void cookContents()
    {
        assert level != null;

        final float progress = Mth.inverseLerp(Calendars.get(level).getTicks(), litTick, litTick + TFCConfig.SERVER.pitKilnTicks.get());
        final float eagerProgress = Mth.clamp(progress * 1.125f, 0, 1); // Reach just above max temperature just before the end
        final float targetTemperature = Mth.lerp(eagerProgress, 0, TFCConfig.SERVER.pitKilnTemperature.get());

        for (int slot = 0; slot < inventory.getSlots(); slot++)
        {
            final ItemStack stack = inventory.getStackInSlot(slot);
            final @Nullable IHeat heat = HeatCapability.get(stack);
            if (heat != null)
            {
                heat.setTemperature(targetTemperature); // Heat each individual item
                final HeatingRecipe recipe = cachedRecipes[slot]; // And transform recipes
                if (recipe != null && recipe.isValidTemperature(targetTemperature))
                {
                    final ItemStack out = recipe.assembleItem(stack); // Liquids are lost
                    inventory.setStackInSlot(slot, out);
                }
            }
        }
    }

    public long getTicksLeft()
    {
        assert level != null;
        return litTick + TFCConfig.SERVER.pitKilnTicks.get() - Calendars.get(level).getTicks();
    }

    @VisibleForTesting
    public void updateCache()
    {
        for (int i = 0; i < 4; i++)
        {
            cachedRecipes[i] = HeatingRecipe.getRecipe(inventory.getStackInSlot(i));
        }
    }
}
