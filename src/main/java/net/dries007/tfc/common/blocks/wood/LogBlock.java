package net.dries007.tfc.common.blocks.wood;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class LogBlock extends RotatedPillarBlock
{
    public static final BooleanProperty NATURAL = TFCBlockStateProperties.NATURAL;

    public LogBlock(Properties properties)
    {
        super(properties);

        registerDefaultState(defaultBlockState().setValue(NATURAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(NATURAL));
    }
}
