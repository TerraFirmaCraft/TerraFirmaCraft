/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.util.OreDictionaryHelper;

import static net.dries007.tfc.objects.blocks.devices.BlockCharcoalForge.LIT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TEBloomery extends TileEntity implements ITickable
{
    private static int MAX_BRONZE_RINGS = 12; //Defines max height of the bloomery where 3 rings = 1 block
    private static int MIN_BRONZE_RINGS = 6; //Defines min height of the bloomery

    private static int CHARCOAL_PER_HEIGHT = 8; //Excluding first block(output)
    private static int ORE_PER_HEIGHT = 8; //Excluding first block(output)

    private int stage; //Controls which construction stage we are.
    private int height; //Defines max ore and fuel count;
    private int fuel;
    private int oreCount;
    private int burnTicksRemaining;


    public TEBloomery()
    {
        stage = 1; //It is always created with first set of clay
        height = 0;
        fuel = 0;
        oreCount = 0;
        burnTicksRemaining = 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        stage = tag.getInteger("stage");
        height = tag.getInteger("height");
        fuel = tag.getInteger("fuel");
        oreCount = tag.getInteger("oreCount");
        burnTicksRemaining = tag.getInteger("burnTicksRemaining");
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setInteger("stage", stage);
        tag.setInteger("height", height);
        tag.setInteger("fuel", fuel);
        tag.setInteger("oreCount", oreCount);
        tag.setInteger("burnTicksRemaining", burnTicksRemaining);
        return super.writeToNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 1024.0D;
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet)
    {
        this.handleUpdateTag(packet.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        readFromNBT(tag);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1D, 1D, 1D));
    }

    private final NonNullList<ItemStack> bronzeRings = NonNullList.withSize(MAX_BRONZE_RINGS, ItemStack.EMPTY);
    private final NonNullList<ItemStack> oreItem = NonNullList.withSize(ORE_PER_HEIGHT * (MAX_BRONZE_RINGS - 3) / 3, ItemStack.EMPTY);

    public void onBreakBlock()
    {
        //TODO: Spawns ore and charcoal back to world? Bronze rings and clay?
    }

    public boolean isLit()
    {
        return burnTicksRemaining > 0;
    }

    private void addBronzeRing(ItemStack stack)
    {
        for (int i = 0; i < bronzeRings.size(); i++)
        {
            if (bronzeRings.get(i).isEmpty())
            {
                bronzeRings.set(i, stack);
                return;
            }
        }
    }

    private void addOre(ItemStack stack)
    {
        for (int i = 0; i < oreItem.size(); i++)
        {
            if (oreItem.get(i).isEmpty())
            {
                oreItem.set(i, stack);
                return;
            }
        }
    }

    private void updateBlock()
    {
        //Update each block
        for(int i = 0; i<=height;i++)
        {
            BlockPos bPos = pos.add(0, i, 0);
            IBlockState state = world.getBlockState(bPos);
            if(stage % 12 > 0 && state.getMaterial() == Material.AIR){
                world.setBlockState(bPos, BlocksTFC.BLOOMERY_STRUCTURE.getDefaultState());
            }
            world.notifyBlockUpdate(bPos, state, state, 3);
        }
        markDirty(); // make sure everything saves to disk
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(EntityPlayer player, ItemStack stack)
    {
        debug();
        if (isLit())
        {
            return false;
        }

        //Is it under construction?
        if(stage < 4 * MAX_BRONZE_RINGS)
        {
            //Time to place a bronze ring

            if(stage % 4 == 3)
            {
                if(stack.isEmpty())return false;
                //TODO: Change this line to use metal rings
                if(!OreDictionaryHelper.doesStackMatchOre(stack, "sheetBronze"))return false;
                addBronzeRing(stack.splitStack(1));
                stage++;
                height = stage / 12; //Update height
                updateBlock();
                return true;
            }else{
                if(stack.isEmpty())return false;
                if(stack.getItem() == Items.CLAY_BALL)
                {
                    stack.shrink(1);
                    stage++;
                    updateBlock();
                    return true;
                }else if(stack.getItem() == Items.COAL
                         && stage % 4 == 0 && stage > MIN_BRONZE_RINGS * 4){
                    //User wants to finish contruction early
                    stage = 4 * MAX_BRONZE_RINGS;
                }else{
                    return false;
                }
            }
        }
        //Time to fire up the structure for the first time
        if (stage == 4 * MAX_BRONZE_RINGS){
            if(fuel < (height - 1) * CHARCOAL_PER_HEIGHT)
            {
                if (stack.getItem() != Items.COAL) return false;
                stack.shrink(1);
                fuel++;
                updateBlock();
                return true;
            }
        }else{
            //Finally, the bloomery is constructed, the user can now place ores and charcoal
            if (stack.getItem() == Items.COAL) {
                if(fuel >= (height - 1) * CHARCOAL_PER_HEIGHT)return false;
                stack.shrink(1);
                fuel++;
                updateBlock();
                return true;
            }
            //TODO: Check all ores
            if (OreDictionaryHelper.doesStackMatchOre(stack, "oreLimonite")) {
                if(oreCount >= (height - 1) * ORE_PER_HEIGHT)return false;
                addOre(stack.splitStack(1));
                oreCount++;
                updateBlock();
                return true;
            }
        }
        ItemStack held = player.getHeldItemMainhand();
        if (ItemFireStarter.canIgnite(held)){
            burnTicksRemaining = 600;
            return true;
        }
        return false;
    }

    public void debug()
    {
        TerraFirmaCraft.getLog().warn("Debugging Bloomery:");
        TerraFirmaCraft.getLog().warn("Stage {} | Fuel {} | Remaining Ticks {}", stage, fuel, burnTicksRemaining);
    }

    @Override
    public void update()
    {
        if (isLit())
        {
            burnTicksRemaining--;
            if(burnTicksRemaining <= 0){
                if (stage == 4 * MAX_BRONZE_RINGS){
                    //Finish structure construction
                    stage++;
                }else{
                    //TODO: Melt ores into bloom
                }
            }
        }
    }

    public int getHeight() { return height; }

    public int getStage() { return stage; }
}
