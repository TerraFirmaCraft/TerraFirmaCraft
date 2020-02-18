package net.dries007.tfc.objects.blocks.soil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.tags.TFCBlockTags;

@ParametersAreNonnullByDefault
public class TFCGrassBlock extends Block
{
    // Used for connected textures only.
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");

    @Nullable
    private static BooleanProperty getPropertyForFace(Direction direction)
    {
        switch (direction)
        {
            case NORTH:
                return NORTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            case SOUTH:
                return SOUTH;
            default:
                return null;
        }
    }

    public TFCGrassBlock(Properties properties)
    {
        super(properties);

        setDefaultState(stateContainer.getBaseState().with(SOUTH, false).with(EAST, false).with(NORTH, false).with(WEST, false));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        BooleanProperty property = getPropertyForFace(facing);
        if (property != null)
        {
            return stateIn.with(property, TFCBlockTags.GRASS.contains(worldIn.getBlockState(facingPos.down()).getBlock()));
        }
        return stateIn;
    }

    @Override
    @Nonnull
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        IBlockReader world = context.getWorld();
        BlockPos pos = context.getPos().down();
        return getDefaultState()
            .with(NORTH, TFCBlockTags.GRASS.contains(world.getBlockState(pos.offset(Direction.NORTH)).getBlock()))
            .with(EAST, TFCBlockTags.GRASS.contains(world.getBlockState(pos.offset(Direction.EAST)).getBlock()))
            .with(WEST, TFCBlockTags.GRASS.contains(world.getBlockState(pos.offset(Direction.WEST)).getBlock()))
            .with(SOUTH, TFCBlockTags.GRASS.contains(world.getBlockState(pos.offset(Direction.SOUTH)).getBlock()));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(NORTH, EAST, SOUTH, WEST);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSolid(BlockState state)
    {
        return true;
    }

    @Override
    @Nonnull
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }
}
