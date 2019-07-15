/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.fruittrees;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class BlockFruitTreeTrunk extends Block
{
    /* Connection sides (used if there's a branch on facing) */
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");
    private static final Map<IFruitTree, BlockFruitTreeTrunk> MAP = new HashMap<>();
    private static final AxisAlignedBB TRUNK_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);

    public static BlockFruitTreeTrunk get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    private final IFruitTree tree;

    public BlockFruitTreeTrunk(IFruitTree tree)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(2.0F);
        setTickRandomly(true);
        setHarvestLevel("axe", 0);
        setSoundType(SoundType.WOOD);
        this.tree = tree;
        setDefaultState(blockState.getBaseState().withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false));
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
        for (EnumFacing face : EnumFacing.HORIZONTALS)
        {
            if (worldIn.getBlockState(pos.offset(face)).getBlock() == BlockFruitTreeBranch.get(tree))
            {
                if (face == EnumFacing.NORTH)
                {
                    state = state.withProperty(NORTH, true);
                }
                else if (face == EnumFacing.SOUTH)
                {
                    state = state.withProperty(SOUTH, true);
                }
                else if (face == EnumFacing.EAST)
                {
                    state = state.withProperty(EAST, true);
                }
                else
                {
                    state = state.withProperty(WEST, true);
                }
            }
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TRUNK_AABB;
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
    public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, TRUNK_AABB);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(worldIn, pos, state, random);
        if (!worldIn.isRemote)
        {
            // Attempt to grow
            float temp = ClimateTFC.getTemp(worldIn, pos);
            float rainfall = ChunkDataTFC.getRainfall(worldIn, pos);
            TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
            if (te != null)
            {
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > tree.getGrowthTime() && tree.isValidForGrowth(temp, rainfall))
                {
                    if (this.getTrunkHeight(worldIn, pos) < 4)
                    {
                        if (worldIn.getBlockState(pos.up()).getMaterial().isReplaceable() || worldIn.getBlockState(pos.up()).getBlock() == BlockFruitTreeLeaves.get(tree))
                        {
                            worldIn.setBlockState(pos.up(), this.getDefaultState());
                        }
                        if (worldIn.getBlockState(pos.up(2)).getMaterial().isReplaceable())
                        {
                            worldIn.setBlockState(pos.up(2), BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, false));
                        }
                    }
                    else
                    {
                        for (EnumFacing branchFacing : EnumFacing.VALUES)
                        {
                            if (branchFacing == EnumFacing.DOWN) continue;
                            BlockPos branchPos = pos.offset(branchFacing);
                            if (worldIn.getBlockState(branchPos).getMaterial().isReplaceable() || worldIn.getBlockState(branchPos).getBlock() == BlockFruitTreeLeaves.get(tree))
                            {
                                worldIn.setBlockState(branchPos, BlockFruitTreeBranch.get(tree).getDefaultState());
                            }
                            for (BlockPos leafPos : BlockPos.getAllInBoxMutable(branchPos.add(-1, 0, -1), branchPos.add(1, 0, 1)))
                            {
                                if (leafPos.equals(branchPos)) continue;
                                if (worldIn.getBlockState(leafPos).getMaterial().isReplaceable())
                                {
                                    worldIn.setBlockState(leafPos, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                }
                            }
                            if (branchFacing == EnumFacing.UP)
                            {
                                if (worldIn.getBlockState(branchPos.up()).getMaterial().isReplaceable())
                                {
                                    worldIn.setBlockState(branchPos.up(), BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                }
                            }
                        }
                    }
                    te.resetCounter();
                }
            }
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, NORTH, SOUTH, EAST, WEST);
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
        return new TETickCounter();
    }

    public int getTrunkHeight(World world, BlockPos pos)
    {
        for (int i = 1; i < 4; i++)
        {
            if (world.getBlockState(pos.down(i)).getBlock() != this) return i;
        }
        return 4;
    }
}
