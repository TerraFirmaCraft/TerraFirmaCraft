/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nullable;

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
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.IFireable;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
public class TEPitKiln extends TileEntity implements ITickable
{
    public static final int STRAW_NEEDED = 8;
    public static final int WOOD_NEEDED = 8;

    private final NonNullList<ItemStack> logs = NonNullList.withSize(WOOD_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> straw = NonNullList.withSize(STRAW_NEEDED, ItemStack.EMPTY);
    private final NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int burnTicksToGo;

    @Override
    public void update()
    {
        if (burnTicksToGo > 0)
        {
            burnTicksToGo--;
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
                    burnTicksToGo = 0;
                    return;
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
                world.setBlockToAir(above);
                updateBlock();
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        burnTicksToGo = compound.getInteger("burnTicksToGo");
        ItemStackHelper.loadAllItems(compound.getCompoundTag("items"), items);
        ItemStackHelper.loadAllItems(compound.getCompoundTag("straw"), straw);
        ItemStackHelper.loadAllItems(compound.getCompoundTag("logs"), logs);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setLong("burnTicksToGo", burnTicksToGo);
        compound.setTag("items", ItemStackHelper.saveAllItems(new NBTTagCompound(), items));
        compound.setTag("straw", ItemStackHelper.saveAllItems(new NBTTagCompound(), straw));
        compound.setTag("logs", ItemStackHelper.saveAllItems(new NBTTagCompound(), logs));
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return new SPacketUpdateTileEntity(pos, 127, getUpdateTag());
    }

    @Override
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

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos(), getPos().add(1, 1, 1));
    }

    public boolean isLit()
    {
        return burnTicksToGo > 0;
    }

    public boolean hasFuel()
    {
        return !(logs.stream().anyMatch(ItemStack::isEmpty) || straw.stream().anyMatch(ItemStack::isEmpty));
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick
     */
    public boolean onRightClick(EntityPlayer player, ItemStack stack, boolean x, boolean z)
    {
        if (isLit())
        {
            return false;
        }
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);

        // Try and extract an item
        if (stack.isEmpty())
        {
            // This will search through the logs, then the straw
            ItemStack dropStack = logs.stream().filter(i -> !i.isEmpty()).findFirst().orElseGet(() ->
                straw.stream().filter(i -> !i.isEmpty()).findFirst().orElse(ItemStack.EMPTY));
            if (!dropStack.isEmpty())
            {
                player.addItemStackToInventory(dropStack.splitStack(1));
                updateBlock();
                return true;
            }
            else
            {
                // Try and grab the item
                ItemStack current = items.get(slot);
                if (current.isEmpty())
                {
                    return false;
                }
                player.addItemStackToInventory(current.splitStack(1));
                items.set(slot, ItemStack.EMPTY);
                updateBlock();
                if (items.stream().filter(ItemStack::isEmpty).count() == 4)
                {
                    world.setBlockToAir(pos);
                }
                return true;
            }
        }
        else
        {
            // Insert an item
            int strawCount = getStrawCount(), logCount = getLogCount();

            // Pottery
            if (IFireable.fromItem(stack.getItem()) != null)
            {
                if (strawCount == 0 && logCount == 0)
                {
                    if (items.get(slot).isEmpty())
                    {
                        items.set(slot, stack.splitStack(1));
                        updateBlock();
                        return true;
                    }
                }
                return false;
            }
            // Straw
            if (stack.getItem() == ItemsTFC.HAY && strawCount < STRAW_NEEDED)
            {
                addStraw(stack.splitStack(1));
                updateBlock();
                return true;
            }
            // Straw via thatch block
            if (stack.getItem() == Item.getItemFromBlock(BlocksTFC.THATCH) && strawCount <= STRAW_NEEDED - 4)
            {
                stack.shrink(1);
                addStraw(new ItemStack(ItemsTFC.HAY));
                addStraw(new ItemStack(ItemsTFC.HAY));
                addStraw(new ItemStack(ItemsTFC.HAY));
                addStraw(new ItemStack(ItemsTFC.HAY));
                updateBlock();
                return true;
            }
            // Only insert logs if all straw is inserted
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

    private void updateBlock()
    {
        IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3); // sync TE
        markDirty(); // make sure everything saves to disk
    }

    public void onBreakBlock()
    {
        items.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        straw.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
        logs.forEach(i -> InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), i));
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
        for (int i = 0; i < straw.size(); i++)
        {
            if (!straw.get(i).isEmpty()) continue;
            straw.set(i, stack);
            return;
        }
    }

    private void addLog(ItemStack stack)
    {
        for (int i = 0; i < logs.size(); i++)
        {
            if (!logs.get(i).isEmpty()) continue;
            logs.set(i, stack);
            return;
        }
    }
}
