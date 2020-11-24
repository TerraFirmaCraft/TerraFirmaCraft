package net.dries007.tfc.common.blocks.rock;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class MossSpreadingStairBlock extends StairsBlock
{
    public MossSpreadingStairBlock(Supplier<BlockState> state, Properties properties)
    {
        super(state, properties);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state)
    {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random)
    {
        MossSpreadingBlock.spreadMoss(worldIn, pos, state, random);
    }
}
