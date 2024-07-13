/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.devices;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

public class JackOLanternBlock extends CarvedPumpkinBlock implements EntityBlockExtension, IForgeBlockExtension
{
    private final ExtendedProperties properties;
    private final Supplier<? extends Block> dead;

    public JackOLanternBlock(ExtendedProperties properties, Supplier<? extends Block> dead)
    {
        super(properties.properties());
        this.properties = properties;
        this.dead = dead;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            final int jackTicks = TFCConfig.SERVER.jackOLanternTicks.get();
            if (counter.getTicksSinceUpdate() > jackTicks && jackTicks > 0)
            {
                level.setBlockAndUpdate(pos, Helpers.copyProperty(dead.get().defaultBlockState(), state, HorizontalDirectionalBlock.FACING));
            }
        }
    }

    public void extinguish(Level level, BlockPos pos, BlockState state)
    {
        level.setBlockAndUpdate(pos, Helpers.copyProperty(dead.get().defaultBlockState(), state, HorizontalDirectionalBlock.FACING));
    }
}
