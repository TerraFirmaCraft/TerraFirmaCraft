package net.dries007.tfc.objects;

public enum Agriculture
{
    FRUITTREE,
    BERRYBUSH,
    ;

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
    
}