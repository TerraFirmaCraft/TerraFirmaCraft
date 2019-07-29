/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.OreDictionaryHelper;

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
        if (!worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
        {
            return true; //If world isn't loaded...
        }
        for (BlockPos.MutableBlockPos searchSupport : BlockPos.getAllInBoxMutable(pos.add(-4, -1, -4), pos.add(4, 1, 4)))
        {
            IBlockState st = worldIn.getBlockState(searchSupport);
            if (st.getBlock() instanceof BlockSupport)
            {
                if (((BlockSupport) st.getBlock()).canSupportBlocks(worldIn, searchSupport))
                {
                    return true; //Found support block that can support this position
                }
            }
        }
        return false;
    }

    /**
     * This is an optimized way to check for blocks that aren't supported during a cave in, instead of checking every single block individually and calling BlockSupper#isBeingSupported
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
        BlockPos minPoint = new BlockPos(minX, minY, minZ);
        BlockPos maxPoint = new BlockPos(maxX, maxY, maxZ);
        for (BlockPos.MutableBlockPos searchingPoint : BlockPos.getAllInBoxMutable(minPoint.add(-4, -1, -4), maxPoint.add(4, 1, 4)))
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
                    for (BlockPos.MutableBlockPos supported : BlockPos.getAllInBoxMutable(searchingPoint.add(-4, -1, -4), searchingPoint.add(4, 1, 4)))
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
        //noinspection ConstantConditions
        OreDictionaryHelper.register(this, "support", wood.getRegistryName().getPath());
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    public Tree getWood() { return this.wood; }

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
        if (getAxis(worldIn, pos) == EnumFacing.Axis.Y)
        {
            return state.withProperty(AXIS, EnumFacing.Axis.Y).withProperty(NORTH, isConnectable(worldIn, pos, EnumFacing.NORTH)).withProperty(SOUTH, isConnectable(worldIn, pos, EnumFacing.SOUTH)).withProperty(EAST, isConnectable(worldIn, pos, EnumFacing.EAST)).withProperty(WEST, isConnectable(worldIn, pos, EnumFacing.WEST));
        }
        else
        {
            //Connections are only used for vertical placed supports
            return state.withProperty(AXIS, getAxis(worldIn, pos)).withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false);
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
        EnumFacing.Axis axis = getAxis(source, pos);
        if (axis == EnumFacing.Axis.Y)
        {
            return VERTICAL_SUPPORT_AABB;
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
        EnumFacing.Axis axis = getAxis(worldIn, pos);
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
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos)
    {
        if (super.canPlaceBlockAt(worldIn, pos))
        {
            EnumFacing.Axis axis = getAxis(worldIn, pos);
            if (axis == EnumFacing.Axis.Y)
            {
                return true;
            }

            EnumFacing face = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis);
            if (isConnectable(worldIn, pos, face))
            {
                face = face.getOpposite();
            }
            int distance = getHorizontalDistance(face, worldIn, pos);
            return distance > 0;
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y).withProperty(NORTH, false).withProperty(SOUTH, false).withProperty(EAST, false).withProperty(WEST, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (worldIn.isRemote) return;
        EnumFacing.Axis axis = getAxis(worldIn, pos);
        if (axis == EnumFacing.Axis.Y)
        {
            //Try placing a 3 blocks high column in one click
            if (!isConnectable(worldIn, pos, EnumFacing.DOWN)
                && !placer.isSneaking() && stack.getCount() > 2 //Need 3 or more because at this point itemstack didn't shrink for the first block
                && worldIn.isAirBlock(pos.up()) && worldIn.isAirBlock(pos.up(2)))
            {
                //Place two more support blocks to make a 3 column in one click
                worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y));
                worldIn.setBlockState(pos.up(2), this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.Y));
                stack.shrink(2);
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
                        worldIn.setBlockState(pos.offset(face, i), this.getDefaultState().withProperty(AXIS, axis));
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
        return canBlockStay(world, pos) && getAxis(world, pos) != EnumFacing.Axis.Y;
    }

    /**
     * Check if the facing can connect (ie: is another support)
     *
     * @param world  the worldObj to check
     * @param pos    the BlockPos the current block is in
     * @param facing the facing to check for connection
     * @return true if the facing has another support block
     */
    private boolean isConnectable(IBlockAccess world, BlockPos pos, EnumFacing facing)
    {
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockSupport; //Instanceof so it can connect different wood types
    }

    /**
     * Checks if this BlockPos can only be vertical
     *
     * @param world worldObj to check
     * @param pos   BlockPos to check
     * @return true if this block can only be vertical, false otherwise
     */
    private boolean isVertical(IBlockAccess world, BlockPos pos)
    {
        //If above or below is support, or below is solid, this one must be vertical too
        if (isConnectable(world, pos, EnumFacing.UP) || isConnectable(world, pos, EnumFacing.DOWN) || world.getBlockState(pos.down()).isNormalCube())
        {
            return true;
        }
        //If this is an intersection, this one must be vertical
        return (isConnectable(world, pos, EnumFacing.NORTH) || isConnectable(world, pos, EnumFacing.SOUTH))
            && (isConnectable(world, pos, EnumFacing.EAST) || isConnectable(world, pos, EnumFacing.WEST));
    }

    /**
     * Return the axis for this block
     *
     * @param world the worldObj this block is in
     * @param pos   this block's position
     * @return axis value
     */
    private EnumFacing.Axis getAxis(IBlockAccess world, BlockPos pos)
    {
        if (isVertical(world, pos))
        {
            return EnumFacing.Axis.Y;
        }
        if (isConnectable(world, pos, EnumFacing.NORTH) || isConnectable(world, pos, EnumFacing.SOUTH))
        {
            return EnumFacing.Axis.Z;
        }
        if (isConnectable(world, pos, EnumFacing.WEST) || isConnectable(world, pos, EnumFacing.EAST))
        {
            return EnumFacing.Axis.X;
        }
        //If none of the above, this is a new placement without any neighboring support blocks
        return EnumFacing.Axis.Y;
    }

    private boolean canBlockStay(IBlockAccess world, BlockPos pos)
    {
        EnumFacing.Axis axis = getAxis(world, pos);
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
        int distance = -1;
        for (int i = 0; i < 5; i++)
        {
            if (isConnectable(worldIn, pos.offset(face, i), face) && getAxis(worldIn, pos.offset(face, i + 1)) == EnumFacing.Axis.Y)
            {
                distance = i;
                break;
            }
            if (!(worldIn.getBlockState(pos.offset(face, i)).getBlock() instanceof BlockSupport) && !worldIn.isAirBlock(pos.offset(face, i)))
            {
                return 0;
            }
        }
        return distance + 1;
    }

}
