/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.rock;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.util.data.Support;


public class RawRockBlock extends RotatedPillarBlock
{
    public RawRockBlock(Properties properties)
    {
        super(properties);
    }

    /**
     * Borrowed from {@link net.minecraft.world.level.block.FallingBlock}, this creates small particles when a block is unsupported and
     * could start a collapse.
     */
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (random.nextInt(64) == 0)
        {
            // Rarity reduced from 16 (gravel/sand particles) -> 64 - partially as the support check is expensive, partially because this will be fairly common, and we don't want to do it more than necessary
            // `canStartCollapse` does all the relatively 'cheap' checks such as is this block supported below - `isSupported` does the expensive ones
            if (CollapseRecipe.canStartCollapse(level, pos) && !Support.isSupported(level, pos))
            {
                final double x = pos.getX() + random.nextDouble();
                final double y = pos.getY() - 0.05D;
                final double z = pos.getZ() + random.nextDouble();
                level.addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
