/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.Containers;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.inventory.ItemStackRecipeWrapper;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class PitKilnTileEntity extends PlacedItemTileEntity
{
    public static final Vec3i[] DIAGONALS = new Vec3i[] {new Vec3i(1, 0, 1), new Vec3i(-1, 0, 1), new Vec3i(1, 0, -1), new Vec3i(-1, 0, -1)};
    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;
    private static final float MAX_TEMP = 1200f;

    public static void serverTick(Level level, BlockPos pos, BlockState state, PitKilnTileEntity pitKiln)
    {
        if (pitKiln.isLit)
        {
            if (level.getGameTime() % 10 == 0)
            {
                BlockPos above = pos.above();
                if (level.isEmptyBlock(above))
                {
                    level.setBlockAndUpdate(above, Blocks.FIRE.defaultBlockState());
                }
                else
                {
                    BlockState stateAbove = level.getBlockState(above);
                    if (stateAbove.getMaterial() != Material.FIRE)
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
            }
            pitKiln.cookContents(false); // we are always heating

            long remainingTicks = TFCConfig.SERVER.pitKilnTicks.get() - (Calendars.SERVER.getTicks() - pitKiln.litTick);
            if (remainingTicks <= 0) //thus the only thing to do at the end is to delete the pit kiln block
            {
                pitKiln.cookContents(true);
                pitKiln.adjustTempsForTime(-1 * remainingTicks);
                pitKiln.emptyFuelContents();
                level.setBlockAndUpdate(pos.above(), Blocks.AIR.defaultBlockState());
                pitKiln.markForBlockUpdate();

                convertPitKilnToPlacedItem(level, pos);
            }
        }
    }

    public static void convertPitKilnToPlacedItem(Level world, BlockPos pos)
    {
        PitKilnTileEntity teOld = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
        if (teOld != null)
        {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            ItemStack[] inventory = new ItemStack[4];
            teOld.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                for (int i = 0; i < 4; i++)
                {
                    inventory[i] = cap.extractItem(i, 64, false);
                }
            });

            // Replace the block
            world.setBlock(pos, TFCBlocks.PLACED_ITEM.get().defaultBlockState(), 3);

            // Replace inventory items
            PlacedItemTileEntity teNew = Helpers.getTileEntity(world, pos, PlacedItemTileEntity.class);
            if (teNew != null)
            {
                teNew.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(cap -> {
                    for (int i = 0; i < 4; i++)
                    {
                        if (inventory[i] != null && !inventory[i].isEmpty())
                        {
                            cap.insertItem(i, inventory[i], false);
                        }
                    }
                });

                // Copy misc data
                teNew.isHoldingLargeItem = teOld.isHoldingLargeItem;
            }
        }
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

    public PitKilnTileEntity(BlockPos pos, BlockState state)
    {
        this(TFCTileEntities.PIT_KILN.get(), pos, state);
    }

    protected PitKilnTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        cachedRecipes = new HeatingRecipe[4];
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isLit = nbt.getBoolean("isLit");
        litTick = nbt.getLong("litTick");
        ContainerHelper.loadAllItems(nbt.getCompound("strawItems"), strawItems);
        ContainerHelper.loadAllItems(nbt.getCompound("logItems"), logItems);
        updateCache();
        super.load(nbt);
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putBoolean("isLit", isLit);
        nbt.putLong("litTick", litTick);
        nbt.put("strawItems", ContainerHelper.saveAllItems(new CompoundTag(), strawItems));
        nbt.put("logItems", ContainerHelper.saveAllItems(new CompoundTag(), logItems));
        return super.save(nbt);
    }

    @Override
    public void ejectInventory()
    {
        assert level != null;
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();
        strawItems.forEach(i -> Containers.dropItemStack(level, x, y, z, i));
        logItems.forEach(i -> Containers.dropItemStack(level, x, y, z, i));
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
                isLit = true;
                litTick = Calendars.SERVER.getTicks();
                markForBlockUpdate();
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(PitKilnBlock.STAGE, PitKilnBlock.LIT));
                level.setBlockAndUpdate(above, Blocks.FIRE.defaultBlockState());
                //Light other adjacent pit kilns
                for (Vec3i diagonal : DIAGONALS)
                {
                    BlockPos pitPos = worldPosition.offset(diagonal);
                    PitKilnTileEntity pitKiln = Helpers.getTileEntity(level, pitPos, PitKilnTileEntity.class);
                    if (pitKiln != null)
                    {
                        pitKiln.tryLight();
                    }
                }
                return true;
            }
        }
        return false;
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

    private void adjustTempsForTime(long remainingTicks)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat -> heat.setTemperature(Mth.clamp(heat.getTemperature() - remainingTicks / 10.0f, 0, MAX_TEMP)));
        }
    }

    private void cookContents(boolean isEnding)
    {
        if (level == null) return;
        float pitTicks = (float) TFCConfig.SERVER.pitKilnTicks.get();
        float temp = MAX_TEMP * ((pitTicks - (Calendars.SERVER.getTicks() - litTick)) / pitTicks);

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            int slot = i; // the boy genius Lex Manos has turned me into a functional programmer
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat -> {
                heat.setTemperature(isEnding ? MAX_TEMP : temp);
                HeatingRecipe recipe = cachedRecipes[slot];
                if (recipe != null && recipe.isValidTemperature(temp))
                {
                    ItemStack out = recipe.assemble(new ItemStackRecipeWrapper(stack));
                    inventory.setStackInSlot(slot, out);
                }
                else
                {
                    inventory.setStackInSlot(slot, ItemStack.EMPTY);
                }
            });
        }
    }

    private void updateCache()
    {
        if (level == null) return;
        for (int i = 0; i < 4; i++)
        {
            cachedRecipes[i] = HeatingRecipe.getRecipe(level, new ItemStackRecipeWrapper(inventory.getStackInSlot(i)));
        }
    }
}
