package net.dries007.tfc.common.blocks.crop;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class DeadCropBlock extends BushBlock
{
    public static final BooleanProperty MATURE = TFCBlockStateProperties.MATURE;

    public DeadCropBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(MATURE);
    }
}