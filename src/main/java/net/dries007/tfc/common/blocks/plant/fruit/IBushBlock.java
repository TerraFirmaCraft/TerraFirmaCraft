/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.calendar.ICalendar;

/**
 * Marker interface for common bush-type blocks.
 * These do random tick updates that are on average 1/day, and use time tracking to implement fast forwarding.
 */
public interface IBushBlock
{
    /**
     * Target average delay between random ticks = one day.
     * Ticks are done at a rate of randomTickSpeed ticks / chunk section / world tick.
     *<p>
     * Only implement if ticking is not desired every tick, ie that we're OK with staggered updating.
     */
    static void randomTick(IBushBlock bush, BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        final int rarity = Math.max(1, (int) (ICalendar.TICKS_IN_DAY * level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING) * (1 / 4096f)));
        if (random.nextInt(rarity) == 0)
        {
            bush.onUpdate(level, pos, state);
        }
    }

    void onUpdate(Level level, BlockPos pos, BlockState state);
}
