/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEChestTFC extends TEInventory implements ITickable, IInventory
{
    public static final int SIZE = 18;

    //copy paste from TileEntityChest, handles the open/close chest animation
    public float lidAngle, prevLidAngle;
    private int numPlayersUsing;
    private int ticksSinceSync;
    private int connectedTo;
    private boolean priority; //used to control which one is the first tile entity(eg: which one takes the first two lines of items)

    private Tree cachedWood;

    public TEChestTFC()
    {
        super(SIZE);
        connectedTo = 0;
        priority = true;
        lidAngle = 0;
        prevLidAngle = 0;
    }

    @Nullable
    public EnumFacing getConnection()
    {
        if (connectedTo <= 0 || connectedTo > 4) return null;
        return EnumFacing.byIndex(connectedTo + 1);
    }

    public void setConnectedTo(EnumFacing facing)
    {
        TEChestTFC connecting = Helpers.getTE(this.world, pos.offset(facing), TEChestTFC.class);
        if (connecting != null)
        {
            this.connectedTo = facing.getIndex() - 1;
            this.priority = false;
            connecting.priority = true;
        }
        else
        {
            this.connectedTo = 0;
        }
    }

    public TEChestTFC getPriorityTE()
    {
        if (priority || getConnection() == null) return this;
        TEChestTFC connecting = Helpers.getTE(this.world, pos.offset(getConnection()), TEChestTFC.class);
        if (connecting != null && connecting.priority)
        {
            return connecting;
        }
        return this;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        connectedTo = nbt.getInteger("connection");
        priority = nbt.getBoolean("priority");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("connection", connectedTo);
        nbt.setBoolean("priority", priority);
        return super.writeToNBT(nbt);
    }

    @Override
    public String getName()
    {
        return "container.chest";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public int getSizeInventory()
    {
        return this.getConnection() == null ? SIZE : SIZE * 2;
    }

    @Override
    public boolean isEmpty()
    {
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            if (!this.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index)
    {
        if (index < SIZE)
        {
            return this.inventory.getStackInSlot(index);
        }
        else if (getConnection() != null)
        {
            index -= SIZE;
            TEChestTFC te = Helpers.getTE(this.world, pos.offset(getConnection()), TEChestTFC.class);
            if (te != null)
            {
                return te.getStackInSlot(index);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack decrStackSize(int index, int count)
    {
        if (index < SIZE)
        {
            return this.inventory.extractItem(index, count, false);
        }
        else if (getConnection() != null)
        {
            index -= SIZE;
            TEChestTFC te = Helpers.getTE(this.world, pos.offset(getConnection()), TEChestTFC.class);
            if (te != null)
            {
                return te.decrStackSize(index, count);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index)
    {
        if (index < SIZE)
        {
            return this.inventory.extractItem(index, getInventoryStackLimit(), false);
        }
        else if (getConnection() != null)
        {
            index -= SIZE;
            TEChestTFC te = Helpers.getTE(this.world, pos.offset(getConnection()), TEChestTFC.class);
            if (te != null)
            {
                return te.removeStackFromSlot(index);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack)
    {
        if (index < SIZE)
        {
            this.inventory.setStackInSlot(index, stack);
        }
        else if (getConnection() != null)
        {
            index -= SIZE;
            TEChestTFC te = Helpers.getTE(this.world, pos.offset(getConnection()), TEChestTFC.class);
            if (te != null)
            {
                te.setInventorySlotContents(index, stack);
            }
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player)
    {
        if (this.world.getTileEntity(this.pos) != this)
        {
            return false;
        }
        else
        {
            return player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player)
    {
        ++this.numPlayersUsing;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
        if (this.isTrapChest())
        {
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
        }
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
        --this.numPlayersUsing;
        this.world.addBlockEvent(pos, this.getBlockType(), 1, this.numPlayersUsing);
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
        if (this.isTrapChest())
        {
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), false);
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < this.inventory.getSlots(); i++)
        {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == 1)
        {
            this.numPlayersUsing = type;
            return true;
        }
        else
        {
            return super.receiveClientEvent(id, type);
        }
    }

    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentTranslation(this.getName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(2, 2, 2));
    }

    @Nullable
    public Tree getWood()
    {
        if (cachedWood == null)
        {
            if (world != null)
            {
                cachedWood = ((BlockChestTFC) world.getBlockState(pos).getBlock()).wood;
            }
        }
        return cachedWood;
    }

    @Override
    public void update()
    {
        ++this.ticksSinceSync;
        if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            this.numPlayersUsing = 0;

            for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double) ((float) pos.getX() - 5.0F), (double) ((float) pos.getY() - 5.0F), (double) ((float) pos.getZ() - 5.0F), (double) ((float) (pos.getX() + 1) + 5.0F), (double) ((float) (pos.getY() + 1) + 5.0F), (double) ((float) (pos.getZ() + 1) + 5.0F))))
            {
                if (entityplayer.openContainer instanceof ContainerChest)
                {
                    IInventory iinventory = ((ContainerChest) entityplayer.openContainer).getLowerChestInventory();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this))
                    {
                        ++this.numPlayersUsing;
                    }
                }
            }
        }
        this.prevLidAngle = this.lidAngle;

        if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F)
        {
            double d1 = (double) pos.getX() + 0.5D;
            double d2 = (double) pos.getZ() + 0.5D;
            this.world.playSound(null, d1, (double) pos.getY() + 0.5D, d2, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F)
        {
            float f2 = this.lidAngle;

            if (this.numPlayersUsing > 0)
            {
                this.lidAngle += 0.1F;
            }
            else
            {
                this.lidAngle -= 0.1F;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            if (this.lidAngle < 0.5F && f2 >= 0.5F)
            {
                double d3 = (double) pos.getX() + 0.5D;
                double d0 = (double) pos.getZ() + 0.5D;
                this.world.playSound(null, d3, (double) pos.getY() + 0.5D, d0, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
    }

    public boolean isTrapChest()
    {
        if (!this.hasWorld()) return false;
        return ((BlockChestTFC) world.getBlockState(pos).getBlock()).type == BlockChest.Type.TRAP;
    }
}
