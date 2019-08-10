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
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.capability.heat.CapabilityItemHeat;
import net.dries007.tfc.api.capability.heat.IItemHeat;
import net.dries007.tfc.api.recipes.heat.HeatRecipe;
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
            // Remove inventory items
            // This happens here to stop the block dropping its items in onBreakBlock()
            IItemHandler capOld = teOld.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack[] inventory = new ItemStack[4];
            if (capOld != null)
            {
                for (int i = 0; i < 4; i++)
                {
                    inventory[i] = capOld.extractItem(i, 64, false);
                }
            }

            // Replace the block
            world.setBlockState(pos, BlocksTFC.PIT_KILN.getDefaultState());
            // Copy TE data
            TEPitKiln teNew = Helpers.getTE(world, pos, TEPitKiln.class);
            if (teNew != null)
            {
                // Copy inventory
                IItemHandler capNew = teNew.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (capNew != null)
                {
                    for (int i = 0; i < 4; i++)
                    {
                        if (inventory[i] != null && !inventory[i].isEmpty())
                        {
                            capNew.insertItem(i, inventory[i], false);
                        }
                    }
                }
                // Copy misc data
                teNew.isHoldingLargeItem = teOld.isHoldingLargeItem;
                if (OreDictionaryHelper.doesStackMatchOre(strawStack, "blockStraw"))
                {
                    teNew.addStrawBlock();
                }
                else
                {
                    teNew.addStraw(strawStack.splitStack(1));
                }
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
                    ItemStack outputStack = ItemStack.EMPTY;
                    // First, heat up the item to max temperature, so the recipe can properly check the temperature of the item
                    IItemHeat heat = stack.getCapability(CapabilityItemHeat.ITEM_HEAT_CAPABILITY, null);
                    if (heat != null)
                    {
                        heat.setTemperature(CapabilityItemHeat.MAX_TEMPERATURE);

                        // Only Tier I and below can be melted in a pit kiln
                        HeatRecipe recipe = HeatRecipe.get(stack, Metal.Tier.TIER_I);
                        if (recipe != null)
                        {
                            outputStack = recipe.getOutputStack(stack);
                        }
                    }

                    // Reset item in inventory
                    inventory.setStackInSlot(i, outputStack);
                }

                world.setBlockToAir(above);
                updateBlock();
                // Since there will be no items in the pit kiln at this point
                TEPlacedItem.convertPitKilnToPlacedItem(world, pos);
            }
        }
    }

    @Override
    public void onBreakBlock(World worldIn, BlockPos pos)
    {
        strawItems.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        logItems.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        super.onBreakBlock(worldIn, pos);
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

        // Try and extract an item
        if (player.isSneaking())
        {
            // This will search through the logItems, then the strawItems
            ItemStack dropStack = logItems.stream().filter(i -> !i.isEmpty()).findFirst().orElseGet(() -> strawItems.stream().filter(i -> !i.isEmpty()).findFirst().orElse(ItemStack.EMPTY));
            if (!dropStack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, dropStack.splitStack(1));
                updateBlock();

                if (getStrawCount() == 0)
                {
                    TEPlacedItem.convertPitKilnToPlacedItem(world, pos);
                }
                return true;
            }
        }
        else if (!stack.isEmpty())
        {
            // Insert an item
            int strawCount = getStrawCount(), logCount = getLogCount();

            // Straw
            if (OreDictionaryHelper.doesStackMatchOre(stack, "straw") && strawCount < STRAW_NEEDED)
            {
                addStraw(stack.splitStack(1));
                world.playSound(null, pos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
                updateBlock();
                return true;
            }

            // Straw via thatch block (special exception)
            if (stack.getItem() == Item.getItemFromBlock(BlocksTFC.THATCH) && strawCount <= STRAW_NEEDED - 4)
            {
                stack.shrink(1);
                addStrawBlock();
                world.playSound(null, pos, SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
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
                    world.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 0.5f, 1.0f);
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
    public void readFromNBT(NBTTagCompound nbt)
    {
        burnTicksToGo = nbt.getInteger("burnTicksToGo");
        ItemStackHelper.loadAllItems(nbt.getCompoundTag("strawItems"), strawItems);
        ItemStackHelper.loadAllItems(nbt.getCompoundTag("logItems"), logItems);

        super.readFromNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("burnTicksToGo", burnTicksToGo);
        nbt.setTag("strawItems", ItemStackHelper.saveAllItems(new NBTTagCompound(), strawItems));
        nbt.setTag("logItems", ItemStackHelper.saveAllItems(new NBTTagCompound(), logItems));
        return super.writeToNBT(nbt);
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
        if (hasFuel() && isValid())
        {
            BlockPos above = pos.add(0, 1, 0);
            if (Blocks.FIRE.canPlaceBlockAt(world, above))
            {
                for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
                {
                    if (!world.isSideSolid(pos.offset(facing), facing.getOpposite()))
                    {
                        return false;
                    }
                }
                burnTicksToGo = ConfigTFC.GENERAL.pitKilnTime;
                updateBlock();
                world.setBlockState(above, Blocks.FIRE.getDefaultState());
                return true;
            }
        }
        return false;
    }

    public void assertValid()
    {
        if (isLit() && !isValid() && !world.isRemote)
        {
            // Stop burning, remove all straw + wood, etc.
            if (world.getBlockState(pos.up()).getMaterial() == Material.FIRE)
            {
                world.setBlockToAir(pos.up());
            }
            strawItems.clear();
            logItems.clear();
            // The easiest way to do this is just to convert to a PlacedItem:
            TEPlacedItem.convertPitKilnToPlacedItem(world, pos);
        }
    }

    private void addStrawBlock()
    {
        for (int i = 0; i < 4; i++)
        {
            addStraw(new ItemStack(ItemsTFC.STRAW));
        }
    }

    private void addStraw(ItemStack stack)
    {
        for (int i = 0; i < strawItems.size(); i++)
        {
            if (strawItems.get(i).isEmpty())
            {
                strawItems.set(i, stack);
                return;
            }
        }
    }

    private void addLog(ItemStack stack)
    {
        for (int i = 0; i < logItems.size(); i++)
        {
            if (logItems.get(i).isEmpty())
            {
                logItems.set(i, stack);
                return;
            }
        }
    }

    private boolean isValid()
    {
        for (EnumFacing face : EnumFacing.HORIZONTALS)
        {
            if (!world.getBlockState(pos.offset(face)).isNormalCube())
            {
                return false;
            }
        }
        return world.getBlockState(pos.down()).isNormalCube();
    }
}
