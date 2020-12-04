package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class BodyPlantBlock extends AbstractBodyPlantBlock
{
    public static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(AbstractBlock.Properties properties, Supplier<? extends Block> headBlock, Direction direction)
    {
        super(properties, direction, SHAPE, false);
        this.headBlock = headBlock;
    }

    protected AbstractTopPlantBlock getHeadBlock()
    {
        return (AbstractTopPlantBlock) headBlock.get();
    }

    @Override
    public boolean isValidBonemealTarget(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }
}
