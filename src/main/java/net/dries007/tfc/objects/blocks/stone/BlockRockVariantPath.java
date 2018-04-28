package net.dries007.tfc.objects.blocks.stone;

import net.dries007.tfc.objects.Rock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRockVariantPath extends BlockRockVariant {

    protected static final AxisAlignedBB ROCK_PATH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9375D, 1.0D);

    public BlockRockVariantPath(Rock.Type type, Rock rock)
    {
        super(type, rock);
        this.setLightOpacity(255);
    }
@Override
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
                return !iblockstate.isOpaqueCube() && !(block instanceof BlockRockVariantPath); // TODO: 28/4/18 make TFC blocks
            default:
                return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }
@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return ROCK_PATH_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }
}
