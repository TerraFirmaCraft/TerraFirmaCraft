/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.te.TEWorldItem;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockWorldItem extends Block implements ITileEntityProvider
{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0.25D, 0D, 0.25D, 0.75D, 0.0625D, 0.75D);

    BlockWorldItem()
    {
        super(Material.CIRCUITS);
        setDefaultState(blockState.getBaseState());
        setHardness(0.1F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEWorldItem();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!worldIn.isSideSolid(pos.add(0, -1, 0), EnumFacing.UP))
            worldIn.setBlockToAir(pos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEWorldItem te = Helpers.getTE(worldIn, pos, TEWorldItem.class);
        if (te != null) te.onBreakBlock(pos.add(0.5D, 0.01D, 0.5D));
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEWorldItem te = Helpers.getTE(worldIn, pos, TEWorldItem.class);
        if (te != null && !worldIn.isRemote)
        {
            te.onBreakBlock(playerIn.getPosition());
            te.inventory.setStackInSlot(0, ItemStack.EMPTY); // Prevent it from dropping itself AGAIN
            worldIn.setBlockToAir(pos);
        }
        return true;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
    {
        // todo: add custom particles
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        // todo: add custom particles
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
    {
        // todo: add custom particles
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        // todo: add custom particles
        return true;
    }
}
