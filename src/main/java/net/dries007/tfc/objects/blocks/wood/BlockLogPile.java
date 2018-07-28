/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;


import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
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
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.te.TELogPile;
import net.dries007.tfc.objects.te.TESidedInventory;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockLogPile extends Block implements ITileEntityProvider
{
    public static final PropertyBool ONFIRE = PropertyBool.create("onfire");
    private static final PropertyBool AXIS = PropertyBool.create("axis");

    public BlockLogPile()
    {
        super(Material.WOOD);

        setHardness(2.0F);
        setSoundType(SoundType.WOOD);
        setTickRandomly(true);
        setHarvestLevel("axe", 0);
        this.setDefaultState(this.getDefaultState().withProperty(AXIS, false).withProperty(ONFIRE, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TELogPile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(AXIS, meta == 0).withProperty(ONFIRE, meta >= 2);
    }

    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(AXIS) ? 0 : 1) + (state.getValue(ONFIRE) ? 2 : 0);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isRemote && state.getValue(ONFIRE))
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
                    if (!state2.getValue(ONFIRE))
                    {
                        worldIn.setBlockState(pos.offset(side), state2.withProperty(ONFIRE, true));
                    }
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(ONFIRE))
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
        if (!world.isRemote)
        {
            TELogPile te = (TELogPile) world.getTileEntity(pos);
            if (te == null) { return true; }

            // Special Interactions
            // 1. Try and put a log inside (happens on right click event)
            // 2. Try and light the TE
            // 3. Open the GUI
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() == Items.FLINT_AND_STEEL && !state.getValue(ONFIRE) && side == EnumFacing.UP)
            {
                // Light the Pile
                if (world.getBlockState(pos.up()).getBlock().isReplaceable(world, pos))
                {
                    world.setBlockState(pos, state.withProperty(ONFIRE, true));
                    te.light();
                    world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                    world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }

            if (!player.isSneaking() && !state.getValue(ONFIRE))
            {
                player.openGui(TerraFirmaCraft.getInstance(), TFCGuiHandler.LOG_PILE, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (placer.getHorizontalFacing().getAxis() == EnumFacing.Axis.Z)
        {
            return this.getDefaultState().withProperty(AXIS, true);
        }
        return this.getDefaultState();
    }

    @Override
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && te != null && te instanceof TESidedInventory)
        {
            ((TESidedInventory) te).onBreakBlock(worldIn, pos);
        }
        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS, ONFIRE);
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
        if (state.getBlock() instanceof BlockLogPile || state.getBlock() instanceof BlockCharcoalPile) { return true; }
        if (state.getMaterial().getCanBurn()) { return false; }
        return state.isNormalCube();
    }

}
