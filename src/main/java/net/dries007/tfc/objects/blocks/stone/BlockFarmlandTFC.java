/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import net.dries007.tfc.api.types.Rock;

public class BlockFarmlandTFC extends BlockRockVariant
{
    public static final int MAX_MOISTURE = 15;
    public static final PropertyInteger MOISTURE = PropertyInteger.create("moisture", 0, MAX_MOISTURE);
    public static final int[] TINT = new int[] {
        0xffffffff,
        0xfff7f7f7,
        0xffefefef,
        0xffe7e7e7,
        0xffdfdfdf,
        0xffd7d7d7,
        0xffcfcfcf,
        0xffc7c7c7,
        0xffbfbfbf,
        0xffb7b7b7,
        0xffafafaf,
        0xffa7a7a7,
        0xff9f9f9f,
        0xff979797,
        0xff8f8f8f,
        0xff878787,
    };
    protected static final AxisAlignedBB FARMLAND_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);
    protected static final AxisAlignedBB FLIPPED_AABB = new AxisAlignedBB(0.0D, 0.9375D, 0.0D, 1.0D, 1.0D, 1.0D);

    public BlockFarmlandTFC(Rock.Type type, Rock rock)
    {
        super(type, rock);
        setDefaultState(blockState.getBaseState().withProperty(MOISTURE, 1)); // 1 is default so it doesn't instantly turn back to dirt
        setTickRandomly(true);
        setLightOpacity(255);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(MOISTURE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(MOISTURE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FARMLAND_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        if (!worldIn.isRemote && entityIn.canTrample(worldIn, this, pos, fallDistance)) // Forge: Move logic to Entity#canTrample
        {
            turnToDirt(worldIn, pos);
        }
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, MOISTURE);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        int current = state.getValue(MOISTURE);
        int target = world.isRainingAt(pos.up()) ? MAX_MOISTURE : getWaterScore(world, pos);

        if (current < target)
        {
            if (current < MAX_MOISTURE) world.setBlockState(pos, state.withProperty(MOISTURE, current + 1), 2);
        }
        else if (current > target || target == 0)
        {
            if (current > 0) world.setBlockState(pos, state.withProperty(MOISTURE, current - 1), 2);
            else if (!hasCrops(world, pos)) turnToDirt(world, pos);
        }

        super.updateTick(world, pos, state, rand);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(get(rock, Rock.Type.DIRT));
    }

    public int getWaterScore(IBlockAccess world, BlockPos pos)
    {
        final int hRange = 7;
        float score = 0;
        for (BlockPos.MutableBlockPos i : BlockPos.getAllInBoxMutable(pos.add(-hRange, -1, -hRange), pos.add(hRange, 2, hRange)))
        {
            BlockPos diff = i.subtract(pos);
            float hDist = MathHelper.sqrt(diff.getX() * diff.getX() + diff.getZ() * diff.getZ());
            if (hDist > hRange) continue;
            if (world.getBlockState(i).getMaterial() != Material.WATER) continue;
            score += ((hRange - hDist) / (float) hRange);
        }
        return score > 1 ? MAX_MOISTURE : Math.round(score * MAX_MOISTURE);
    }

    public void turnToDirt(World world, BlockPos pos)
    {
        world.setBlockState(pos, get(rock, Rock.Type.DIRT).getDefaultState());
        AxisAlignedBB axisalignedbb = FLIPPED_AABB.offset(pos);
        for (Entity entity : world.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb))
        {
            double d0 = Math.min(axisalignedbb.maxY - axisalignedbb.minY, axisalignedbb.maxY - entity.getEntityBoundingBox().minY);
            entity.setPositionAndUpdate(entity.posX, entity.posY + d0 + 0.001D, entity.posZ);
        }
    }

    private boolean hasCrops(World worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos.up()).getBlock();
        return block instanceof IPlantable && canSustainPlant(worldIn.getBlockState(pos), worldIn, pos, EnumFacing.UP, (IPlantable) block);
    }

    //todo: onBlockAdded turn back if solid above
    //todo: neighborChanged turn back if solid above

}
