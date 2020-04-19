package net.dries007.tfc.objects.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class RockSpikeBlock extends Block
{
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public static final VoxelShape BASE_SHAPE = makeCuboidShape(2, 0, 2, 14, 16, 14);
    public static final VoxelShape MIDDLE_SHAPE = makeCuboidShape(4, 0, 4, 12, 16, 12);
    public static final VoxelShape TIP_SHAPE = makeCuboidShape(6, 0, 6, 10, 16, 10);

    public RockSpikeBlock(Properties properties)
    {
        super(properties);

        setDefaultState(stateContainer.getBaseState().with(PART, Part.BASE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        switch (state.get(PART))
        {
            case BASE:
                return BASE_SHAPE;
            case MIDDLE:
                return MIDDLE_SHAPE;
            case TIP:
            default:
                return TIP_SHAPE;
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PART);
    }

    public enum Part implements IStringSerializable
    {
        BASE, MIDDLE, TIP;

        @Override
        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
