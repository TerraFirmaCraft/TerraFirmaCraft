/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.calendar.ICalendar;

// todo: this is bad because I'm mid-rework of bushes but it'll do for now.
public interface IBushBlock
{
    void onUpdate(Level level, BlockPos pos, BlockState state);

    /**
     * Target average delay between random ticks = one day.
     * Ticks are done at a rate of randomTickSpeed ticks / chunk section / world tick.
     *
     * Only implement if ticking is not desired every tick, ie that we're OK with staggered updating.
     */
    default void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        final int rarity = Math.max(1, (int) (ICalendar.TICKS_IN_DAY * level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) * (1 / 4096f)));
        if (random.nextInt(rarity) == 0)
        {
            onUpdate(level, pos, state);
        }
    }
}
