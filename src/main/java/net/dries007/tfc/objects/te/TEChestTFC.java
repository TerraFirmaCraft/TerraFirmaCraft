package net.dries007.tfc.objects.te;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;

import static net.dries007.tfc.Constants.MOD_ID;

public class TEChestTFC extends TileEntityChest
{
    public static final ResourceLocation ID = new ResourceLocation(MOD_ID, "chest");
    public static final int SIZE = 18;

    private Wood cachedWood;

    {
        chestContents = NonNullList.withSize(SIZE, ItemStack.EMPTY); // todo: make chest size configurable.
    }

    @Override
    public BlockChestTFC getBlockType()
    {
        Block block = super.getBlockType();
        return block instanceof BlockChestTFC ? ((BlockChestTFC) block) : null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(2, 2, 2));
    }

    public Wood getWood()
    {
        if (cachedWood == null)
        {
            if (world == null || getBlockType() == null) return Wood.OAK;
            cachedWood = getBlockType().wood;
        }
        return cachedWood;
    }

    @Override
    public int getSizeInventory()
    {
        return SIZE;
    }

    @Override
    protected boolean isChestAt(BlockPos posIn)
    {
        if (world == null) return false;

        Block block = this.world.getBlockState(posIn).getBlock();
        return block instanceof BlockChestTFC && ((BlockChestTFC) block).wood == getWood() && ((BlockChest) block).chestType == getChestType();
    }
}
