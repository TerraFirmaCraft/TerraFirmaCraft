/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.util.climate.ClimateRange;
import net.dries007.tfc.util.data.DataManager;

import static net.dries007.tfc.common.blocks.crop.Crop.*;
import static net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks.SpreadingBush.*;
import static net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks.StationaryBush.*;
import static net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks.Tree.*;
import static net.dries007.tfc.util.climate.ClimateRanges.*;

public class BuiltinClimateRanges extends DataManagerProvider<ClimateRange>
{
    public BuiltinClimateRanges(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ClimateRange.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(SPREADING_BUSHES, BLACKBERRY, b -> b.minHydration(24).temperature(7, 24));
        add(SPREADING_BUSHES, RASPBERRY, b -> b.minHydration(24).temperature(5, 25));
        add(SPREADING_BUSHES, BLUEBERRY, b -> b.minHydration(12).temperature(7, 29));
        add(SPREADING_BUSHES, ELDERBERRY, b -> b.minHydration(12).temperature(10, 33));
        add(STATIONARY_BUSHES, SNOWBERRY, b -> b.minHydration(24).temperature(-7, 18));
        add(STATIONARY_BUSHES, BUNCHBERRY, b -> b.minHydration(24).temperature(15, 35));
        add(STATIONARY_BUSHES, GOOSEBERRY, b -> b.minHydration(24).temperature(5, 27));
        add(STATIONARY_BUSHES, CLOUDBERRY, b -> b.minHydration(10).temperature(-2, 17));
        add(STATIONARY_BUSHES, STRAWBERRY, b -> b.minHydration(12).temperature(5, 28));
        add(STATIONARY_BUSHES, WINTERGREEN_BERRY, b -> b.minHydration(12).temperature(-6, 17));
        add(CRANBERRY_BUSH, new ClimateRange.Builder().minHydration(30).temperature(-5, 17).build());
        add(FRUIT_TREES, CHERRY, b -> b.minHydration(60).temperature(10, 42));
        add(FRUIT_TREES, GREEN_APPLE, b -> b.minHydration(42).temperature(-2, 32));
        add(FRUIT_TREES, LEMON, b -> b.minHydration(33).temperature(-6, 32));
        add(FRUIT_TREES, OLIVE, b -> b.minHydration(56).temperature( 3, 37));
        add(FRUIT_TREES, ORANGE, b -> b.minHydration(60).temperature(2, 37));
        add(FRUIT_TREES, PEACH, b -> b.minHydration(60).temperature(8, 43));
        add(FRUIT_TREES, PLUM, b -> b.minHydration(27).temperature(-3, 34));
        add(FRUIT_TREES, RED_APPLE, b -> b.minHydration(48).temperature(8, 38));
        add(BANANA_PLANT, new ClimateRange.Builder().minHydration(34).temperature(10, 42).build());
        add(CROPS, BARLEY, b -> b.hydration(18, 75).temperature(-8, 26));
        add(CROPS, OAT, b -> b.hydration(35, 100).temperature(3, 40));
        add(CROPS, RYE, b -> b.hydration(25, 85).temperature(-11, 30));
        add(CROPS, MAIZE, b -> b.hydration(75, 100).temperature(13, 40));
        add(CROPS, WHEAT, b -> b.hydration(25, 100).temperature(-4, 35));
        add(CROPS, RICE, b -> b.hydration(25, 100).temperature(15, 30));
        add(CROPS, BEET, b -> b.hydration(18, 85).temperature(-5, 20));
        add(CROPS, CABBAGE, b -> b.hydration(15, 65).temperature(-10, 27));
        add(CROPS, CARROT, b -> b.hydration(25, 100).temperature(3, 30));
        add(CROPS, GARLIC, b -> b.hydration(15, 75).temperature(-20, 18));
        add(CROPS, GREEN_BEAN, b -> b.hydration(38, 100).temperature(2, 35));
        add(CROPS, POTATO, b -> b.hydration(50, 100).temperature(1, 37));
        add(CROPS, ONION, b -> b.hydration(25, 90).temperature(0, 30));
        add(CROPS, SOYBEAN, b -> b.hydration(40, 100).temperature(8, 30));
        add(CROPS, SQUASH, b -> b.hydration(23, 95).temperature(5, 33));
        add(CROPS, SUGARCANE, b -> b.hydration(40, 100).temperature(12, 38));
        add(CROPS, TOMATO, b -> b.hydration(30, 95).temperature(0, 36));
        add(CROPS, JUTE, b -> b.hydration(25, 100).temperature(5, 37));
        add(CROPS, PAPYRUS, b -> b.hydration(70, 100).temperature(19, 37));
        add(CROPS, PUMPKIN, b -> b.hydration(30, 70).temperature(5, 22));
        add(CROPS, MELON, b -> b.hydration(75, 100).temperature(19, 35));
        add(CROPS, RED_BELL_PEPPER, b -> b.hydration(25, 60).temperature(16, 30));
        add(CROPS, YELLOW_BELL_PEPPER, b -> b.hydration(25, 60).temperature(16, 30));
    }

    private <T> void add(Map<T, DataManager.Reference<ClimateRange>> map, T value, UnaryOperator<ClimateRange.Builder> builder)
    {
        add(map.get(value),  builder.apply(new ClimateRange.Builder()).build());
    }
}
