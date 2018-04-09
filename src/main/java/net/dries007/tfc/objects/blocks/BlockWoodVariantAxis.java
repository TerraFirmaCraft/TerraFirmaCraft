package net.dries007.tfc.objects.blocks;

import net.dries007.tfc.util.EnumAxis;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWoodVariantAxis extends BlockWoodVariant
{
    public static final PropertyEnum<EnumAxis> LOG_AXIS = PropertyEnum.<EnumAxis>create("axis", EnumAxis.class);

    public BlockWoodVariantAxis(Type type, Wood wood)
    {
        super(type, wood);
        setDefaultState(this.getDefaultState().withProperty(LOG_AXIS, EnumAxis.Y));
    }

    @Override public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos){ return true; }

    @Override public boolean isWood(IBlockAccess world, BlockPos pos)
    {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[]{LOG_AXIS});
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LOG_AXIS, EnumAxis.getFacingfromMeta(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return EnumAxis.getMetafromAxis((EnumAxis)state.getValue(LOG_AXIS));
    }
    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getStateFromMeta(meta).withProperty(LOG_AXIS, EnumAxis.fromFacingAxis(facing.getAxis()));
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch ((EnumAxis)state.getValue(LOG_AXIS))
                {
                    case X:
                        return state.withProperty(LOG_AXIS, EnumAxis.Z);
                    case Z:
                        return state.withProperty(LOG_AXIS, EnumAxis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }
}
