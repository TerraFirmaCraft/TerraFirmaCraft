/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;

public class DeadWallTorchBlock extends WallTorchBlock implements Lightable
{
    public DeadWallTorchBlock(Properties properties, ParticleOptions particle)
    {
        super(properties, particle);
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {}

    @Override
    public boolean lightBlock(Level level, BlockState state, BlockPos pos, boolean isStrong, @Nullable Entity entity)
    {
        level.setBlockAndUpdate(pos, TFCBlocks.WALL_TORCH.get().withPropertiesOf(state));
        level.getBlockEntity(pos, TFCBlockEntities.TICK_COUNTER.get()).ifPresent(TickCounterBlockEntity::resetCounter);
        return true;
    }
}
