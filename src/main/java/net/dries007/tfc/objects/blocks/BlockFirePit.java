/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.items.ItemFireStarter;

/**
 * todo: Needs more work in general
 */
public class BlockFirePit extends Block implements ITileEntityProvider
{
    public static final PropertyBool LIT = PropertyBool.create("lit");
    protected static final AxisAlignedBB AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.03125D, 0.9375D);

    public BlockFirePit()
    {
        super(Material.FIRE);
        setDefaultState(blockState.getBaseState().withProperty(LIT, false));
        disableStats();
        setTickRandomly(true);
        setLightLevel(1F);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LIT, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LIT) ? 1 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
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
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.isRainingAt(pos)) //todo
            worldIn.setBlockState(pos, state.withProperty(LIT, false), 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rng)
    {
        if (!state.getValue(LIT)) return;

        if (rng.nextInt(24) == 0)
        {
            world.playSound((double) ((float) pos.getX() + 0.5F), (double) ((float) pos.getY() + 0.5F), (double) ((float) pos.getZ() + 0.5F), SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rng.nextFloat(), rng.nextFloat() * 0.7F + 0.3F, false);
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.1;
        double z = pos.getZ() + 0.5;

        //todo: vary speed
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rng.nextFloat() - 0.5, y, z + rng.nextFloat() - 0.5, 0.0D, 0.2D, 0.0D);
        if (rng.nextFloat() > 0.75)
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + rng.nextFloat() - 0.5, y, z + rng.nextFloat() - 0.5, 0.0D, 0.1D, 0.0D);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canBePlacedOn(worldIn, pos.add(0, -1, 0)))
            worldIn.setBlockToAir(pos);
    }

    @Override
    public int tickRate(World worldIn)
    {
        return 30;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && canBePlacedOn(worldIn, pos.add(0, -1, 0));
    }

    // todo: override the fire stuff, see BlockPitKiln

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack held = playerIn.getHeldItem(hand);
        if (state.getValue(LIT)) //todo: remove debug feature
            worldIn.setBlockState(pos, state.withProperty(LIT, false));

        if (ItemFireStarter.canIgnite(held))
            worldIn.setBlockState(pos, state.withProperty(LIT, true));

        //todo: gui
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIT);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getValue(LIT);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return null; //todo
    }

    private boolean canBePlacedOn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP);
    }
}
