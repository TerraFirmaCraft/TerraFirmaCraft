/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockFirePit extends Block implements IBellowsConsumerBlock, ILightableBlock
{
    private static final AxisAlignedBB AABB = new AxisAlignedBB(0, 0.0D, 0, 1, 0.125, 1);

    public BlockFirePit()
    {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(LIT, false));
        disableStats();
        setTickRandomly(true);
        setLightLevel(1F);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
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
    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return AABB;
    }

    @Override
    @Nonnull
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEFirePit tile = Helpers.getTE(worldIn, pos, TEFirePit.class);
        if (tile != null)
        {
            tile.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    @Nonnull
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
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            if (!state.getValue(LIT))
            {
                ItemStack held = player.getHeldItem(hand);
                if (ItemFireStarter.onIgnition(held))
                {
                    worldIn.setBlockState(pos, state.withProperty(LIT, true));
                    return true;
                }
            }
            if (!player.isSneaking())
            {
                TFCGuiHandler.openGui(worldIn, pos, player, TFCGuiHandler.Type.FIRE_PIT);
            }

        }
        return true;
    }

    @Override
    @Nonnull
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEFirePit();
    }

    @Override
    public boolean canIntakeFrom(TEBellows te, Vec3i offset, EnumFacing facing)
    {
        return offset.equals(TEBellows.OFFSET_LEVEL);
    }

    @Override
    public void onAirIntake(TEBellows te, World world, BlockPos pos, int airAmount)
    {
        TEFirePit teFirePit = Helpers.getTE(world, pos, TEFirePit.class);
        if (teFirePit != null)
        {
            teFirePit.onAirIntake(airAmount);
        }
    }

    private boolean canBePlacedOn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP);
    }
}
