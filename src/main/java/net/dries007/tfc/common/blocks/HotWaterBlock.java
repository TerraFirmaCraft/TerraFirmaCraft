package net.dries007.tfc.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.client.particle.TFCParticleTypes;

public class HotWaterBlock extends FlowingFluidBlock
{
    public HotWaterBlock(Supplier<? extends FlowingFluid> supplier, Properties properties)
    {
        super(supplier, properties);
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;

        if (rand.nextInt(4) == 0)
            worldIn.addParticle(ParticleTypes.BUBBLE, x + rand.nextFloat() - rand.nextFloat(), y, z + rand.nextFloat() - rand.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (worldIn.isEmptyBlock(pos.above()))
            worldIn.addParticle(TFCParticleTypes.STEAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
