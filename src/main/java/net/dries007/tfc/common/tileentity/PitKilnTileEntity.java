package net.dries007.tfc.common.tileentity;

import javax.annotation.Nonnull;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

import net.minecraftforge.items.CapabilityItemHandler;

import net.dries007.tfc.common.blocks.devices.PitKilnBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.ItemStackRecipeWrapper;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public class PitKilnTileEntity extends PlacedItemTileEntity implements ITickableTileEntity
{
    public static void convertPitKilnToPlacedItem(World world, BlockPos pos)
    {
        PitKilnTileEntity teOld = Helpers.getTileEntity(world, pos, PitKilnTileEntity.class);
        if (teOld != null)
        {
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            ItemStack[] inventory = new ItemStack[4];
            teOld.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
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
                teNew.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent(cap -> {
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

    public static final Vector3i[] DIAGONALS = new Vector3i[] {new Vector3i(1, 0, 1), new Vector3i(-1, 0, 1), new Vector3i(1, 0, -1), new Vector3i(-1, 0, -1)};

    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;
    private static final float MAX_TEMP = 1200f;

    private final NonNullList<ItemStack> logItems = NonNullList.withSize(WOOD_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> strawItems = NonNullList.withSize(STRAW_NEEDED, ItemStack.EMPTY);
    private long litTick;
    private boolean isLit;
    private final HeatingRecipe[] cachedRecipes;

    public PitKilnTileEntity()
    {
        this(TFCTileEntities.PIT_KILN.get());
    }

    protected PitKilnTileEntity(TileEntityType<?> type)
    {
        super(type);
        cachedRecipes = new HeatingRecipe[4];
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt)
    {
        isLit = nbt.getBoolean("isLit");
        litTick = nbt.getLong("litTick");
        ItemStackHelper.loadAllItems(nbt.getCompound("strawItems"), strawItems);
        ItemStackHelper.loadAllItems(nbt.getCompound("logItems"), logItems);
        updateCache();
        super.load(state, nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT save(CompoundNBT nbt)
    {
        nbt.putBoolean("isLit", isLit);
        nbt.putLong("litTick", litTick);
        nbt.put("strawItems", ItemStackHelper.saveAllItems(new CompoundNBT(), strawItems));
        nbt.put("logItems", ItemStackHelper.saveAllItems(new CompoundNBT(), logItems));
        return super.save(nbt);
    }

    @Override
    public void tick()
    {
        if (isLit && level != null)
        {
            if (level.getGameTime() % 10 == 0)
            {
                BlockPos above = worldPosition.above();
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
                        emptyFuelContents();
                        convertPitKilnToPlacedItem(level, worldPosition);
                        return;
                    }
                }

                if (!isValid(level, worldPosition))
                {
                    // consume contents, don't cook items, convert to placed item
                    emptyFuelContents();
                    convertPitKilnToPlacedItem(level, worldPosition);
                    return;
                }
            }
            cookContents(false); // we are always heating

            long remainingTicks = TFCConfig.SERVER.pitKilnTicks.get() - (Calendars.SERVER.getTicks() - litTick);
            if (remainingTicks <= 0) //thus the only thing to do at the end is to delete the pit kiln block
            {
                cookContents(true);
                adjustTempsForTime(-1 * remainingTicks);
                emptyFuelContents();
                level.setBlockAndUpdate(worldPosition.above(), Blocks.AIR.defaultBlockState());
                markForBlockUpdate();

                convertPitKilnToPlacedItem(level, worldPosition);
            }
        }
    }

    @Override
    public void onRemove()
    {
        if (level == null) return;
        int x = worldPosition.getX();
        int y = worldPosition.getY();
        int z = worldPosition.getZ();
        strawItems.forEach(i -> InventoryHelper.dropItemStack(level, x, y, z, i));
        logItems.forEach(i -> InventoryHelper.dropItemStack(level, x, y, z, i));
        super.onRemove();
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
            if (AbstractFireBlock.canBePlacedAt(level, above, Direction.UP))
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
                for (Vector3i diagonal : DIAGONALS)
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

    private void adjustTempsForTime(long remainingTicks)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            stack.getCapability(HeatCapability.CAPABILITY).ifPresent(heat -> heat.setTemperature(MathHelper.clamp(heat.getTemperature() - remainingTicks / 10.0f, 0, MAX_TEMP)));
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

    public static boolean isValid(World level, BlockPos worldPosition)
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
}
