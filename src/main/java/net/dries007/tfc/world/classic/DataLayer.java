/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic;

import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;

/**
 * Todo: Rewrite to make typesafe ?
 */
@SuppressWarnings("WeakerAccess")
public final class DataLayer
{
    private static final DataLayer[] LAYERS = new DataLayer[256];
    public static final DataLayer ERROR = new DataLayer(-1, null, "ERROR", Integer.MIN_VALUE, Float.NaN);
    public static final DataLayer GRANITE = newBlockDataLayer(0, BlockRockVariant.get(Rock.GRANITE, Rock.Type.RAW), "Granite");
    public static final DataLayer DIORITE = newBlockDataLayer(1, BlockRockVariant.get(Rock.DIORITE, Rock.Type.RAW), "Diorite");
    public static final DataLayer GABBRO = newBlockDataLayer(2, BlockRockVariant.get(Rock.GABBRO, Rock.Type.RAW), "Gabbro");
    public static final DataLayer SHALE = newBlockDataLayer(5, BlockRockVariant.get(Rock.SHALE, Rock.Type.RAW), "Shale");
    public static final DataLayer CLAYSTONE = newBlockDataLayer(6, BlockRockVariant.get(Rock.CLAYSTONE, Rock.Type.RAW), "Claystone");
    public static final DataLayer ROCKSALT = newBlockDataLayer(7, BlockRockVariant.get(Rock.ROCKSALT, Rock.Type.RAW), "Rock Salt");
    public static final DataLayer LIMESTONE = newBlockDataLayer(8, BlockRockVariant.get(Rock.LIMESTONE, Rock.Type.RAW), "Limestone");
    public static final DataLayer CONGLOMERATE = newBlockDataLayer(9, BlockRockVariant.get(Rock.CONGLOMERATE, Rock.Type.RAW), "Conglomerate");
    public static final DataLayer DOLOMITE = newBlockDataLayer(10, BlockRockVariant.get(Rock.DOLOMITE, Rock.Type.RAW), "Dolomite");
    public static final DataLayer CHERT = newBlockDataLayer(11, BlockRockVariant.get(Rock.CHERT, Rock.Type.RAW), "Chert");
    public static final DataLayer CHALK = newBlockDataLayer(12, BlockRockVariant.get(Rock.CHALK, Rock.Type.RAW), "Chalk");
    public static final DataLayer RHYOLITE = newBlockDataLayer(13, BlockRockVariant.get(Rock.RHYOLITE, Rock.Type.RAW), "Rhyolite");
    public static final DataLayer BASALT = newBlockDataLayer(14, BlockRockVariant.get(Rock.BASALT, Rock.Type.RAW), "Basalt");
    public static final DataLayer ANDESITE = newBlockDataLayer(15, BlockRockVariant.get(Rock.ANDESITE, Rock.Type.RAW), "Andesite");
    public static final DataLayer DACITE = newBlockDataLayer(16, BlockRockVariant.get(Rock.DACITE, Rock.Type.RAW), "Dacite");
    public static final DataLayer QUARTZITE = newBlockDataLayer(17, BlockRockVariant.get(Rock.QUARTZITE, Rock.Type.RAW), "Quartzite");
    public static final DataLayer SLATE = newBlockDataLayer(18, BlockRockVariant.get(Rock.SLATE, Rock.Type.RAW), "Slate");
    public static final DataLayer PHYLLITE = newBlockDataLayer(19, BlockRockVariant.get(Rock.PHYLLITE, Rock.Type.RAW), "Phyllite");
    public static final DataLayer SCHIST = newBlockDataLayer(20, BlockRockVariant.get(Rock.SCHIST, Rock.Type.RAW), "Schist");
    public static final DataLayer GNEISS = newBlockDataLayer(21, BlockRockVariant.get(Rock.GNEISS, Rock.Type.RAW), "Gneiss");
    public static final DataLayer MARBLE = newBlockDataLayer(22, BlockRockVariant.get(Rock.MARBLE, Rock.Type.RAW), "Marble");
    public static final DataLayer NO_TREE = newIntDataLayer(29, "No Tree", -1);
    public static final DataLayer ASH = newIntDataLayer(30, "Ash", 7);
    public static final DataLayer ASPEN = newIntDataLayer(31, "Aspen", 1);
    public static final DataLayer BIRCH = newIntDataLayer(32, "Birch", 2);
    public static final DataLayer CHESTNUT = newIntDataLayer(33, "Chestnut", 3);
    public static final DataLayer DOUGLASFIR = newIntDataLayer(34, "Douglas Fir", 4);
    public static final DataLayer HICKORY = newIntDataLayer(35, "Hickory", 5);
    public static final DataLayer KOA = newIntDataLayer(45, "Acacia Koa", 0);
    public static final DataLayer MAPLE = newIntDataLayer(36, "Maple", 6);
    public static final DataLayer OAK = newIntDataLayer(37, "Oak", 0);
    public static final DataLayer PINE = newIntDataLayer(38, "Pine", 8);
    public static final DataLayer REDWOOD = newIntDataLayer(39, "Sequoia", 9);
    public static final DataLayer SPRUCE = newIntDataLayer(40, "Spruce", 10);
    public static final DataLayer SYCAMORE = newIntDataLayer(41, "Sycamore", 11);
    public static final DataLayer SAVANNAHACACIA = newIntDataLayer(46, "Acacia Savannah", 0);
    public static final DataLayer WHITECEDAR = newIntDataLayer(42, "White Cedar", 12);
    public static final DataLayer WHITEELM = newIntDataLayer(43, "White Elm", 13);
    public static final DataLayer WILLOW = newIntDataLayer(44, "Willow", 14);
    public static final DataLayer EVT_0_125 = newFloatDataLayer(80, "0.125", 0.125f);
    public static final DataLayer EVT_0_25 = newFloatDataLayer(81, "0.25", 0.25f);
    public static final DataLayer EVT_0_5 = newFloatDataLayer(82, "0.5", 0.5f);
    public static final DataLayer EVT_1 = newFloatDataLayer(83, "1", 1f);
    public static final DataLayer EVT_2 = newFloatDataLayer(84, "2", 2f);
    public static final DataLayer EVT_4 = newFloatDataLayer(85, "4", 4f);
    public static final DataLayer EVT_8 = newFloatDataLayer(86, "8", 8f);
    public static final DataLayer EVT_16 = newFloatDataLayer(87, "16", 16f);
    public static final DataLayer RAIN_62_5 = newFloatDataLayer(90, "62.5", 62.5f);
    public static final DataLayer RAIN_125 = newFloatDataLayer(91, "125", 125f);
    public static final DataLayer RAIN_250 = newFloatDataLayer(92, "250", 250f);
    public static final DataLayer RAIN_500 = newFloatDataLayer(93, "500", 500f);
    public static final DataLayer RAIN_1000 = newFloatDataLayer(94, "1000", 1000f);
    public static final DataLayer RAIN_2000 = newFloatDataLayer(95, "2000", 2000f);
    public static final DataLayer RAIN_4000 = newFloatDataLayer(96, "4000", 4000f);
    public static final DataLayer RAIN_8000 = newFloatDataLayer(97, "8000", 8000f);
    public static final DataLayer SEISMIC_STABLE = newIntDataLayer(110, "Stable", 0);
    public static final DataLayer SEISMIC_UNSTABLE = newIntDataLayer(111, "Unstable", 1);
    public static final DataLayer DRAINAGE_NONE = newIntDataLayer(120, "None", 0);
    public static final DataLayer DRAINAGE_VERY_POOR = newIntDataLayer(121, "Very Poor", 1);
    public static final DataLayer DRAINAGE_POOR = newIntDataLayer(122, "Poor", 2);
    public static final DataLayer DRAINAGE_NORMAL = newIntDataLayer(123, "Normal", 3);
    public static final DataLayer DRAINAGE_GOOD = newIntDataLayer(124, "Good", 4);
    public static final DataLayer DRAINAGE_VERY_GOOD = newIntDataLayer(125, "Very Good", 5);
    public static final DataLayer PH_ACID_HIGH = newIntDataLayer(130, "High Acidity", 0);
    public static final DataLayer PH_ACID_LOW = newIntDataLayer(131, "Low acidity", 1);
    public static final DataLayer PH_NEUTRAL = newIntDataLayer(132, "Neutral", 2);
    public static final DataLayer PH_ALKALINE_LOW = newIntDataLayer(133, "Low Alkalinity", 3);
    public static final DataLayer PH_ALKALINE_HIGH = newIntDataLayer(134, "High Alkalinity", 4);

    public static DataLayer get(int i)
    {
        if (LAYERS[i] == null) throw new IllegalArgumentException("Layer " + i + " not used.");
        return LAYERS[i];
    }

    private static DataLayer newBlockDataLayer(int i, BlockRockVariant block, String name)
    {
        if (LAYERS[i] != null) throw new IllegalArgumentException("Layer " + i + " already in use.");
        return LAYERS[i] = new DataLayer(i, block, name, Integer.MIN_VALUE, Float.NaN);
    }

    private static DataLayer newIntDataLayer(int i, String name, int value)
    {
        if (LAYERS[i] != null) throw new IllegalArgumentException("Layer " + i + " already in use.");
        return LAYERS[i] = new DataLayer(i, null, name, value, Float.NaN);
    }

    private static DataLayer newFloatDataLayer(int i, String name, float value)
    {
        if (LAYERS[i] != null) throw new IllegalArgumentException("Layer " + i + " already in use.");
        return LAYERS[i] = new DataLayer(i, null, name, Integer.MIN_VALUE, value);
    }

    public final int layerID;
    public final BlockRockVariant block;
    public final String name;
    public final int valueInt;
    public final float valueFloat;

    private DataLayer(int i, BlockRockVariant block, String name, int valueInt, float valueFloat)
    {
        this.layerID = i;
        this.block = block;
        this.name = name;
        this.valueInt = valueInt;
        this.valueFloat = valueFloat;
    }
}
