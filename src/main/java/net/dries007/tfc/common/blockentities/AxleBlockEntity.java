package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AxleBlockEntity extends TFCBlockEntity
{
    public AxleBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public AxleBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.AXLE.get(), pos, state);
    }

}
