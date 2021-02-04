package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TFCDeadTorchBlock extends TorchBlock
{
    public TFCDeadTorchBlock(Properties properties, IParticleData particle)
    {
        super(properties, particle);
    }

    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {

    }
}
