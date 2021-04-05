/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.stream.Stream;

import net.minecraft.fluid.Fluids;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;

import net.dries007.tfc.common.blocks.berrybush.SpreadingBushBlock;
import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.TFCFluids;
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

    public static final FluidProperty WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER));
    public static final FluidProperty WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, Fluids.LAVA));
    public static final FluidProperty SALT_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, TFCFluids.SALT_WATER));
    public static final FluidProperty FRESH_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER));

    public static final IntegerProperty COUNT_1_3 = IntegerProperty.create("count", 1, 3);

    public static final IntegerProperty STAGE_1 = BlockStateProperties.STAGE;
    public static final IntegerProperty STAGE_2 = IntegerProperty.create("stage", 0, 2);
    public static final IntegerProperty STAGE_3 = IntegerProperty.create("stage", 0, 3);
    public static final IntegerProperty STAGE_4 = IntegerProperty.create("stage", 0, 4);
    public static final IntegerProperty STAGE_5 = IntegerProperty.create("stage", 0, 5);
    public static final IntegerProperty STAGE_6 = IntegerProperty.create("stage", 0, 6);
    public static final IntegerProperty STAGE_7 = IntegerProperty.create("stage", 0, 7);
    public static final IntegerProperty STAGE_8 = IntegerProperty.create("stage", 0, 8);
    public static final IntegerProperty STAGE_9 = IntegerProperty.create("stage", 0, 9);
    public static final IntegerProperty STAGE_10 = IntegerProperty.create("stage", 0, 10);
    public static final IntegerProperty STAGE_11 = IntegerProperty.create("stage", 0, 11);
    public static final IntegerProperty STAGE_12 = IntegerProperty.create("stage", 0, 12);

    public static final IntegerProperty SAPLINGS = IntegerProperty.create("saplings", 1, 4);

    public static final IntegerProperty[] STAGES = {STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, STAGE_6, STAGE_7, STAGE_8, STAGE_9, STAGE_10, STAGE_11, STAGE_12};

    public static final IntegerProperty AGE_3 = IntegerProperty.create("age", 0, 3);

    public static final EnumProperty<ITallPlant.Part> TALL_PLANT_PART = EnumProperty.create("part", ITallPlant.Part.class);
    public static final EnumProperty<RockSpikeBlock.Part> ROCK_SPIKE_PART = EnumProperty.create("part", RockSpikeBlock.Part.class);
    public static final EnumProperty<SpreadingBushBlock.Lifecycle> LIFECYCLE = EnumProperty.create("lifecycle", SpreadingBushBlock.Lifecycle.class);

    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public static final BooleanProperty WILD = BooleanProperty.create("wild");
    public static final BooleanProperty GROWING = BooleanProperty.create("growing");

    // for placed items
    public static final BooleanProperty ITEM_0 = BooleanProperty.create("item_0");
    public static final BooleanProperty ITEM_1 = BooleanProperty.create("item_1");
    public static final BooleanProperty ITEM_2 = BooleanProperty.create("item_2");
    public static final BooleanProperty ITEM_3 = BooleanProperty.create("item_3");

    public static final IntegerProperty PIT_KILN_STAGE = IntegerProperty.create("stage", 0, 16);
}