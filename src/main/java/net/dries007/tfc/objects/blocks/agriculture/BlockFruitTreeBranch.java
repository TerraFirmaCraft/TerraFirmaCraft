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
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
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

    private static final AxisAlignedBB VERTICAL_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
    private static final AxisAlignedBB HORIZONTAL_Z_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625, 0.625, 1.0D);
    private static final AxisAlignedBB HORIZONTAL_X_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 1.0D, 0.625, 0.625);

    private static final Map<IFruitTree, BlockFruitTreeBranch> MAP = new HashMap<>();

    public static BlockFruitTreeBranch get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    public final IFruitTree tree;

    public BlockFruitTreeBranch(IFruitTree tree)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(2.0F);
        setHarvestLevel("axe", 0);
        setSoundType(SoundType.WOOD);
        this.tree = tree;
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
        EnumFacing face = getFacing(worldIn, pos);
        return face != null ? state.withProperty(FACING, face) : state.withProperty(FACING, EnumFacing.UP);
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
        EnumFacing face = getFacing(source, pos);
        if (face == null) return VERTICAL_AABB;
        switch (face)
        {
            case NORTH:
            case SOUTH:
                return HORIZONTAL_Z_AABB;
            case EAST:
            case WEST:
                return HORIZONTAL_X_AABB;
            default:
                return VERTICAL_AABB;
        }
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
        return rand.nextInt(10) > 5 - fortune ? Item.getItemFromBlock(BlockFruitTreeSapling.get(tree)) : Items.AIR;
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
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
