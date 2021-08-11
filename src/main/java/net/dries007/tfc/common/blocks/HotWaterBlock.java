/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.dries007.tfc.client.particle.TFCParticles;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class HotWaterBlock extends LiquidBlock
{
    public HotWaterBlock(Supplier<? extends FlowingFluid> supplier, Properties properties)
    {
        super(supplier, properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand)
    {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;

        if (rand.nextInt(4) == 0)
            worldIn.addParticle(ParticleTypes.BUBBLE, x + rand.nextFloat() - rand.nextFloat(), y, z + rand.nextFloat() - rand.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (worldIn.isEmptyBlock(pos.above()))
            worldIn.addParticle(TFCParticles.STEAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
