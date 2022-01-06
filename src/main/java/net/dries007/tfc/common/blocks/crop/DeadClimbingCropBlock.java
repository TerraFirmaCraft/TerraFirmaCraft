package net.dries007.tfc.common.blocks.crop;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class DeadClimbingCropBlock extends DeadDoubleCropBlock
{
    public static final BooleanProperty STICK = TFCBlockStateProperties.STICK;

    public DeadClimbingCropBlock(Properties properties, Crop crop)
    {
        super(properties, crop);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STICK));
    }
}