/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.BarrelRecipe;
import net.dries007.tfc.api.recipes.LoomRecipe;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.network.PacketLoomUpdate;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.world.classic.CalendarTFC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class TELoom extends TEBase implements ITickable
{
    private Tree cachedWood;

    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress = 0;

    private LoomRecipe recipe = null;

    @Override
    public BlockLoom getBlockType()
    {
        Block block = super.getBlockType();
        if (!(block instanceof BlockLoom))
            throw new IllegalArgumentException("Block type is invalid; must be instance of BlockLoom");
        return ((BlockLoom) block);
    }

    @Nullable
    public Tree getWood()
    {
        if (cachedWood == null)
        {
            if (world == null)
                return null;
            cachedWood = getBlockType().wood;
        }
        return cachedWood;
    }

    public void onBreakBlock()
    {
        items.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        items.clear();
        ItemStackHelper.loadAllItems(compound.getCompoundTag("items"), items);
        progress = compound.getInteger("progress");
        if(!items.get(0).isEmpty())
        {
            recipe = LoomRecipe.get(items.get(0).getItem());
        }
        else if(!items.get(1).isEmpty())
        {
            recipe = LoomRecipe.getByOutput(items.get(1).getItem());
        }
        else recipe = null;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("items", ItemStackHelper.saveAllItems(new NBTTagCompound(), items));
        compound.setInteger("progress", progress);
        return compound;
    }

    public void onReceivePacket(long lastPushed)
    {
        this.lastPushed = lastPushed;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
        updateBlock();
    }

    public void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
    }

    private long lastPushed = 0L;

    @SideOnly(Side.CLIENT)
    public double getAnimPos()
    {
        int time = (int) (world.getTotalWorldTime() - lastPushed);
        if (time < 10)
            return java.lang.Math.sin((java.lang.Math.PI/20) * time) * 0.23125;
        else if (time < 20)
            return java.lang.Math.sin((java.lang.Math.PI/20) *(20 - time)) * 0.23125;
        return 0;
    }

    @SideOnly(Side.CLIENT)
    public String getAnimElement()
    {
        return (progress % 2 == 0)? "u" : "l";
    }

    private boolean needsUpdate = false;

    public boolean onRightClick(EntityPlayer player)
    {
        if(player.isSneaking())
        {
            if(recipe != null)
            {
                if(recipe.getInputCount() == items.get(0).getCount() && progress < recipe.getStepCount() && !needsUpdate)
                {
                    if (!world.isRemote)
                    {
                        long time = world.getTotalWorldTime() - lastPushed;
                        if (time < 20)
                            return true;
                        lastPushed = world.getTotalWorldTime();

                        needsUpdate = true;

                        TerraFirmaCraft.getNetwork().sendToDimension(new PacketLoomUpdate(this, lastPushed), world.provider.getDimension());
                    }
                    return true;
                }
            }
        }
        else {
            ItemStack heldItem = player.getHeldItem(EnumHand.MAIN_HAND);

            if (items.get(0).isEmpty() && items.get(1).isEmpty() && LoomRecipe.get(heldItem.getItem()) != null)
            {
                items.set(0, heldItem.copy());
                items.get(0).setCount(1);
                heldItem.shrink(1);
                recipe = LoomRecipe.get(items.get(0).getItem());

                updateBlock();
                return true;
            } else if (!items.get(0).isEmpty())
            {
                if (items.get(0).getItem() == heldItem.getItem() && recipe.getInputCount() > items.get(0).getCount())
                {
                    heldItem.shrink(1);
                    items.get(0).setCount(items.get(0).getCount() + 1);
                }

                updateBlock();
                return true;
            }

            if (!items.get(1).isEmpty())
            {
                player.addItemStackToInventory(items.get(1).copy());
                items.set(1, ItemStack.EMPTY);
                progress = 0;
                recipe = null;
                updateBlock();
                return true;
            }
        }
        return false;
    }

    @Override
    public void update()
    {
        if(recipe != null)
        {
            if(needsUpdate)
            {
                if (world.getTotalWorldTime() - lastPushed >= 20)
                {
                    needsUpdate = false;
                    progress++;

                    if (progress == recipe.getStepCount()) {
                        items.set(0, ItemStack.EMPTY);
                        items.set(1, new ItemStack(recipe.getOutputItem(), 1));
                    }
                    updateBlock();
                }
            }
        }
    }

    public int getMaxInputCount()
    {
        return (recipe == null)? 1 : recipe.getInputCount();
    }

    public int getCount()
    {
        return items.get(0).getCount();
    }

    public int getMaxProgress()
    {
        return (recipe == null)? 1 : recipe.getStepCount();
    }

    public int getProgress()
    {
        return progress;
    }

    public boolean hasRecipe()
    {
        return recipe != null;
    }

    public ResourceLocation getInProgressTexture()
    {
        return recipe.getInProgressTexture();
    }
}
