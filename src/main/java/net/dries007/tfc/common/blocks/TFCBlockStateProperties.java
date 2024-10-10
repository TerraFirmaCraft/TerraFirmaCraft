/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.stream.Stream;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

import net.dries007.tfc.common.blocks.crop.DoubleCropBlock;
import net.dries007.tfc.common.blocks.devices.TFCComposterBlock;
import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.rock.RockSpikeBlock;
import net.dries007.tfc.common.blocks.rotation.CrankshaftBlock;
import net.dries007.tfc.common.blocks.wood.BranchDirection;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.world.river.Flow;

/**
 * @see BlockStateProperties
 */
public class TFCBlockStateProperties
{
    public static final IntegerProperty DISTANCE_9 = IntegerProperty.create("distance", 1, 9);

    public static final FluidProperty WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER));
    public static final FluidProperty ALL_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, TFCFluids.SPRING_WATER));

    // Note that when using properties containing lava, blocks should override `randomTick` and `isRandomlyTicking` in accordance
    // with `LiquidBlock`, in order to handle lava based effects. With only water, this isn't super necessary.
    public static final FluidProperty ALL_WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, TFCFluids.SPRING_WATER, Fluids.LAVA));
    public static final FluidProperty WATER_AND_LAVA = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER, TFCFluids.SALT_WATER, Fluids.LAVA));
    public static final FluidProperty SALT_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, TFCFluids.SALT_WATER));
    public static final FluidProperty FRESH_WATER = FluidProperty.create("fluid", Stream.of(Fluids.EMPTY, Fluids.WATER));

    public static final IntegerProperty COUNT_1_3 = IntegerProperty.create("count", 1, 3);
    public static final IntegerProperty COUNT_1_4 = IntegerProperty.create("count", 1, 4);
    public static final IntegerProperty COUNT_1_5 = IntegerProperty.create("count", 1, 5);
    public static final IntegerProperty COUNT_1_16 = IntegerProperty.create("count", 1, 16);
    public static final IntegerProperty COUNT_1_36 = IntegerProperty.create("count", 1, 36);
    public static final IntegerProperty COUNT_1_64 = IntegerProperty.create("count", 1, 64);

    public static final IntegerProperty LAYERS_4 = IntegerProperty.create("layers", 1, 4);
    public static final IntegerProperty SMOKE_LEVEL = IntegerProperty.create("smoke_level", 0, 4);

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

    public static final IntegerProperty AGE_1 = BlockStateProperties.AGE_1;
    public static final IntegerProperty AGE_2 = BlockStateProperties.AGE_2;
    public static final IntegerProperty AGE_3 = BlockStateProperties.AGE_3;
    public static final IntegerProperty AGE_4 = IntegerProperty.create("age", 0, 4);
    public static final IntegerProperty AGE_5 = BlockStateProperties.AGE_5;
    public static final IntegerProperty AGE_6 = IntegerProperty.create("age", 0, 6);
    public static final IntegerProperty AGE_7 = BlockStateProperties.AGE_7;
    public static final IntegerProperty AGE_8 = IntegerProperty.create("age", 0, 8);

    public static final IntegerProperty SAPLINGS = IntegerProperty.create("saplings", 1, 4);

    public static final EnumProperty<DoubleCropBlock.Part> DOUBLE_CROP_PART = EnumProperty.create("part", DoubleCropBlock.Part.class);
    public static final EnumProperty<ITallPlant.Part> TALL_PLANT_PART = EnumProperty.create("part", ITallPlant.Part.class);
    public static final EnumProperty<RockSpikeBlock.Part> ROCK_SPIKE_PART = EnumProperty.create("part", RockSpikeBlock.Part.class);
    public static final EnumProperty<CrankshaftBlock.Part> CRANKSHAFT_PART = EnumProperty.create("part", CrankshaftBlock.Part.class);
    public static final EnumProperty<Lifecycle> LIFECYCLE = EnumProperty.create("lifecycle", Lifecycle.class);
    public static final EnumProperty<TFCComposterBlock.CompostType> COMPOST_TYPE = EnumProperty.create("type", TFCComposterBlock.CompostType.class);
    public static final EnumProperty<BranchDirection> BRANCH_DIRECTION = EnumProperty.create("branch_direction", BranchDirection.class);
    public static final DirectionProperty FACING_NOT_DOWN = DirectionProperty.create("facing", s -> s != Direction.DOWN);

    public static final BooleanProperty TIP = BooleanProperty.create("tip");
    public static final BooleanProperty UPPER = BooleanProperty.create("upper");
    public static final BooleanProperty STICK = BooleanProperty.create("stick");
    public static final BooleanProperty MATURE = BooleanProperty.create("mature");
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    public static final BooleanProperty GROWS_BRANCHES = BooleanProperty.create("grows_branches");
    public static final BooleanProperty SEALED = BooleanProperty.create("sealed");
    public static final BooleanProperty DRIED = BooleanProperty.create("dried");
    public static final BooleanProperty HAS_HANDSTONE = BooleanProperty.create("has_handstone");
    public static final BooleanProperty RACK = BooleanProperty.create("rack");
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final BooleanProperty WAXED = BooleanProperty.create("waxed");
    public static final BooleanProperty FLAT = BooleanProperty.create("flat");

    // for placed items
    public static final BooleanProperty ITEM_0 = BooleanProperty.create("item_0");
    public static final BooleanProperty ITEM_1 = BooleanProperty.create("item_1");
    public static final BooleanProperty ITEM_2 = BooleanProperty.create("item_2");
    public static final BooleanProperty ITEM_3 = BooleanProperty.create("item_3");

    // wattle
    public static final BooleanProperty TOP = BooleanProperty.create("top");
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    public static final BooleanProperty WATTLE_WOVEN = BooleanProperty.create("woven");

    public static final IntegerProperty PIT_KILN_STAGE = IntegerProperty.create("stage", 0, 16);
    public static final IntegerProperty HEAT_LEVEL = IntegerProperty.create("heat_level", 0, 7);

    public static final EnumProperty<Flow> FLOW = EnumProperty.create("flow", Flow.class);
    public static final BooleanProperty MIRROR = BooleanProperty.create("mirror");

    private static final IntegerProperty[] STAGES = {STAGE_1, STAGE_2, STAGE_3, STAGE_4, STAGE_5, STAGE_6, STAGE_7, STAGE_8, STAGE_9, STAGE_10, STAGE_11, STAGE_12};
    private static final IntegerProperty[] AGES = {AGE_1, AGE_2, AGE_3, AGE_4, AGE_5, AGE_6, AGE_7, AGE_8};

    public static IntegerProperty getStageProperty(int maxStage)
    {
        if (maxStage > 0 && maxStage <= STAGES.length)
        {
            return STAGES[maxStage - 1];
        }
        throw new IllegalArgumentException("No stage property for stages [0, " + maxStage + "]");
    }

    public static IntegerProperty getAgeProperty(int maxAge)
    {
        if (maxAge > 0 && maxAge <= AGES.length)
        {
            return AGES[maxAge - 1];
        }
        throw new IllegalArgumentException("No age property for ages [0, " + maxAge + "]");
    }
}