package net.dries007.tfc.common.blocks.crop;


import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;

public class DoubleDeadCropBlock extends DeadCropBlock
{
    public static final EnumProperty<DoubleCropBlock.Part> PART = TFCBlockStateProperties.DOUBLE_CROP_PART;

    public DoubleDeadCropBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(PART));
    }
}