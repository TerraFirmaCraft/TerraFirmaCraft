/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.IFruitTree;

@ParametersAreNonnullByDefault
public class BlockFruitTreeBranch extends Block
{
    /* Facing of this branch */
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class);

    /* Connection sides
     * 0 = no connection
     * 1 = connected, use vertical model
     * 2 = connected=, use horizontal model */
    public static final PropertyInteger NORTH = PropertyInteger.create("north", 0, 2);
    public static final PropertyInteger EAST = PropertyInteger.create("east", 0, 2);
    public static final PropertyInteger SOUTH = PropertyInteger.create("south", 0, 2);
    public static final PropertyInteger WEST = PropertyInteger.create("west", 0, 2);
    public static final PropertyInteger UP = PropertyInteger.create("up", 0, 2);

    private static final AxisAlignedBB TRUNK_N_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 0.625D, 0.625D, 1.0D);
    private static final AxisAlignedBB TRUNK_E_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.625D, 0.625D, 0.625D);
    private static final AxisAlignedBB TRUNK_S_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.625D);
    private static final AxisAlignedBB TRUNK_W_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);


    private static final AxisAlignedBB TRUNK_U_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);

    private static final AxisAlignedBB CONNECTION_N_AABB = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.0D, 0.625D, 0.3125D);
    private static final AxisAlignedBB CONNECTION_S_AABB = new AxisAlignedBB(0.3125D, 0.375D, 0.6875D, 0.0D, 0.625D, 1.0D);
    private static final AxisAlignedBB CONNECTION_W_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.3125D, 0.625D, 0.6875D);
    private static final AxisAlignedBB CONNECTION_E_AABB = new AxisAlignedBB(0.6875D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);


    private static final Map<IFruitTree, BlockFruitTreeBranch> MAP = new HashMap<>();

    public static BlockFruitTreeBranch get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    private final IFruitTree tree;

    public BlockFruitTreeBranch(IFruitTree tree)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(2.0F);
        setHarvestLevel("axe", 0);
        setSoundType(SoundType.WOOD);
        this.tree = tree;
        Blocks.FIRE.setFireInfo(this, 5, 20);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(NORTH, 0).withProperty(EAST, 0).withProperty(SOUTH, 0).withProperty(WEST, 0).withProperty(UP, 0));
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
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        int connectedValue;
        EnumFacing face = getFacing(worldIn, pos);
        if (face == null || face == EnumFacing.UP || face == EnumFacing.DOWN)
        {
            // Vertical branch
            state = state.withProperty(FACING, EnumFacing.UP);
            connectedValue = 1;
        }
        else
        {
            // Horizontal branch
            state = state.withProperty(FACING, face);
            connectedValue = 2;
        }
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (worldIn.getBlockState(pos.offset(facing)).getBlock() instanceof BlockFruitTreeLeaves)
            {
                if (facing == EnumFacing.NORTH)
                {
                    state = state.withProperty(NORTH, connectedValue);
                }
                else if (facing == EnumFacing.SOUTH)
                {
                    state = state.withProperty(SOUTH, connectedValue);
                }
                else if (facing == EnumFacing.EAST)
                {
                    state = state.withProperty(EAST, connectedValue);
                }
                else if (facing == EnumFacing.WEST)
                {
                    state = state.withProperty(WEST, connectedValue);
                }
                else if (facing == EnumFacing.UP)
                {
                    state = state.withProperty(UP, connectedValue);
                }
            }
        }
        return state;
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        state = getActualState(state, source, pos);
        AxisAlignedBB finalAABB;
        switch (state.getValue(FACING))
        {
            case NORTH:
                finalAABB = TRUNK_N_AABB;
                break;
            case EAST:
                finalAABB = TRUNK_E_AABB;
                break;
            case SOUTH:
                finalAABB = TRUNK_S_AABB;
                break;
            case WEST:
                finalAABB = TRUNK_W_AABB;
                break;
            default:
                finalAABB = TRUNK_U_AABB;
        }
        if (state.getValue(NORTH) > 0)
        {
            finalAABB = finalAABB.union(CONNECTION_N_AABB);
        }
        if (state.getValue(EAST) > 0)
        {
            finalAABB = finalAABB.union(CONNECTION_E_AABB);
        }
        if (state.getValue(SOUTH) > 0)
        {
            finalAABB = finalAABB.union(CONNECTION_S_AABB);
        }
        if (state.getValue(WEST) > 0)
        {
            finalAABB = finalAABB.union(CONNECTION_W_AABB);
        }
        return finalAABB;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (getFacing(worldIn, pos) == null)
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.getItem().getToolClasses(stack).contains("axe") || stack.getItem().getToolClasses(stack).contains("saw"))
        {
            if (!worldIn.isRemote && RANDOM.nextBoolean())
            {
                ItemStack dropStack = new ItemStack(BlockFruitTreeSapling.get(tree));
                InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
            }
        }
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, NORTH, EAST, SOUTH, WEST, UP);
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
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(BlockFruitTreeSapling.get(tree));
    }

    @Nonnull
    public IFruitTree getTree()
    {
        return tree;
    }

    private EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos)
    {
        for (EnumFacing facing : EnumFacing.VALUES)
        {
            if (worldIn.getBlockState(pos.offset(facing)).getBlock() == BlockFruitTreeTrunk.get(tree))
            {
                return facing.getOpposite();
            }
        }
        return null;
    }
}
