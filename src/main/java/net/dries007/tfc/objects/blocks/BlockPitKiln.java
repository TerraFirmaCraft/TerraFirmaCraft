package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockPitKiln extends Block implements ITileEntityProvider
{
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.03125D, 0.9375D);

    public BlockPitKiln()
    {
        super(Material.CIRCUITS);
        TileEntity.register(TEPitKiln.ID.toString(), TEPitKiln.class);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEPitKiln();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isSideSolid(pos.add(0, -1, 0), EnumFacing.UP))
            worldIn.destroyBlock(pos, true);
    }

    public boolean canLight(IBlockAccess world, BlockPos pos)
    {
        for (EnumFacing facing : EnumFacing.Plane.HORIZONTAL)
        {
            if (!world.isSideSolid(pos.offset(facing), facing.getOpposite(), false))
                return false;
        }

        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te == null || te.isLit()) return false;

        return te.hasFuel();
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        if (side != EnumFacing.UP)
            return false;
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te == null) return false;
        return te.isLit();
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te == null || !te.isLit()) return 0;
        return 120; // Twice as much as the highest vanilla level (60)
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return canLight(world, pos) ? 200 : 0; // Chance is x/300, so 200 = 2/3 chance to light.
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos)
    {
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te == null) return true;
        return te.isLit();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te == null) return true;
        te.onRightClick(playerIn, playerIn.getHeldItem(hand), hitX < 0.5, hitZ < 0.5);
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        if (side != EnumFacing.UP) return false;
        TEPitKiln te = Helpers.getTE(world, pos, TEPitKiln.class);
        if (te == null) return false;
        return te.hasFuel();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TEPitKiln te = Helpers.getTE(source, pos, TEPitKiln.class);
        if (te == null || te.hasFuel()) return FULL_BLOCK_AABB;
        return AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te == null || te.hasFuel()) return BlockFaceShape.SOLID;
        return BlockFaceShape.UNDEFINED;
    }
}
