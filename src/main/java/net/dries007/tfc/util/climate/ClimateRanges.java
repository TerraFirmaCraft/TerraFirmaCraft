package net.dries007.tfc.util.climate;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.RegisteredDataManager;

public class ClimateRanges
{
    public static final Supplier<ClimateRange> CRANBERRY_BUSH = register("plant/cranberry_bush");
    public static final Map<FruitBlocks.StationaryBush, Supplier<ClimateRange>> STATIONARY_BUSHES = Helpers.mapOfKeys(FruitBlocks.StationaryBush.class, bush -> register("plant/" + bush.name() + "_bush"));

    private static RegisteredDataManager.Entry<ClimateRange> register(String name)
    {
        return ClimateRange.MANAGER.register(Helpers.identifier(name.toLowerCase(Locale.ROOT)));
    }
}
