/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;

import net.dries007.tfc.util.calendar.Season;

/**
 * @see net.minecraft.state.properties.BlockStateProperties
 */
public class TFCBlockStateProperties
{
    public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");

    public static final EnumProperty<Season> SEASON = EnumProperty.create("season", Season.class);
    public static final EnumProperty<Season> SEASON_NO_SPRING = EnumProperty.create("season", Season.class, Season.SUMMER, Season.FALL, Season.WINTER);

    public static final IntegerProperty DISTANCE_1_6 = IntegerProperty.create("distance", 1, 6);
    public static final IntegerProperty DISTANCE_1_7 = BlockStateProperties.DISTANCE;
    public static final IntegerProperty DISTANCE_1_8 = IntegerProperty.create("distance", 1, 6);
}