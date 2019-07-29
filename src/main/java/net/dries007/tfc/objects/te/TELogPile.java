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

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogPile;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;
import static net.dries007.tfc.objects.blocks.property.ILightableBlock.LIT;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TELogPile extends TEInventory implements ITickable
{
    private static final int NUM_SLOTS = 4;

    private boolean burning;
    private int burnTicks;
    private boolean isContainerOpen;

    public TELogPile()
    {
        super(NUM_SLOTS);

        burnTicks = 0;
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
        burnTicks = nbt.getInteger("burn_ticks");
        burning = nbt.getBoolean("burning");
        super.readFromNBT(nbt);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("burn_ticks", burnTicks);
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
                if (burnTicks < ConfigTFC.GENERAL.pitKilnTime)
                {
                    burnTicks++;
                }
                else
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
        this.burning = true;
        tryLightNearby();
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

    public boolean isBurning()
    {
        return burning;
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

    // This function does some magic **** to not create floating charcoal. Don't touch unless broken
    // - AlcatrazEscapee
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

        double logs = (double) countLogs();
        double log2 = 0.008d * logs * (logs + 42.5d) - 0.75d + 1.5d * Constants.RNG.nextFloat();
        int charcoal = (int) Math.min(8, Math.max(0, Math.round(log2)));
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
                int toCreate = charcoal > 8 ? 8 : charcoal;
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
