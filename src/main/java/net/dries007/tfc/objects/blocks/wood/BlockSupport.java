/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class BlockSupport extends Block
{
    /* Axis of the support, Y for vertical placed, Z/X for horizontal */
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    /* Connection sides used by vertical supports */
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool WEST = PropertyBool.create("west");

    private static final AxisAlignedBB VERTICAL_SUPPORT_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
    private static final AxisAlignedBB HORIZONTAL_X_SUPPORT_AABB = new AxisAlignedBB(0.0D, 0.625D, 0.3125D, 1.0D, 1.0D, 0.6875D);
    private static final AxisAlignedBB HORIZONTAL_Z_SUPPORT_AABB = new AxisAlignedBB(0.3125D, 0.625D, 0.0D, 0.6875D, 1.0D, 1.0D);
    private static final AxisAlignedBB CONNECTION_N_AABB = new AxisAlignedBB(0.3125D, 0.625D, 0.0D, 0.6875D, 1.0D, 0.3125D);
    private static final AxisAlignedBB CONNECTION_S_AABB = new AxisAlignedBB(0.3125D, 0.625D, 0.6875D, 0.6875D, 1.0D, 1.0);
    private static final AxisAlignedBB CONNECTION_E_AABB = new AxisAlignedBB(0.6875D, 0.625D, 0.3125D, 1.0D, 1.0D, 0.6875D);
    private static final AxisAlignedBB CONNECTION_W_AABB = new AxisAlignedBB(0.0D, 0.625D, 0.3125D, 0.3125D, 1.0D, 0.6875D);

    private static final Map<Tree, BlockSupport> MAP = new HashMap<>();

    public static BlockSupport get(Tree wood)
    {
        return MAP.get(wood);
    }

    public static ItemStack get(Tree wood, int amount)
    {
        return new ItemStack(MAP.get(wood), amount);
    }

    /**
     * Checks if this pos is being supported by a support beam
     *
     * @param worldIn the worldObj to check
     * @param pos     the BlockPos to check for support
     * @return true if there is a support in 4 block radius
     */
    public static boolean isBeingSupported(World worldIn, BlockPos pos)
    {
        int sRangeHor = ConfigTFC.General.FALLABLE.supportBeamRangeHor;
        int sRangeVert = ConfigTFC.General.FALLABLE.supportBeamRangeUp;
        int sRangeHorNeg = ConfigTFC.General.FALLABLE.supportBeamRangeHor * -1;
        int sRangeVertNeg = ConfigTFC.General.FALLABLE.supportBeamRangeDown * -1;
        if (!worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
        {
            return true; // If world isn't loaded...
        }
        for (BlockPos.MutableBlockPos searchSupport : BlockPos.getAllInBoxMutable(
            pos.add(sRangeHorNeg, sRangeVertNeg, sRangeHorNeg), pos.add(sRangeHor, sRangeVert, sRangeHor)))
        {
            IBlockState st = worldIn.getBlockState(searchSupport);
            if (st.getBlock() instanceof BlockSupport)
            {
                if (((BlockSupport) st.getBlock()).canSupportBlocks(worldIn, searchSupport))
                {
                    return true; // Found support block that can support this position
                }
            }
        }
        return false;
    }

    /**
     * This is an optimized way to check for blocks that aren't supported during a
     * cave in, instead of checking every single block individually and calling
     * BlockSupper#isBeingSupported
     */
    public static Set<BlockPos> getAllUnsupportedBlocksIn(World worldIn, BlockPos from, BlockPos to)
    {
        Set<BlockPos> listSupported = new HashSet<>();
        Set<BlockPos> listUnsupported = new HashSet<>();
        int minX = Math.min(from.getX(), to.getX());
        int maxX = Math.max(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int maxY = Math.max(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxZ = Math.max(from.getZ(), to.getZ());
        int sRangeHor = ConfigTFC.General.FALLABLE.supportBeamRangeHor;
        int sRangeVert = ConfigTFC.General.FALLABLE.supportBeamRangeUp;
        int sRangeHorNeg = ConfigTFC.General.FALLABLE.supportBeamRangeHor * -1;
        int sRangeVertNeg = ConfigTFC.General.FALLABLE.supportBeamRangeDown * -1;
        BlockPos minPoint = new BlockPos(minX, minY, minZ);
        BlockPos maxPoint = new BlockPos(maxX, maxY, maxZ);
        for (BlockPos.MutableBlockPos searchingPoint : BlockPos.getAllInBoxMutable(minPoint.add(sRangeHorNeg, sRangeVertNeg, sRangeHorNeg),
            maxPoint.add(sRangeHor, sRangeVert, sRangeHor)))
        {
            if (!listSupported.contains(searchingPoint))
            {
                listUnsupported.add(searchingPoint.toImmutable()); //Adding blocks that wasn't found supported
            }
            IBlockState st = worldIn.getBlockState(searchingPoint);
            if (st.getBlock() instanceof BlockSupport)
            {
                if (((BlockSupport) st.getBlock()).canSupportBlocks(worldIn, searchingPoint))
                {
                    for (BlockPos.MutableBlockPos supported : BlockPos.getAllInBoxMutable(searchingPoint.add(sRangeHorNeg, sRangeVertNeg, sRangeHorNeg), searchingPoint.add(sRangeHor, sRangeVert, sRangeHor)))
                    {
                        listSupported.add(supported.toImmutable()); //Adding all supported blocks by this support
                        listUnsupported.remove(supported); //Remove if this block was added earlier
                    }
                }
            }
        }
        //Searching point wasn't from points between from <-> to but
        //Time to remove the outsides that were added for convenience
        listUnsupported.removeIf(content -> content.getX() < minX || content.getX() > maxX
            || content.getY() < minY || content.getY() > maxY
            || content.getZ() < minZ || content.getZ() > maxZ);

        return listUnsupported;
    }

    private final Tree wood;

    public BlockSupport(Tree wood)
    {
        super(Material.WOOD, Material.WOOD.getMaterialMapColor());
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(2.0F);
        setHarvestLevel("axe", 0);
        setSoundType(SoundType.WOOD);
        this.wood = wood;
        OreDictionaryHelper.register(this, "support");
        Blocks.FIRE.setFireInfo(this, 5, 20);
        setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false));
    }

    public Tree getWood() { return this.wood; }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(AXIS).ordinal();
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getActualState(@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(AXIS) == EnumFacing.Axis.Y)
        {
            return state.withProperty(NORTH, isConnectable(worldIn, pos, EnumFacing.NORTH)).withProperty(SOUTH, isConnectable(worldIn, pos, EnumFacing.SOUTH)).withProperty(EAST, isConnectable(worldIn, pos, EnumFacing.EAST)).withProperty(WEST, isConnectable(worldIn, pos, EnumFacing.WEST));
        }
        else
        {
            //Connections are only used for vertical placed supports
            return state.withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false);
        }
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
        EnumFacing.Axis axis = state.getValue(AXIS);
        if (axis == EnumFacing.Axis.Y)
        {
            AxisAlignedBB value = VERTICAL_SUPPORT_AABB;
            if (isConnectable(source, pos, EnumFacing.NORTH))
            {
                value = value.union(CONNECTION_N_AABB);
            }
            if (isConnectable(source, pos, EnumFacing.SOUTH))
            {
                value = value.union(CONNECTION_S_AABB);
            }
            if (isConnectable(source, pos, EnumFacing.EAST))
            {
                value = value.union(CONNECTION_E_AABB);
            }
            if (isConnectable(source, pos, EnumFacing.WEST))
            {
                value = value.union(CONNECTION_W_AABB);
            }
            return value;
        }
        else
        {
            return axis == EnumFacing.Axis.X ? HORIZONTAL_X_SUPPORT_AABB : HORIZONTAL_Z_SUPPORT_AABB;
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
    public void addCollisionBoxToList(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        EnumFacing.Axis axis = state.getValue(AXIS);
        if (axis == EnumFacing.Axis.Y)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, VERTICAL_SUPPORT_AABB);
            if (isConnectable(worldIn, pos, EnumFacing.NORTH))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, CONNECTION_N_AABB);
            }
            if (isConnectable(worldIn, pos, EnumFacing.SOUTH))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, CONNECTION_S_AABB);
            }
            if (isConnectable(worldIn, pos, EnumFacing.EAST))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, CONNECTION_E_AABB);
            }
            if (isConnectable(worldIn, pos, EnumFacing.WEST))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, CONNECTION_W_AABB);
            }
        }
        else if (axis == EnumFacing.Axis.X)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, HORIZONTAL_X_SUPPORT_AABB);
        }
        else
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, HORIZONTAL_Z_SUPPORT_AABB);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (!this.canBlockStay(worldIn, pos))
        {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean canPlaceBlockOnSide(@Nonnull World world, @Nonnull BlockPos pos, EnumFacing side)
    {
        if (side.getAxis() == EnumFacing.Axis.Y)
        {
            return world.getBlockState(pos.down()).isNormalCube() || isConnectable(world, pos, EnumFacing.DOWN);
        }
        else
        {
            if (!isConnectable(world, pos, side.getOpposite())) return false;
            int distance = getHorizontalDistance(side, world, pos);
            return distance > 0;
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack heldStack = player.getHeldItem(hand);
        if (player.isSneaking() && heldStack.getItem() instanceof ItemBlock && ((ItemBlock) heldStack.getItem()).getBlock() == this)
        {
            // Try placing a support block
            int maxSearch = 5;
            BlockPos above = pos.up();
            while (world.getBlockState(above).getBlock() instanceof BlockSupport)
            {
                above = above.up();
                if (--maxSearch <= 0)
                {
                    return false;
                }
            }
            if (world.getBlockState(above).getMaterial().isReplaceable())
            {
                if (!world.isRemote)
                {
                    world.setBlockState(above, this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y), 2);
                    if (!player.isCreative())
                    {
                        heldStack.shrink(1);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (worldIn.isRemote) return;
        EnumFacing.Axis axis = state.getValue(AXIS);
        if (axis == EnumFacing.Axis.Y)
        {
            //Try placing a 3 blocks high column in one click
            if (!isConnectable(worldIn, pos, EnumFacing.DOWN)
                && !placer.isSneaking() && stack.getCount() > 2 //Need 3 or more because at this point itemstack didn't shrink for the first block
                && worldIn.isAirBlock(pos.up()) && worldIn.isAirBlock(pos.up(2)))
            {
                //Place two more support blocks to make a 3 column in one click
                if (worldIn.checkNoEntityCollision(new AxisAlignedBB(pos.up())))
                {
                    worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y), 2);
                    if (worldIn.checkNoEntityCollision(new AxisAlignedBB(pos.up(2))))
                    {
                        worldIn.setBlockState(pos.up(2), this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y), 2);
                        stack.shrink(2);
                    }
                    else
                    {
                        stack.shrink(1);
                    }
                }
            }
        }
        else
        {
            //Try placing all horizontally placed blocks in one go
            EnumFacing face = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);
            if (isConnectable(worldIn, pos, face))
            {
                face = face.getOpposite();
            }
            int distance = getHorizontalDistance(face, worldIn, pos);
            if (distance == 0 || stack.getCount() < distance)
            {
                //Another vertical support to connect not found or player don't have enough items to place.
                worldIn.destroyBlock(pos, true);
            }
            else if (distance > 0)
            {
                stack.shrink(distance - 1); //-1 because the first one is already placed by onBlockPlace
                for (int i = 1; i < distance; i++)
                {
                    if (worldIn.getBlockState(pos.offset(face, i)).getMaterial().isReplaceable())
                    {
                        worldIn.setBlockState(pos.offset(face, i), this.getDefaultState().withProperty(AXIS, axis), 2);
                        worldIn.scheduleBlockUpdate(pos.offset(face, i).down(), worldIn.getBlockState(pos.offset(face, i).down()).getBlock(), 3, 2);
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, AXIS, NORTH, SOUTH, EAST, WEST);
    }

    /**
     * Checks if this support block can support collapsable/fallable blocks
     * Returns true only if this is horizontally placed and can stay in place.
     *
     * @param world the worldObj this support block is in
     * @param pos   the BlockPos this support block is in
     * @return true if this can support blocks
     */
    private boolean canSupportBlocks(IBlockAccess world, BlockPos pos)
    {
        return canBlockStay(world, pos) && world.getBlockState(pos).getValue(AXIS) != EnumFacing.Axis.Y;
    }

    /**
     * Check if the facing can connect
     *
     * @param world  the worldObj to check
     * @param pos    the BlockPos the current block is in
     * @param facing the facing to check for connection
     * @return true if the facing has another support block and it's Axis is Y or facing this connection
     */
    private boolean isConnectable(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        IBlockState state = world.getBlockState(pos.offset(facing));
        if (state.getBlock() instanceof BlockSupport)
        {
            EnumFacing.Axis offsetAxis = state.getValue(AXIS);
            return offsetAxis == EnumFacing.Axis.Y || offsetAxis == facing.getAxis();
        }
        return false;
    }

    /**
     * Checks if this block is a vertical support beam of height 3 or higher
     *
     * @param world the world this block is in
     * @param pos   the position of the block
     * @return true if this is a vertical support beam three blocks or higher, false otherwise
     */
    private boolean isThreeTall(IBlockAccess world, BlockPos pos)
    {
        // if the block is invalid it definitely can't support a vertical beam
        if (!canBlockStay(world, pos)) return false;
        IBlockState state = world.getBlockState(pos);
        EnumFacing.Axis axis = state.getValue(AXIS);
        // sideways supports are never three tall
        if (axis != EnumFacing.Axis.Y) return false;
        // if either of the two block beneath this block are not block supports, then this isn't three tall
        if (!(world.getBlockState(pos.down()).getBlock() instanceof BlockSupport)) return false;
        return world.getBlockState(pos.down().down()).getBlock() instanceof BlockSupport;
    }

    /**
     * Checks if this support can stay in pos
     *
     * @param world the world obj
     * @param pos   the pos of this support
     * @return true if this support can stay in this position
     */
    private boolean canBlockStay(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockSupport)) return false;
        EnumFacing.Axis axis = state.getValue(AXIS);
        if (axis == EnumFacing.Axis.Y)
        {
            return world.getBlockState(pos.down()).isNormalCube() || isConnectable(world, pos, EnumFacing.DOWN);
        }
        if (axis == EnumFacing.Axis.X)
        {
            return isConnectable(world, pos, EnumFacing.WEST) && isConnectable(world, pos, EnumFacing.EAST);
        }
        if (axis == EnumFacing.Axis.Z)
        {
            return isConnectable(world, pos, EnumFacing.NORTH) && isConnectable(world, pos, EnumFacing.SOUTH);
        }
        return true; //Should never happen, yet, if this is EnumAxis.NONE...
    }

    /**
     * Checks the distance to a vertical support, in blocks
     *
     * @param face    the EnumFacing to check, please use N-S-W-E
     * @param worldIn the worldObj to check blocks
     * @param pos     the BlockPos to start
     * @return 0 if not found, 1-5 block distance between this BlockPos and the found vertical support
     */
    private int getHorizontalDistance(EnumFacing face, IBlockAccess worldIn, BlockPos pos)
    {
        // if the placement block on the clicked side is not three tall don't bother checking for length
        if (!isThreeTall(worldIn, pos.offset(face.getOpposite()))) return 0;
        // look across the gap for valid distance
        int distance = -1;
        for (int i = 0; i < 5; i++)
        {
            IBlockState state = worldIn.getBlockState(pos.offset(face, i + 1));
            if (state.getBlock() instanceof BlockSupport && state.getValue(AXIS) == EnumFacing.Axis.Y)
            {
                distance = i;
                break;
            }
            if (!(worldIn.getBlockState(pos.offset(face, i)).getBlock() instanceof BlockSupport) && !worldIn.isAirBlock(pos.offset(face, i)))
            {
                return 0;
            }
        }

        // if another side wasn't found, fail
        if (distance == -1) return 0;

        // if the other side isn't three tall, fail
        if (!isThreeTall(worldIn, pos.offset(face, distance + 1))) return 0;

        // return the distance + 1 because the distance checked is off by one for the loop
        return distance + 1;
    }

}
