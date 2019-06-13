/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TEBloomery;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.ILightableBlock;
import net.dries007.tfc.util.functionalinterfaces.FacingChecker;

import static net.dries007.tfc.util.Helpers.getAValidHorizontal;
import static net.minecraft.block.BlockTrapDoor.OPEN;

public class BlockBloomery extends BlockHorizontal implements IItemSize, ILightableBlock
{
    //[horizontal index][basic shape / door1 / door2]
    private static final AxisAlignedBB[][] AABB =
        {
            {
                new AxisAlignedBB(0.0F, 0.0F, 0.0f, 1.0f, 1.0F, 0.5F),
                new AxisAlignedBB(0.0F, 0.0F, 0.0f, 0.125f, 1.0F, 0.5F),
                new AxisAlignedBB(0.875F, 0.0F, 0.0f, 1.0f, 1.0F, 0.5F)
            },
            {
                new AxisAlignedBB(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0f),
                new AxisAlignedBB(0.5F, 0.0F, 0.0F, 1.0F, 1.0F, 0.125f),
                new AxisAlignedBB(0.5F, 0.0F, 0.875F, 1.0F, 1.0F, 1.0f)
            },
            {
                new AxisAlignedBB(0.0f, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F),
                new AxisAlignedBB(0.0f, 0.0F, 0.5F, 0.125F, 1.0F, 1.0F),
                new AxisAlignedBB(0.875f, 0.0F, 0.5F, 1.0F, 1.0F, 1.0F)
            },
            {
                new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 1.0F),
                new AxisAlignedBB(0.0F, 0.0F, 0.0F, 0.5F, 1.0F, 0.125F),
                new AxisAlignedBB(0.0F, 0.0F, 0.875F, 0.5F, 1.0F, 1.0F)
            }
        };

    private FacingChecker isValidMultiblock = (World world, BlockPos pos, EnumFacing facing) -> getLevel(world, pos, facing) > -1;

    public BlockBloomery()
    {
        super(Material.IRON);
        setSoundType(SoundType.METAL);
        setHarvestLevel("pickaxe", 0);
        setHardness(20.0F);
        setDefaultState(this.blockState.getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(LIT, false)
            .withProperty(OPEN, false));
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.VERY_SMALL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState()
            .withProperty(FACING, EnumFacing.byHorizontalIndex(meta % 4))
            .withProperty(LIT, meta / 4 % 2 != 0)
            .withProperty(OPEN, meta / 8 != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex()
            + (state.getValue(LIT) ? 4 : 0)
            + (state.getValue(OPEN) ? 8 : 0);
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
        return AABB[state.getValue(FACING).getHorizontalIndex()][0];
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    @ParametersAreNonnullByDefault
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        if (blockState.getValue(OPEN))
            return NULL_AABB;
        return AABB[blockState.getValue(FACING).getHorizontalIndex()][0];
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
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        if (getLevel(worldIn, pos, state.getValue(FACING)) > -1) return;
        dropBlockAsItem(worldIn, pos, state, 0);
        //TODO
        //TEToolRack te = Helpers.getTE(worldIn, pos, TEToolRack.class);
        //if (te != null) te.onBreakBlock();
        worldIn.setBlockToAir(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nullable
    @ParametersAreNonnullByDefault
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        if (!blockState.getValue(OPEN))
            return super.collisionRayTrace(blockState, worldIn, pos, start, end);

        int index = blockState.getValue(FACING).getHorizontalIndex();
        RayTraceResult rayTraceDoor1 = rayTrace(pos, start, end, AABB[index][1]),
            rayTraceDoor2 = rayTrace(pos, start, end, AABB[index][2]);

        if (rayTraceDoor1 == null)
            return rayTraceDoor2;
        else if (rayTraceDoor2 == null)
            return rayTraceDoor1;
        return rayTraceDoor1.hitVec.squareDistanceTo(end) > rayTraceDoor2.hitVec.squareDistanceTo(end)
            ? rayTraceDoor1 : rayTraceDoor2;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, @Nonnull BlockPos pos)
    {
        if (!super.canPlaceBlockAt(worldIn, pos))
            return false;

        switch (getLevel(worldIn, pos, EnumFacing.SOUTH))
        {
            case -2:
                return false;
            case -1:
            {
                for (int i = 1; i < 4; i++)
                    if (getLevel(worldIn, pos, EnumFacing.HORIZONTALS[i]) > -1)
                        return true;
                return false;
            }
            default:
                return true;
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEBloomery te = Helpers.getTE(worldIn, pos, TEBloomery.class);
        if (te == null)
            return true;
        ItemStack item = playerIn.getHeldItem(hand);
        if (ItemFireStarter.canIgnite(item))
        {
            if (state.getValue(LIT) || !te.canIgnite())
                //think about this more
                return true;
            //TODO ignite
            item.damageItem(1, playerIn);
            worldIn.setBlockState(pos, state.withProperty(LIT, true));
            return true;
        }
        worldIn.setBlockState(pos, state.cycleProperty(OPEN));
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, getAValidHorizontal(worldIn, pos, isValidMultiblock, placer.getHorizontalFacing()));
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT, OPEN);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    @ParametersAreNonnullByDefault
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEBloomery();
    }

    /**
     * @return true if the block is valid for a bloomery, false otherwise
     */
    public boolean isBlockEligible(World worldIn, @Nonnull BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        Material blockMaterial = state.getMaterial();
        return (blockMaterial == Material.ROCK || blockMaterial == Material.IRON) && state.isNormalCube();
    }

    /**
     * @return bloomery height, maximum of 4, or a negative value as follows:
     * -2 if the position is invalid no matter the rotation
     * -1 if the position is invalid but rotating might fix this
     */
    public int getLevel(World worldIn, @Nonnull BlockPos pos, @Nonnull EnumFacing facing)
    {
        if (!(isBlockEligible(worldIn, pos.up()) && isBlockEligible(worldIn, pos.down())))
            return -2;
        Vec3i horizontal = facing.rotateY().getDirectionVec();
        if (!(isBlockEligible(worldIn, pos.add(horizontal)) && isBlockEligible(worldIn, pos.subtract(horizontal))))
            return -1;

        pos = pos.offset(facing);
        if (!(isBlockEligible(worldIn, pos.offset(facing)) && isBlockEligible(worldIn, pos.add(horizontal)) && isBlockEligible(worldIn, pos.subtract(horizontal))))
            return -1;
        for (int i = 0; i < 5; i++)
        {
            pos = pos.up();
            if (!(isBlockEligible(worldIn, pos.north())
                && isBlockEligible(worldIn, pos.south())
                && isBlockEligible(worldIn, pos.east())
                && isBlockEligible(worldIn, pos.west())))
                return i;
        }
        return 4;
    }
}
