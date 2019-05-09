/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.PitKilnRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class TEPitKiln extends TEPlacedItem implements ITickable
{
    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;

    public static void convertPlacedItemToPitKiln(World world, BlockPos pos, ItemStack strawStack)
    {
        TEPlacedItem teOld = Helpers.getTE(world, pos, TEPlacedItem.class);
        if (teOld != null)
        {
            // Replace the block
            world.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
            // Copy TE data
            TEPitKiln teNew = Helpers.getTE(world, pos, TEPitKiln.class);
            if (teNew != null)
            {
                teNew.copyDataFromPlacedItem(teOld, strawStack);
            }
        }
    }

    private final NonNullList<ItemStack> logItems = NonNullList.withSize(WOOD_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> strawItems = NonNullList.withSize(STRAW_NEEDED, ItemStack.EMPTY);

    private int burnTicksToGo;

    @Override
    public void update()
    {
        if (burnTicksToGo > 0)
        {
            burnTicksToGo--;
            BlockPos above = pos.up();
            if (world.isAirBlock(above))
            {
                world.setBlockState(above, Blocks.FIRE.getDefaultState());
            }
            else
            {
                IBlockState stateAbove = world.getBlockState(above);
                if (stateAbove.getMaterial() != Material.FIRE)
                {
                    // todo: decide what to do now.
                    burnTicksToGo = 0;
                    return;
                }
            }
            if (burnTicksToGo == 0)
            {
                strawItems.clear();
                logItems.clear();

                for (int i = 0; i < inventory.getSlots(); i++)
                {
                    ItemStack stack = inventory.getStackInSlot(i);
                    PitKilnRecipe recipe = PitKilnRecipe.get(stack);
                    if (recipe != null)
                    {
                        inventory.setStackInSlot(i, recipe.getOutput(stack, Metal.Tier.TIER_I));
                    }
                }

                world.setBlockToAir(above);
                updateBlock();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        burnTicksToGo = nbt.getInteger("burnTicksToGo");
        ItemStackHelper.loadAllItems(nbt.getCompoundTag("strawItems"), strawItems);
        ItemStackHelper.loadAllItems(nbt.getCompoundTag("logItems"), logItems);

        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setLong("burnTicksToGo", burnTicksToGo);
        compound.setTag("strawItems", ItemStackHelper.saveAllItems(new NBTTagCompound(), strawItems));
        compound.setTag("logItems", ItemStackHelper.saveAllItems(new NBTTagCompound(), logItems));
        return compound;
    }

    public boolean isLit()
    {
        return burnTicksToGo > 0;
    }

    public boolean hasFuel()
    {
        return !(logItems.stream().anyMatch(ItemStack::isEmpty) || strawItems.stream().anyMatch(ItemStack::isEmpty));
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(EntityPlayer player, ItemStack stack, boolean x, boolean z)
    {
        if (isLit())
        {
            return false;
        }
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);

        // Try and extract an item
        if (stack.isEmpty() || player.isSneaking())
        {
            // This will search through the logItems, then the strawItems
            ItemStack dropStack = logItems.stream().filter(i -> !i.isEmpty()).findFirst().orElseGet(() ->
                strawItems.stream().filter(i -> !i.isEmpty()).findFirst().orElse(ItemStack.EMPTY));
            if (!dropStack.isEmpty())
            {
                player.addItemStackToInventory(dropStack.splitStack(1));
                updateBlock();

                if (isEmpty())
                {
                    TEPlacedItem.convertPitKilnToPlacedItem(world, pos);
                }
                return true;
            }
        }
        else
        {
            // Insert an item
            int strawCount = getStrawCount(), logCount = getLogCount();

            // Straw
            if (OreDictionaryHelper.doesStackMatchOre(stack, "straw") && strawCount < STRAW_NEEDED)
            {
                addStraw(stack.splitStack(1));
                updateBlock();
                return true;
            }

            // Straw via thatch block (special exception)
            if (stack.getItem() == Item.getItemFromBlock(BlocksTFC.THATCH) && strawCount <= STRAW_NEEDED - 4)
            {
                stack.shrink(1);
                addStraw(new ItemStack(ItemsTFC.STRAW));
                addStraw(new ItemStack(ItemsTFC.STRAW));
                addStraw(new ItemStack(ItemsTFC.STRAW));
                addStraw(new ItemStack(ItemsTFC.STRAW));
                updateBlock();
                return true;
            }
            // Only insert logItems if all strawItems is inserted
            if (strawCount == STRAW_NEEDED)
            {
                // Logs
                if (OreDictionaryHelper.doesStackMatchOre(stack, "logWood") && logCount < WOOD_NEEDED)
                {
                    addLog(stack.splitStack(1));
                    updateBlock();
                    return true;
                }
                // Light
                if (logCount == WOOD_NEEDED && stack.getItem() instanceof ItemFlintAndSteel)
                {
                    // Flint and steel should light immediately
                    return tryLight();
                }
            }
        }
        return false;
    }

    @Override
    public void onBreakBlock()
    {
        strawItems.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        logItems.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        super.onBreakBlock();
    }

    public int getLogCount()
    {
        return (int) logItems.stream().filter(i -> !i.isEmpty()).count();
    }

    public int getStrawCount()
    {
        return (int) strawItems.stream().filter(i -> !i.isEmpty()).count();
    }

    public boolean tryLight()
    {
        if (!hasFuel()) return false;
        BlockPos above = pos.add(0, 1, 0);
        if (!Blocks.FIRE.canPlaceBlockAt(world, above)) return false;
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
        {
            if (!world.isSideSolid(pos.offset(facing), facing.getOpposite())) return false;
        }
        burnTicksToGo = ConfigTFC.GENERAL.pitKilnTime;
        updateBlock();
        world.setBlockState(above, Blocks.FIRE.getDefaultState());
        return true;
    }

    private void addStraw(ItemStack stack)
    {
        for (int i = 0; i < strawItems.size(); i++)
        {
            if (!strawItems.get(i).isEmpty()) continue;
            strawItems.set(i, stack);
            return;
        }
    }

    private void addLog(ItemStack stack)
    {
        for (int i = 0; i < logItems.size(); i++)
        {
            if (!logItems.get(i).isEmpty()) continue;
            logItems.set(i, stack);
            return;
        }
    }

    private void copyDataFromPlacedItem(TEPlacedItem teOld, ItemStack strawStack)
    {
        this.isHoldingLargeItem = teOld.isHoldingLargeItem;

        // Known at this point that strawStack matches the ore dictionary name "straw"
        addStraw(strawStack.splitStack(1));
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            inventory.setStackInSlot(i, teOld.inventory.getStackInSlot(i));
        }
    }
}
