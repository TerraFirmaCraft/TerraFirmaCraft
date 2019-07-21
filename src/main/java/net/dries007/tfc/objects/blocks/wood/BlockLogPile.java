/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;


import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockLogPile extends Block implements ILightableBlock
{
    private static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, EnumFacing.Axis.X, EnumFacing.Axis.Z);

    public BlockLogPile()
    {
        super(Material.WOOD);

        setHardness(2.0F);
        setSoundType(SoundType.WOOD);
        setTickRandomly(true);
        setHarvestLevel("axe", 0);
        this.setDefaultState(this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AXIS, meta == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X).withProperty(LIT, meta >= 2);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(AXIS) == EnumFacing.Axis.Z ? 0 : 1) + (state.getValue(LIT) ? 2 : 0);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isRemote && state.getValue(LIT))
        {
            for (EnumFacing side : EnumFacing.values())
            {
                if (!isValidCoverBlock(worldIn, pos.offset(side)))
                {
                    worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
                }
                IBlockState state2 = worldIn.getBlockState(pos.offset(side));
                if (state2.getBlock() instanceof BlockLogPile)
                {
                    if (!state2.getValue(LIT))
                    {
                        worldIn.setBlockState(pos.offset(side), state2.withProperty(LIT, true));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + rand.nextFloat(), pos.getY() + 1, pos.getZ() + rand.nextFloat(),
                0f, 0.1f + 0.1f * rand.nextFloat(), 0f);
            if (rand.nextDouble() < 0.4D)
            {
                worldIn.playSound((double) pos.getX() + 0.5D, (double) pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 0.6F, false);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        TELogPile te = Helpers.getTE(world, pos, TELogPile.class);
        if (te != null)
        {
            // Special Interactions
            // 1. Try and put a log inside (happens on right click event)
            // 2. Try and light the TE
            // 3. Open the GUI
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() == Items.FLINT_AND_STEEL && !state.getValue(LIT) && side == EnumFacing.UP)
            {
                // Light the Pile
                if (world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos))
                {
                    if (!world.isRemote)
                    {
                        world.setBlockState(pos, state.withProperty(LIT, true));
                        te.light();
                        world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                    return true;
                }
            }

            if (!player.isSneaking() && !state.getValue(LIT))
            {
                if (!world.isRemote)
                {
                    TFCGuiHandler.openGui(world, pos, player, TFCGuiHandler.Type.LOG_PILE);

                }
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (placer.getHorizontalFacing().getAxis().isHorizontal())
        {
            return this.getDefaultState().withProperty(AXIS, placer.getHorizontalFacing().getAxis());
        }
        return this.getDefaultState();
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && te instanceof TEInventory)
        {
            ((TEInventory) te).onBreakBlock(worldIn, pos);
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS, LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TELogPile tile = Helpers.getTE(world, pos, TELogPile.class);
        if (tile != null)
        {
            return side == EnumFacing.DOWN || tile.countLogs() == 16;
        }
        return super.isSideSolid(state, world, pos, side);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 60;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 30;
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
        return new TELogPile();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.clear();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TELogPile te = Helpers.getTE(world, pos, TELogPile.class);
        if (te != null)
        {
            return te.getLog().copy();
        }
        return ItemStack.EMPTY;
    }

    private boolean isValidCoverBlock(World world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == BlocksTFC.LOG_PILE || state.getBlock() == BlocksTFC.CHARCOAL_PILE)
        {
            return true;
        }
        if (state.getMaterial().getCanBurn())
        {
            return false;
        }
        return state.isNormalCube();
    }

}
