package net.dries007.tfc.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

public class BodyPlantBlock extends AbstractBodyPlantBlock
{
    private final Supplier<? extends Block> headBlock;

    public BodyPlantBlock(AbstractBlock.Properties properties, Supplier<? extends Block> headBlock, VoxelShape shape, Direction direction)
    {
        super(properties, direction, shape, false);
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

    @Override // lifted from AbstractPlantBlock to add leaves to it
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.relative(growthDirection.getOpposite());
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (!canAttachToBlock(block))
        {
            return false;
        }
        else
        {
            return block == getHeadBlock() || block == getBodyBlock() || blockstate.is(BlockTags.LEAVES) || blockstate.isFaceSturdy(worldIn, blockpos, growthDirection);
        }
    }
}
