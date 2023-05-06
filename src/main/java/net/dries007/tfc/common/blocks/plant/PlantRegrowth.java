/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Season;

public final class PlantRegrowth
{
    public static boolean canSpread(Level level, Random random)
    {
        return random.nextFloat() < TFCConfig.SERVER.plantSpreadChance.get() && Calendars.get(level).getCalendarMonthOfYear().getSeason() != Season.WINTER;
    }

    /**
     * @param selfSpreadRange the max distance the plant will attempt to spread
     * @param radius          the square radius that the plant will check for tagged plants to prevent over-densification
     * @param maxPlants       the max amount of plants within the radius that are allowed before spreading is denied
     * @return                a {@linkplain BlockPos} if we have a place to put it.
     */
    @Nullable
    public static BlockPos spreadSelf(BlockState state, ServerLevel level, BlockPos pos, Random random, int selfSpreadRange, int radius, int maxPlants)
    {
        final BlockPos newPos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random), Mth.nextInt(random, 1, selfSpreadRange));
        if (level.getBlockState(newPos).isAir() && state.canSurvive(level, newPos))
        {
            int plants = 0;
            final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    cursor.setWithOffset(newPos, x, 0, z);
                    if (!level.isLoaded(cursor))
                    {
                        return null;
                    }
                    if (Helpers.isBlock(level.getBlockState(cursor), TFCTags.Blocks.PLANTS))
                    {
                        if (plants++ > maxPlants)
                        {
                            return null;
                        }
                    }
                }
            }
            return newPos;
        }
        return null;
    }
}
