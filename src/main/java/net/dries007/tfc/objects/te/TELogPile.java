/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TELogPile extends TEInventory implements ITickable
{
    private static final int NUM_SLOTS = 4;

    private boolean burning;
    private long startBurningTick;
    private boolean isContainerOpen;

    public TELogPile()
    {
        super(NUM_SLOTS);

        startBurningTick = 0;
        burning = false;
    }

    public void setContainerOpen(boolean containerOpen)
    {
        isContainerOpen = containerOpen;
        setAndUpdateSlots(-1);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        if (!world.isRemote)
        {
            for (int i = 0; i < 4; i++)
            {
                if (!inventory.getStackInSlot(i).isEmpty())
                {
                    super.setAndUpdateSlots(slot);
                    return;
                }
            }
            if (!isContainerOpen)
            {
                world.setBlockToAir(pos);
            }
        }
        super.setAndUpdateSlots(slot);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        startBurningTick = nbt.getLong("startBurningTick");
        burning = nbt.getBoolean("burning");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setLong("startBurningTick", startBurningTick);
        nbt.setBoolean("burning", burning);
        return super.writeToNBT(nbt);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return !burning && world.getTileEntity(pos) == this;
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (burning)
            {
                if ((int) (CalendarTFC.PLAYER_TIME.getTicks() - startBurningTick) > ConfigTFC.Devices.CHARCOAL_PIT.ticks)
                {
                    // Attempt to turn this log pile into charcoal
                    createCharcoal();
                }
            }
            else
            {
                if (world.getBlockState(pos.up()).getBlock() == Blocks.FIRE)
                {
                    burning = true;
                    startBurningTick = CalendarTFC.PLAYER_TIME.getTicks();
                }
            }
        }
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return OreDictionaryHelper.doesStackMatchOre(stack, "logWood");
    }

    /**
     * Insert one log into the pile
     *
     * @param stack the log ItemStack to be inserted
     * @return true if one log was inserted, false otherwise
     */
    public boolean insertLog(ItemStack stack)
    {
        stack.setCount(1);
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.insertItem(i, stack, false).isEmpty())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Try to insert logs into every possible slot
     *
     * @param stack the log ItemStack to be inserted
     * @return 0 if none was inserted, number of logs inserted in the pile otherwise
     */
    public int insertLogs(ItemStack stack)
    {
        int start = stack.getCount();
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            stack = inventory.insertItem(i, stack, false);
            if (stack.isEmpty())
            {
                break;
            }
        }
        int remaining = stack.isEmpty() ? 0 : stack.getCount();
        return start - remaining;
    }

    public ItemStack getLog()
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return inventory.getStackInSlot(i);
            }
        }
        return ItemStack.EMPTY;
    }

    public void light()
    {
        burning = true;
        startBurningTick = CalendarTFC.PLAYER_TIME.getTicks();
        tryLightNearby();
        markDirty();
    }

    public int countLogs()
    {
        int logs = 0;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            logs += inventory.getStackInSlot(i).getCount();
        }
        return logs;
    }

    private void tryLightNearby()
    {
        for (EnumFacing side : EnumFacing.values())
        {
            IBlockState state = world.getBlockState(pos.offset(side));
            if (state.getBlock() instanceof BlockLogPile)
            {
                if (state.getValue(LIT)) continue;
                world.setBlockState(pos.offset(side), state.withProperty(LIT, true));
                TELogPile tile = Helpers.getTE(world, pos.offset(side), TELogPile.class);
                if (tile != null)
                {
                    tile.light();
                }
            }
        }
    }

    /**
     * This function does some magic **** to not create floating charcoal. Don't touch unless broken
     *
     * @author AlcatrazEscapee
     */
    private void createCharcoal()
    {
        int j = 0;
        Block block;
        do
        {
            j++;
            block = world.getBlockState(pos.down(j)).getBlock();
            // This is here so that the charcoal pile will collapse Bottom > Top
            // Because the pile scans Top > Bottom this is necessary to avoid floating blocks
            if (block instanceof BlockLogPile)
            {
                return;
            }
        } while (block == Blocks.AIR || block instanceof BlockCharcoalPile);

        double logs = countLogs() * (0.25 + 0.25 * Constants.RNG.nextFloat());
        int charcoal = (int) MathHelper.clamp(logs, 0, 8);
        if (charcoal == 0)
        {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return;
        }
        if (j == 1)
        {
            // This log pile is at the bottom of the charcoal pit
            world.setBlockState(pos, BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, charcoal));
            return;
        }
        for (int k = j - 1; k >= 0; k--)
        {
            // Climb back up from the bottom
            IBlockState state = world.getBlockState(pos.down(k));
            if (state.getBlock() == Blocks.AIR)
            {
                // If it hits air, place the remaining pile in that block
                world.setBlockState(pos.down(k), BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, charcoal));
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }

            if (state.getBlock() instanceof BlockCharcoalPile)
            {
                // Place what it can in the existing charcoal pit, then continue climbing
                charcoal += state.getValue(LAYERS);
                int toCreate = Math.min(charcoal, 8);
                world.setBlockState(pos.down(k), BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, toCreate));
                charcoal -= toCreate;
            }

            if (charcoal <= 0)
            {
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }
        }
        // If you exit the loop, its arrived back at the original position OR needs to rest the original position, and needs to replace that block
        world.setBlockState(pos, BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, charcoal));
    }
}
