/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks;

import java.util.stream.Stream;

import net.minecraft.fluid.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;

import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.util.calendar.Season;

/**
 * @see net.minecraft.state.properties.BlockStateProperties
 */
public class TFCBlockStateProperties
{
    public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");

    public static final EnumProperty<Season> SEASON = EnumProperty.create("season", Season.class);
    public static final EnumProperty<Season> SEASON_NO_SPRING = EnumProperty.create("season", Season.class, Season.SUMMER, Season.FALL, Season.WINTER);

    public static final IntegerProperty DISTANCE_7 = BlockStateProperties.DISTANCE;
    public static final IntegerProperty DISTANCE_8 = IntegerProperty.create("distance", 1, 8);
    public static final IntegerProperty DISTANCE_9 = IntegerProperty.create("distance", 1, 9);
    public static final IntegerProperty DISTANCE_10 = IntegerProperty.create("distance", 1, 10);

    public static final IntegerProperty[] DISTANCES = {DISTANCE_7, DISTANCE_8, DISTANCE_9, DISTANCE_10};

    public static final FluidProperty WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER));
    public static final FluidProperty WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, Fluids.LAVA));

    public static final IntegerProperty COUNT_1_3 = IntegerProperty.create("count", 1, 3);
}