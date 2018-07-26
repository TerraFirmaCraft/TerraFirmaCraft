package net.dries007.tfc.objects;

public enum Agriculture
{
    CROP,
    FRUITTREE,
    BERRYBUSH,
    ;

    public enum Crop
    {
        BARLEY(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        BEET(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        OAT(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        RICE(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        RYE(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        WHEAT(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        REDBELLPEPPER(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        YELLOWBELLPEPPER(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        CABBAGE(Temperature.TEMPERATE, 5, Soil.FARMLAND),
        CARROT(Temperature.TEMPERATE, 4, Soil.FARMLAND),
        GARLIC(Temperature.TEMPERATE, 4, Soil.FARMLAND),
        GREENBEAN(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        MAIZE(Temperature.TEMPERATE, 5, Soil.FARMLAND),
        ONION(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        POTATO(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        SOYBEAN(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        SQUASH(Temperature.TEMPERATE, 6, Soil.FARMLAND),
        SUGARCANE(Temperature.WARM,7, Soil.FARMLAND),
        TOMATO(Temperature.TEMPERATE, 7, Soil.FARMLAND),
        JUTE(Temperature.TEMPERATE, 5, Soil.FARMLAND),
        ;

        public final Temperature temperature;
        public final Soil soil;
        public final int maxStage;

        Crop(Agriculture.Temperature temperature, int maxStage, Soil soil)
        {
            this.temperature = temperature;
            this.maxStage = maxStage;
            this.soil = soil;
        }
    }

    public enum FruitTree {
        BANANA(Temperature.TEMPERATE),
        CHERRY(Temperature.TEMPERATE),
        GREEN_APPLE(Temperature.TEMPERATE),
        LEMON(Temperature.TEMPERATE),
        OLIVE(Temperature.TEMPERATE),
        ORANGE(Temperature.TEMPERATE),
        PEACH(Temperature.TEMPERATE),
        PLUM(Temperature.TEMPERATE),
        RED_APPLE(Temperature.TEMPERATE),
        ;

        public final Temperature temperature;

        FruitTree(Agriculture.Temperature temperature) {
            this.temperature = temperature;
        }
    }

    public enum BerryBush {
        BLACKBERRY(Temperature.TEMPERATE),
        BLUEBERRY(Temperature.TEMPERATE),
        BUNCHBERRY(Temperature.TEMPERATE),
        CLOUDBERRY(Temperature.TEMPERATE),
        CRANBERRY(Temperature.TEMPERATE),
        ELDERBERRY(Temperature.TEMPERATE),
        GOOSEBERRY(Temperature.TEMPERATE),
        RASPBERRY(Temperature.TEMPERATE),
        SNOWBERRY(Temperature.TEMPERATE),
        STRAWBERRY(Temperature.TEMPERATE),
        WINTERGREENBERRY(Temperature.TEMPERATE),
        ;

        public final Temperature temperature;

        BerryBush(Agriculture.Temperature temperature) {
            this.temperature = temperature;
        }
    }

    public enum Temperature {
        //TEMPERATURE FOR SPAWNING AND GROWING todo set temperature categories.
        COLD,
        COOL,
        TEMPERATE,
        WARM,
        HOT,
        ;
    }

    public enum Soil {
        FARMLAND,
        ;
    }

    //public static Soil fromState(IBlockState state) { return ()[state]; }

}