/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.dries007.tfc.util.Helpers;

public class MoltenBlock extends ExtendedBlock
{
    public static final IntegerProperty LAYERS = TFCBlockStateProperties.LAYERS_4;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public MoltenBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LAYERS, 1).setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LAYERS, LIT);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && level.getBlockState(pos).getValue(LIT))
        {
            entity.hurt(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random)
    {
        if (state.getValue(LIT) && level.isEmptyBlock(pos.above()))
        {
            final double x = pos.getX() + 0.5D;
            final double y = pos.getY() + 1.1D;
            final double z = pos.getZ() + 0.5D;
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, Helpers.triangle(random) * 0.1D, 0.2D, Helpers.triangle(random) * 0.1D);
            if (random.nextInt(10) == 0)
            {
                level.addParticle(ParticleTypes.LAVA, x, y, z, Helpers.triangle(random) * 0.1D, 0.5D, Helpers.triangle(random) * 0.1D);
            }
        }
    }
}
