/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockPathTFC extends BlockRockVariantFallable
{
    private static final AxisAlignedBB GRASS_PATH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    public BlockPathTFC(Rock.Type type, Rock rock)
    {
        super(type, rock);
        setLightOpacity(255);
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
        return GRASS_PATH_AABB;
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

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        switch (side)
        {
            case UP:
                return true;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
                Block block = iblockstate.getBlock();
                return !iblockstate.isOpaqueCube() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH;
            default:
                return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(get(rock, Rock.Type.DIRT));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
        this.updateBlockState(worldIn, pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        this.updateBlockState(worldIn, pos);
    }

    private void updateBlockState(World worldIn, BlockPos pos)
    {
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid())
        {
            worldIn.setBlockState(pos, BlockRockVariant.get(rock, Rock.Type.DIRT).getDefaultState());
            for (Entity entity : worldIn.getEntitiesWithinAABBExcludingEntity(null, GRASS_PATH_AABB.offset(pos)))
            {
                double initialOffset = Math.min(GRASS_PATH_AABB.offset(pos).maxY - GRASS_PATH_AABB.offset(pos).minY, GRASS_PATH_AABB.offset(pos).maxY - entity.getEntityBoundingBox().minY);
                entity.setPositionAndUpdate(entity.posX, entity.posY + initialOffset + 0.001D, entity.posZ);
            }
        }
    }
}
