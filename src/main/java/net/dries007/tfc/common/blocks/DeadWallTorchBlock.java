package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DeadWallTorchBlock extends WallTorchBlock
{
    public DeadWallTorchBlock(Properties properties, IParticleData particle)
    {
        super(properties, particle);
    }

    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {

    }
}
