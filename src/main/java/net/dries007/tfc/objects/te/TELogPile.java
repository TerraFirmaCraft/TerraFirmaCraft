/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.te;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLogPile;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.Constants.MOD_ID;
import static net.dries007.tfc.objects.blocks.BlockCharcoalPile.LAYERS;
import static net.dries007.tfc.objects.blocks.wood.BlockLogPile.ONFIRE;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TELogPile extends TESidedInventory implements ITickable
{

    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "log_pile");

    private static final int NUM_SLOTS = 4;

    public static boolean isStackValid(ItemStack stack)
    {
        return (stack.isEmpty() || Helpers.doesStackMatchOre(stack, "logWood"));
    }

    private final int maxBurnTicks = 8000; // 8 In-game Hours
    public boolean burning;
    private int burnTicks;

    public TELogPile()
    {
        super(NUM_SLOTS);

        burnTicks = 0;
        burning = false;
    }

    public void update()
    {
        if (world.isRemote) { return; }

        if (burning)
        {
            if (burnTicks < maxBurnTicks)
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
            world.setBlockToAir(pos);
        }
        super.setAndUpdateSlots(slot);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return isStackValid(stack);
    }

    @Override
    public void readFromNBT(NBTTagCompound c)
    {
        burnTicks = c.getInteger("burn_ticks");
        burning = c.getBoolean("burning");
        super.readFromNBT(c);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound c)
    {
        c.setInteger("burn_ticks", burnTicks);
        c.setBoolean("burning", burning);
        return super.writeToNBT(c);
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

    private void tryLightNearby()
    {
        for (EnumFacing side : EnumFacing.values())
        {
            IBlockState state = world.getBlockState(pos.offset(side));
            if (state.getBlock() instanceof BlockLogPile)
            {
                if (state.getValue(ONFIRE)) continue;
                world.setBlockState(pos.offset(side), state.withProperty(ONFIRE, true));
                TELogPile tile = Helpers.getTE(world, pos.offset(side), TELogPile.class);
                if (tile != null)
                {
                    tile.light();
                }
            }
        }
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

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return (oldState.getBlock() != newState.getBlock());
    }

    // This function does some magic **** to not create floating charcoal. Don't touch unless broken
    private void createCharcoal()
    {
        int j = 0;
        Block block;
        do
        {
            j++;
            block = world.getBlockState(pos.down(j)).getBlock();
            // This is here so that the charcoal pile will collapse Bottom > Top
            // Because the pile scans Top > Bottom this is nessecary to avoid floating blocks
            if (block instanceof BlockLogPile) { return; }
        } while (block == Blocks.AIR || block instanceof BlockCharcoalPile);

        double logs = (double) countLogs();
        double log2 = 0.008d * logs * (logs + 42.5d) - 0.75d + 1.5d * Math.random();
        int charcoal = (int) Math.min(8, Math.max(0, Math.round(log2)));
        if (charcoal == 0)
        {
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            return;
        }
        if (j == 1)
        {
            // This log pile is at the bottom of the charcoal pit
            //noinspection ConstantConditions
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
                //noinspection ConstantConditions
                world.setBlockState(pos.down(k), BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, charcoal));
                world.setBlockState(pos, Blocks.AIR.getDefaultState());
                return;
            }

            if (state.getBlock() instanceof BlockCharcoalPile)
            {
                // Place what it can in the existing charcoal pit, then continue climbing
                charcoal += state.getValue(LAYERS);
                int toCreate = charcoal > 8 ? 8 : charcoal;
                //noinspection ConstantConditions
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
        //noinspection ConstantConditions
        world.setBlockState(pos, BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(LAYERS, charcoal));
    }
}
