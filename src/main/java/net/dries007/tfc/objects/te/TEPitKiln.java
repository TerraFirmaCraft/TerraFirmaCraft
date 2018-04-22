package net.dries007.tfc.objects.te;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.wood.ItemLogTFC;
import net.dries007.tfc.util.IFireable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static net.dries007.tfc.Constants.MOD_ID;

public class TEPitKiln extends TileEntity implements ITickable
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "pit_kiln");

    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;

    private int burnTicksToGo;
    private final NonNullList<ItemStack> logs = NonNullList.withSize(WOOD_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> straw = NonNullList.withSize(STRAW_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    @Override
    public void update()
    {
        if (burnTicksToGo > 0)
        {
            burnTicksToGo --;
            if (hasWorld())
            {
                BlockPos above = getPos().add(0, 1, 0);
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
                    }
                }
            }
            if (burnTicksToGo == 0)
            {
                straw.clear();
                logs.clear();
                for (int i = 0; i < items.size(); i++)
                {
                    ItemStack stack = items.get(i);
                    Item item = stack.getItem();
                    if (!(item instanceof IFireable)) continue;
                    IFireable fireable = ((IFireable) item);
                    items.set(i, fireable.getFiringResult(stack, Metal.Tier.TIER_I));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setLong("burnTicksToGo", burnTicksToGo);

        {
            NBTTagList itemsNBT = new NBTTagList();
            items.forEach(i -> itemsNBT.appendTag(i.writeToNBT(new NBTTagCompound())));
            compound.setTag("items", itemsNBT);
        }
        {
            NBTTagList strawNBT = new NBTTagList();
            straw.forEach(i -> strawNBT.appendTag(i.writeToNBT(new NBTTagCompound())));
            compound.setTag("straw", strawNBT);
        }
        {
            NBTTagList logsNBT = new NBTTagList();
            logs.forEach(i -> logsNBT.appendTag(i.writeToNBT(new NBTTagCompound())));
            compound.setTag("logs", logsNBT);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        burnTicksToGo = compound.getInteger("burnTicksToGo");
        {
            NBTTagList itemsNBT = compound.getTagList("items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemsNBT.tagCount(); i++) items.set(i, new ItemStack(itemsNBT.getCompoundTagAt(i)));
        }
        {
            NBTTagList strawNBT = compound.getTagList("straw", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < strawNBT.tagCount(); i++) straw.set(i, new ItemStack(strawNBT.getCompoundTagAt(i)));
        }
        {
            NBTTagList logsNBT = compound.getTagList("logs", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < logsNBT.tagCount(); i++) logs.set(i, new ItemStack(logsNBT.getCompoundTagAt(i)));
        }
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.getNbtCompound());
    }

    public boolean isLit()
    {
        return burnTicksToGo > 0;
    }

    public boolean hasFuel()
    {
        return !(logs.stream().anyMatch(ItemStack::isEmpty) || straw.stream().anyMatch(ItemStack::isEmpty));
    }

    public boolean addItem(ItemStack heldItem, boolean x, boolean z)
    {
        int slot = 0;
        if (x) slot += 1;
        if (z) slot += 2;
        ItemStack current = items.get(slot);
        if (!current.isEmpty()) return false;
        items.set(slot, heldItem.splitStack(1));
        markDirty();
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        return false;
    }

    @SuppressWarnings("ConstantConditions")
    public void onRightClick(EntityPlayer player, ItemStack item, boolean x, boolean z)
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        markDirty();
        int count = getStrawCount();
        if (item.isEmpty())
        {
            if (getLogCount() > 0)
            {
                ItemStack itemStack = logs.stream().filter(i -> !i.isEmpty()).findFirst().get();
                player.addItemStackToInventory(itemStack.splitStack(1));
                return;
            }
            if (getStrawCount() > 0)
            {
                ItemStack itemStack = straw.stream().filter(i -> !i.isEmpty()).findFirst().get();
                player.addItemStackToInventory(itemStack.splitStack(1));
                return;
            }
            int slot = 0;
            if (x) slot += 1;
            if (z) slot += 2;
            ItemStack current = items.get(slot);
            if (!current.isEmpty())
            {
                player.addItemStackToInventory(current.splitStack(1));
                items.set(slot, ItemStack.EMPTY);
            }
            if (items.stream().filter(ItemStack::isEmpty).count() == 4)
                world.destroyBlock(pos, true);
        }
        if (item.getItem() == ItemsTFC.HAY && count < STRAW_NEEDED)
        {
            addStraw(item.splitStack(1));
            return;
        }
        if (item.getItem() == Item.getItemFromBlock(BlocksTFC.THATCH) && count <= STRAW_NEEDED - 4)
        {
            item.shrink(1);
            addStraw(new ItemStack(ItemsTFC.HAY));
            addStraw(new ItemStack(ItemsTFC.HAY));
            addStraw(new ItemStack(ItemsTFC.HAY));
            addStraw(new ItemStack(ItemsTFC.HAY));
            return;
        }
        if (count < STRAW_NEEDED) return;
        count = getLogCount();
        if (item.getItem() instanceof ItemLogTFC && count < WOOD_NEEDED)
        {
            addLog(item.splitStack(1));
            return;
        }
        TerraFirmaCraft.getLog().info("Fire!");
        burnTicksToGo = 600;
    }

    private void addStraw(ItemStack stack)
    {
        TerraFirmaCraft.getLog().info("addStraw {}", stack);
        for (int i = 0; i < straw.size(); i++)
        {
            if (!straw.get(i).isEmpty()) continue;
            straw.set(i, stack);
            return;
        }
    }

    private void addLog(ItemStack stack)
    {
        TerraFirmaCraft.getLog().info("addLog {}", stack);
        for (int i = 0; i < logs.size(); i++)
        {
            if (!logs.get(i).isEmpty()) continue;
            logs.set(i, stack);
            return;
        }
    }

    public NonNullList<ItemStack> getItems()
    {
        return items;
    }

    public int getLogCount()
    {
        return (int) logs.stream().filter(i -> !i.isEmpty()).count();
    }

    public int getStrawCount()
    {
        return (int) straw.stream().filter(i -> !i.isEmpty()).count();
    }
}
