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
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.pathfinding.PathNodeType;
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
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.blocks.BlockPlacedItem.PLACED_ITEM_AABB;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockPitKiln extends Block implements ILightableBlock
{
    public static final PropertyBool FULL = PropertyBool.create("full");

    private static final AxisAlignedBB[] AABB_LEVELS = new AxisAlignedBB[] {
        PLACED_ITEM_AABB,
        new AxisAlignedBB(0, 0, 0, 1, 0.0625, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.125, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.1875, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.25, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.3125, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.375, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.4375, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.5, 1),
        new AxisAlignedBB(0, 0, 0, 1, 0.75, 1),
        FULL_BLOCK_AABB
    };

    public BlockPitKiln()
    {
        super(Material.CIRCUITS);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty(FULL, false).withProperty(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        return state.getValue(FULL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LIT, (meta & 1) > 0).withProperty(FULL, (meta & 2) > 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(LIT) ? 1 : 0) + (state.getValue(FULL) ? 2 : 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return true;
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
        TEPitKiln tile = Helpers.getTE(source, pos, TEPitKiln.class);
        if (tile != null)
        {
            int height = tile.getStrawCount();
            if (tile.getLogCount() > 4)
            {
                height = 10; // Full block
            }
            else if (tile.getLogCount() > 0)
            {
                height = 9; // 75% of block
            }
            return AABB_LEVELS[height];
        }
        return PLACED_ITEM_AABB;
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
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            if (blockIn == Blocks.FIRE)
            {
                te.tryLight();
            }
            if (!worldIn.isSideSolid(pos.down(), EnumFacing.UP))
            {
                if (te.isLit())
                {
                    te.emptyFuelContents();
                }
                worldIn.destroyBlock(pos, true);
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            te.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            // Skip interacting if using a fire starter (wait for fire in #neighborChanged)
            if (ItemFireStarter.canIgnite(playerIn.getHeldItem(hand)))
            {
                return false;
            }
            return te.onRightClick(playerIn, playerIn.getHeldItem(hand), hitX < 0.5, hitZ < 0.5);
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FULL, LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return state.getActualState(world, pos).getValue(FULL);
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT) ? 120 : 0; // Twice as much as the highest vanilla level (60)
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
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
        return new TEPitKiln();
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer worldObj, BlockPos blockPosition, IBlockState iblockstate, EntityLivingBase entity, int numberOfParticles)
    {
        return true;
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager)
    {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager)
    {
        return true;
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable EntityLiving entity)
    {
        return state.getValue(LIT) && (entity == null || !entity.isImmuneToFire()) ? net.minecraft.pathfinding.PathNodeType.DAMAGE_FIRE : null;
    }
}