/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.*;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateTFC;
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
    private static final AxisAlignedBB CONNECTION_N_AABB = new AxisAlignedBB(0.3125D, 0.375D, 0.0D, 0.0D, 0.625D, 0.3125D);
    private static final AxisAlignedBB CONNECTION_S_AABB = new AxisAlignedBB(0.3125D, 0.375D, 0.6875D, 0.0D, 0.625D, 1.0D);
    private static final AxisAlignedBB CONNECTION_W_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.3125D, 0.3125D, 0.625D, 0.6875D);
    private static final AxisAlignedBB CONNECTION_E_AABB = new AxisAlignedBB(0.6875D, 0.375D, 0.3125D, 1.0D, 0.625D, 0.6875D);


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
        Blocks.FIRE.setFireInfo(this, 5, 20);
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
        state = getActualState(state, source, pos);
        AxisAlignedBB finalAABB = TRUNK_AABB;
        if (state.getValue(NORTH))
        {
            finalAABB = finalAABB.union(CONNECTION_N_AABB);
        }
        if (state.getValue(SOUTH))
        {
            finalAABB = finalAABB.union(CONNECTION_S_AABB);
        }
        if (state.getValue(WEST))
        {
            finalAABB = finalAABB.union(CONNECTION_W_AABB);
        }
        if (state.getValue(EAST))
        {
            finalAABB = finalAABB.union(CONNECTION_E_AABB);
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
            float temp = ClimateTFC.getActualTemp(worldIn, pos);
            float rainfall = ChunkDataTFC.getRainfall(worldIn, pos);
            TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
            if (te != null)
            {
                long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                if (hours > (tree.getGrowthTime() * ConfigTFC.General.FOOD.fruitTreeGrowthTimeModifier) && tree.isValidForGrowth(temp, rainfall))
                {
                    te.resetCounter();
                    if (worldIn.getBlockState(pos.up()).getBlock() != this) //If the above block is a trunk, this one don't need to do anything
                    {
                        if (this.getTrunkHeight(worldIn, pos) < 4)
                        {
                            BlockPos missingLeaf = getMissingLeaf(worldIn, pos);
                            if (missingLeaf != null)
                            {
                                //Missing leaf, spawn that first
                                if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                {
                                    worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, false));
                                }
                            }
                            else
                            {
                                //Time to grow one trunk
                                worldIn.setBlockState(pos.up(), this.getDefaultState());
                                //Set all remaining leaf blocks to air
                                levelUpLeaves(worldIn, pos, false);
                            }
                        }
                        else
                        {
                            //Starting with top
                            if (worldIn.getBlockState(pos.up()).getBlock() != BlockFruitTreeBranch.get(tree))
                            {
                                BlockPos missingLeaf = getMissingLeaf(worldIn, pos);
                                if (missingLeaf != null)
                                {
                                    //There is a missing leaf
                                    if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                    }
                                    return; //Done what we needed to do on this stage
                                }
                                else
                                {
                                    worldIn.setBlockState(pos.up(), BlockFruitTreeBranch.get(tree).getDefaultState());
                                    levelUpLeaves(worldIn, pos, true);
                                    return; //Done what we needed to do on this stage
                                }
                            }
                            else
                            {
                                BlockPos missingLeaf = getMissingLeaf(worldIn, pos.up());
                                if (missingLeaf != null)
                                {
                                    //There is a missing leaf
                                    if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                    }
                                    return; //Done what we needed to do on this stage
                                }
                                //is there a missing leaf in diagonals?
                                missingLeaf = getMissingLeaf(worldIn, pos.up(), EnumFacing.NORTH);
                                if (missingLeaf != null)
                                {
                                    if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                    }
                                    return; //Done what we needed to do on this stage
                                }
                                missingLeaf = getMissingLeaf(worldIn, pos.up(), EnumFacing.SOUTH);
                                if (missingLeaf != null)
                                {
                                    if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                    }
                                    return; //Done what we needed to do on this stage
                                }
                            }
                            List<EnumFacing> shuffledList = Arrays.asList(EnumFacing.HORIZONTALS.clone()); //if we don't clone, the original array shuffes!
                            Collections.shuffle(shuffledList, new Random(pos.toLong()));
                            for (EnumFacing branchFacing : shuffledList)
                            {
                                BlockPos branchPos = pos.offset(branchFacing);
                                if (worldIn.getBlockState(branchPos).getBlock() != BlockFruitTreeBranch.get(tree))
                                {
                                    if (worldIn.getBlockState(branchPos).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(branchPos, BlockFruitTreeBranch.get(tree).getDefaultState());
                                    }
                                    return; //Done what we needed to do on this stage
                                }
                                BlockPos missingLeaf = getMissingLeaf(worldIn, branchPos, branchFacing);
                                if (missingLeaf != null)
                                {
                                    if (worldIn.getBlockState(missingLeaf).getMaterial().isReplaceable())
                                    {
                                        worldIn.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, true));
                                        return; //Done what we needed to do on this stage
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!(worldIn.getBlockState(pos.down()).getBlock() instanceof BlockFruitTreeTrunk) && !BlocksTFC.isGrowableSoil(worldIn.getBlockState(pos.down())))
        {
            worldIn.destroyBlock(pos, false);
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
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        if (!worldIn.isRemote)
        {
            //Destroy this tree upwards starting from this block
            //Not needed to destroy leaves, they will decay by itself
            Block branch = BlockFruitTreeBranch.get(tree);
            if (worldIn.getBlockState(pos.up()).getBlock() == this || worldIn.getBlockState(pos.up()).getBlock() == branch)
            {
                worldIn.destroyBlock(pos.up(), false);
            }
            if (worldIn.getBlockState(pos.north()).getBlock() == branch)
            {
                worldIn.destroyBlock(pos.up(), false);
            }
            if (worldIn.getBlockState(pos.south()).getBlock() == branch)
            {
                worldIn.destroyBlock(pos.up(), false);
            }
            if (worldIn.getBlockState(pos.west()).getBlock() == branch)
            {
                worldIn.destroyBlock(pos.up(), false);
            }
            if (worldIn.getBlockState(pos.east()).getBlock() == branch)
            {
                worldIn.destroyBlock(pos.up(), false);
            }
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

    private BlockPos getMissingLeaf(World world, BlockPos branchPos, EnumFacing branchFacing)
    {
        //Helper method, gets a missing leaf block position that connects to this branch

        //the same facing has priority
        if (world.getBlockState(branchPos.offset(branchFacing)).getBlock() != BlockFruitTreeLeaves.get(tree))
        {
            return branchPos.offset(branchFacing);
        }

        //The rest is shuffled
        List<BlockPos> positions = Arrays.asList(branchPos.offset(branchFacing.rotateY()),
            branchPos.offset(branchFacing.rotateY().getOpposite()),
            branchPos.offset(branchFacing).offset(branchFacing.rotateY()),
            branchPos.offset(branchFacing).offset(branchFacing.rotateY().getOpposite())
        );
        Collections.shuffle(positions);
        for (BlockPos pos : positions)
        {
            if (world.getBlockState(pos).getBlock() != BlockFruitTreeLeaves.get(tree))
            {
                return pos;
            }
        }
        return null; //All leaves are in position
    }

    @Nullable
    private BlockPos getMissingLeaf(World world, BlockPos pos)
    {
        //Helper method, checks if all 5 leafs blocks(horizontals + above) has a missing block leaf

        //Above first
        if (world.getBlockState(pos.offset(EnumFacing.UP)).getBlock() != BlockFruitTreeLeaves.get(tree))
        {
            return pos.offset(EnumFacing.UP);
        }
        List<EnumFacing> shuffledList = Arrays.asList(EnumFacing.HORIZONTALS.clone()); //if i don't clone, the original array shuffes!
        Collections.shuffle(shuffledList);
        for (EnumFacing facing : shuffledList)
        {
            if (world.getBlockState(pos.offset(facing)).getBlock() != BlockFruitTreeLeaves.get(tree))
            {
                return pos.offset(facing);
            }
        }
        return null;
    }

    private void levelUpLeaves(World world, BlockPos oldCenter, boolean hasvestable)
    {
        //Remove the old ones
        for (EnumFacing facing : EnumFacing.HORIZONTALS)
        {
            if (world.getBlockState(oldCenter.offset(facing)).getBlock() == BlockFruitTreeLeaves.get(tree))
            {
                world.setBlockToAir(oldCenter.offset(facing));
            }
        }
        BlockPos missingLeaf;
        //noinspection ConstantConditions
        do
        {
            //Respawn leafs in the new height
            missingLeaf = getMissingLeaf(world, oldCenter.up());
            if (missingLeaf != null && world.getBlockState(missingLeaf).getMaterial().isReplaceable())
            {
                world.setBlockState(missingLeaf, BlockFruitTreeLeaves.get(tree).getDefaultState().withProperty(BlockFruitTreeLeaves.HARVESTABLE, hasvestable));
            }
            else
            {
                //Found a block(ceiling?) that prevents this tree to grow leafs, aborting
                break;
            }
        } while (missingLeaf != null);
    }

    private int getTrunkHeight(World world, BlockPos pos)
    {
        for (int i = 1; i < 4; i++)
        {
            if (world.getBlockState(pos.down(i)).getBlock() != this) return i;
        }
        return 4;
    }
}
