/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class IcicleBlock extends ThinSpikeBlock
{
    /**
     * Modified from {@link net.minecraft.world.level.block.PointedDripstoneBlock#spawnDripParticle(Level, BlockPos, BlockState, Fluid)}
     */
    public static void spawnDripParticle(Level level, BlockPos pos, BlockState state)
    {
        Vec3 offset = state.getOffset(level, pos);
        level.addParticle(ParticleTypes.DRIPPING_DRIPSTONE_WATER, pos.getX() + 0.5D + offset.x, ((pos.getY() + 1) - 0.6875F) - 0.0625D, pos.getZ() + 0.5D + offset.z, 0.0D, 0.0D, 0.0D);
    }

    public IcicleBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    protected void attack(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (Helpers.isItem(player.getMainHandItem(), TFCTags.Items.TOOLS_HAMMER) || Helpers.isItem(player.getMainHandItem(), ItemTags.SWORDS))
        {
            level.destroyBlock(pos, true);
            for (BlockPos testPos : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)))
            {
                if (level.getBlockState(testPos).getBlock() instanceof IcicleBlock)
                {
                    level.destroyBlock(testPos, true);
                }
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        final float temperature = Climate.getTemperature(level, pos);
        if (state.getValue(TIP) && state.getValue(FLUID).getFluid() == Fluids.EMPTY && temperature > 0 && random.nextFloat() < 0.15f)
        {
            if (random.nextFloat() < 0.15f)
            {
                spawnDripParticle(level, pos, state);
            }
        }
    }
}
