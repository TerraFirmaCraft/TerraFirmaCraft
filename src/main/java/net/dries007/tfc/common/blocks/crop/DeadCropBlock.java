package net.dries007.tfc.common.blocks.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.TFCBushBlock;

public class DeadCropBlock extends TFCBushBlock
{
    public static final BooleanProperty MATURE = TFCBlockStateProperties.MATURE;

    public DeadCropBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(MATURE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(MATURE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.block(); // todo
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.FARMLAND);
    }
}