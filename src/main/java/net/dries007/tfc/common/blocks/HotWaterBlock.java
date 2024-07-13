/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.config.TFCConfig;

public class HotWaterBlock extends LiquidBlock
{
    public HotWaterBlock(Supplier<? extends FlowingFluid> supplier, Properties properties)
    {
        super(supplier.get(), properties);
    }

    @Override
    public void animateTick(BlockState stateIn, Level level, BlockPos pos, RandomSource random)
    {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 1.0D;
        double z = pos.getZ() + 0.5D;

        if (random.nextInt(4) == 0)
            level.addParticle(ParticleTypes.BUBBLE, x + random.nextFloat() - random.nextFloat(), y, z + random.nextFloat() - random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (level.isEmptyBlock(pos.above()))
            level.addParticle(TFCParticles.STEAM.get(), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.random.nextInt(10) == 0 && entity instanceof LivingEntity living && living.getHealth() < living.getMaxHealth())
        {
            living.heal(TFCConfig.SERVER.hotWaterHealAmount.get().floatValue());
        }
    }
}
