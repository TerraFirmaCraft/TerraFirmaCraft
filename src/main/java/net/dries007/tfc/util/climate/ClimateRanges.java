/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.climate;

import java.util.Locale;
import java.util.Map;

import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.DataManager;

public final class ClimateRanges
{
    public static final DataManager.Reference<ClimateRange> BANANA_PLANT = register("plant/banana_tree");
    public static final DataManager.Reference<ClimateRange> CRANBERRY_BUSH = register("plant/cranberry_bush");
    public static final Map<FruitBlocks.StationaryBush, DataManager.Reference<ClimateRange>> STATIONARY_BUSHES = Helpers.mapOf(FruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush"));
    public static final Map<FruitBlocks.SpreadingBush, DataManager.Reference<ClimateRange>> SPREADING_BUSHES = Helpers.mapOf(FruitBlocks.SpreadingBush.class, bush -> register("plant/" + bush.name() + "_bush"));

    public static final Map<FruitBlocks.Tree, DataManager.Reference<ClimateRange>> FRUIT_TREES = Helpers.mapOf(FruitBlocks.Tree.class, tree -> register("plant/" + tree.name() + "_tree"));

    public static final Map<Crop, DataManager.Reference<ClimateRange>> CROPS = Helpers.mapOf(Crop.class, crop -> register("crop/" + crop.getSerializedName()));

    private static DataManager.Reference<ClimateRange> register(String name)
    {
        return ClimateRange.MANAGER.getReference(Helpers.identifier(name.toLowerCase(Locale.ROOT)));
    }
}
