/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

/**
 * Barrel block. Can be filled with fluids (10 B), and one item stack. Performs barrel recipes.
 * Sealed state is stored in block state and cached in TE, synced when updated via custom packet
 *
 * @see TEBarrel
 * @see BarrelRecipe
 */
@ParametersAreNonnullByDefault
public class BlockBarrel extends Block implements IItemSize
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    /**
     * Used to toggle the barrel seal state and update the tile entity, in the correct order
     */
    public static void toggleBarrelSeal(World world, BlockPos pos)
    {
        TEBarrel tile = Helpers.getTE(world, pos, TEBarrel.class);
        if (tile != null)
        {
            IBlockState state = world.getBlockState(pos);
            boolean previousSealed = state.getValue(SEALED);
            world.setBlockState(pos, state.withProperty(SEALED, !previousSealed));
            if (previousSealed)
            {
                tile.onUnseal();
            }
            else
            {
                tile.onSealed();
            }
        }
    }

    public BlockBarrel()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHardness(2F);
        setDefaultState(blockState.getBaseState().withProperty(SEALED, false));
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return stack.getTagCompound() == null ? Size.VERY_LARGE : Size.HUGE; // Causes overburden if sealed
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return stack.getTagCompound() == null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(SEALED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(SEALED) ? 1 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
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

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
    {
        if (!world.isRemote)
        {
            boolean powered = world.isBlockPowered(pos);
            if (powered || block.getDefaultState().canProvidePower())
            {
                if (powered != state.getValue(SEALED))
                {
                    toggleBarrelSeal(world, pos);
                }
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEBarrel tile = Helpers.getTE(worldIn, pos, TEBarrel.class);
        if (tile != null)
        {
            tile.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        TEBarrel te = Helpers.getTE(worldIn, pos, TEBarrel.class);
        if (te != null)
        {
            if (heldItem.isEmpty() && playerIn.isSneaking())
            {
                worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 0.85F);
                toggleBarrelSeal(worldIn, pos);
                return true;
            }
            else if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) && !state.getValue(SEALED))
            {
                IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (fluidHandler != null)
                {
                    if (!worldIn.isRemote)
                    {
                        FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
                        te.markDirty();
                    }
                    return true;
                }
            }
            else
            {
                if (!worldIn.isRemote)
                {
                    TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.BARREL);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (!worldIn.isRemote && stack.getTagCompound() != null)
        {
            TEBarrel te = Helpers.getTE(worldIn, pos, TEBarrel.class);
            if (te != null)
            {
                worldIn.setBlockState(pos, state.withProperty(SEALED, true));
                te.loadFromItemStack(stack);
            }
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SEALED);
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
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
        return new TEBarrel();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (!state.getValue(SEALED))
        {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        // Unseal the vessel if an explosion destroys it, so it drops it's contents
        world.setBlockState(pos, world.getBlockState(pos).withProperty(SEALED, false));
        super.onBlockExploded(world, pos, explosion);
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world,
                                  BlockPos pos, EntityPlayer player)
    {
        ItemStack stack = new ItemStack(state.getBlock());
        TEBarrel tile = Helpers.getTE(world, pos, TEBarrel.class);
        if (tile != null && tile.isSealed())
        {
            tile.saveToItemStack(stack);
        }
        return stack;
    }
}
