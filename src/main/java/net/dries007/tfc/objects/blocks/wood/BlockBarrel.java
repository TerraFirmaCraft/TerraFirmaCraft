package net.dries007.tfc.objects.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEBarrel;

public class BlockBarrel extends Block implements ITileEntityProvider
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");
    private static final AxisAlignedBB bounds = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockBarrel()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return bounds;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }

            ItemStack heldItem = playerIn.getHeldItem(hand);

            TEBarrel te = (TEBarrel)worldIn.getTileEntity(pos);

            if (heldItem != ItemStack.EMPTY)
            {
                FluidUtil.interactWithFluidHandler(playerIn, hand, te.tank);
                te.markDirty();
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
            else if (playerIn.isSneaking())
            {
                te.sealed = !te.sealed;
                te.markDirty();
                worldIn.notifyBlockUpdate(pos, state, state, 3);

                return false;
            }
            else
            {
                playerIn.sendMessage(new TextComponentString("Content: " + te.tank.getFluidAmount()));
            }

            return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEBarrel();
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SEALED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }


    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return super.getActualState(state, worldIn, pos).withProperty(SEALED, ((TEBarrel)worldIn.getTileEntity(pos)).sealed);
    }
}
