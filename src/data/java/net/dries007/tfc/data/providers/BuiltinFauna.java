/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data.providers;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.UnaryOperator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.common.entities.Fauna;
import net.dries007.tfc.common.entities.Faunas;
import net.dries007.tfc.common.entities.aquatic.Fish;
import net.dries007.tfc.util.calendar.Month;

public class BuiltinFauna extends DataManagerProvider<Fauna>
{
    private static final List<Month> FRESHWATER_FISH_MONTHS = List.of(Month.MAY, Month.JUNE, Month.JULY, Month.AUGUST, Month.SEPTEMBER, Month.OCTOBER);

    public BuiltinFauna(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(Fauna.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(Faunas.ISOPOD, b -> b.distanceBelowSeaLevel(20).maxTemperature(14));
        add(Faunas.CRAYFISH, b -> b.distanceBelowSeaLevel(2).minTemperature(5).minRainfall(125));
        add(Faunas.LOBSTER, b -> b.distanceBelowSeaLevel(1).maxTemperature(21));
        add(Faunas.HORSESHOE_CRAB, b -> b.distanceBelowSeaLevel(1).temperature(10, 21).maxRainfall(400));
        add(Faunas.COD, b -> b.distanceBelowSeaLevel(5).maxTemperature(18));
        add(Faunas.PUFFERFISH, b -> b.distanceBelowSeaLevel(3).minTemperature(10));
        add(Faunas.TROPICAL_FISH, b -> b.distanceBelowSeaLevel(3).minTemperature(18));
        add(Faunas.JELLYFISH, b -> b.distanceBelowSeaLevel(3).minTemperature(18));
        add(Faunas.ORCA, b -> b.distanceBelowSeaLevel(6).maxTemperature(19).minRainfall(100).chance(10));
        add(Faunas.DOLPHIN, b -> b.distanceBelowSeaLevel(6).minTemperature(10).minRainfall(200).chance(10));
        add(Faunas.MANATEE, b -> b.distanceBelowSeaLevel(3).minTemperature(20).minRainfall(300).chance(10));
        add(Faunas.CROCODILE, b -> b.distanceBelowSeaLevel(0).minTemperature(15));
        add(Faunas.FISH.get(Fish.BLUEGILL), b -> b.temperature(-10, 26).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.CRAPPIE), b -> b.temperature(-10, 26).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.LAKE_TROUT), b -> b.maxTemperature(23).minRainfall(250).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.RAINBOW_TROUT), b -> b.maxTemperature(10).minRainfall(150).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.LARGEMOUTH_BASS), b -> b.temperature(-14, 20).rainfall(100, 400).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.SMALLMOUTH_BASS), b -> b.temperature(-14, 20).rainfall(100, 400).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.FISH.get(Fish.SALMON), b -> b.minTemperature(-5).months(FRESHWATER_FISH_MONTHS));
        add(Faunas.PENGUIN, b -> b.maxTemperature(-14).minRainfall(75));
        add(Faunas.FROG, b -> b.minRainfall(150).minTemperature(-13));
        add(Faunas.TURTLE, b -> b.minTemperature(21).minRainfall(250));
        add(Faunas.POLAR_BEAR, b -> b.maxTemperature(-10).minRainfall(100));
        add(Faunas.GRIZZLY_BEAR, b -> b.temperature(-15, 15).minRainfall(200).minForest(2));
        add(Faunas.BLACK_BEAR, b -> b.temperature(5, 20).minRainfall(250).minForest(2));
        add(Faunas.COUGAR, b -> b.temperature(-10, 21).minRainfall(150));
        add(Faunas.PANTHER, b -> b.temperature(-10, 21).minRainfall(150));
        add(Faunas.LION, b -> b.minTemperature(16).rainfall(50, 300).maxForest(2));
        add(Faunas.SABERTOOTH, b -> b.maxTemperature(0).minRainfall(250));
        add(Faunas.TIGER, b -> b.minTemperature(13).minRainfall(100).minForest(2));
        add(Faunas.SQUID, b -> b.distanceBelowSeaLevel(15));
        add(Faunas.OCTOPOTEUTHIS, b -> b.maxBrightness(0).distanceBelowSeaLevel(33));
        add(Faunas.PIG, b -> b.temperature(-10, 35).minRainfall(200).minForest(2));
        add(Faunas.COW, b -> b.temperature(-10, 35).minRainfall(250));
        add(Faunas.GOAT, b -> b.temperature(-12, 25).maxRainfall(300));
        add(Faunas.YAK, b -> b.maxTemperature(-11).minRainfall(100));
        add(Faunas.ALPACA, b -> b.temperature(-8, 20).minRainfall(250));
        add(Faunas.SHEEP, b -> b.temperature(0, 35).rainfall(70, 300));
        add(Faunas.MUSK_OX, b -> b.temperature(-25, 0).minRainfall(100));
        add(Faunas.CHICKEN, b -> b.minTemperature(14).minRainfall(225).minForest(2));
        add(Faunas.DUCK, b -> b.temperature(-25, 30).minRainfall(100).maxForest(2));
        add(Faunas.QUAIL, b -> b.temperature(-15, 10).minRainfall(200));
        add(Faunas.RABBIT, b -> b.minRainfall(15));
        add(Faunas.FOX, b -> b.rainfall(130, 400).maxTemperature(25).minForest(2));
        add(Faunas.PANDA, b -> b.temperature(18, 28).rainfall(300, 500).minForest(3).fuzzy());
        add(Faunas.BOAR, b -> b.rainfall(130, 400).temperature(-5, 25).maxForest(3));
        add(Faunas.WILDEBEEST, b -> b.rainfall(90, 380).minTemperature(13).maxForest(2));
        add(Faunas.OCELOT, b -> b.rainfall(300, 500).temperature(15, 30).minForest(2));
        add(Faunas.CARIBOU, b -> b.rainfall(110, 500).maxTemperature(-9));
        add(Faunas.DEER, b -> b.rainfall(160, 500).temperature(-12, 16).minForest(2));
        add(Faunas.GAZELLE, b -> b.rainfall(90, 380).minTemperature(12).maxForest(2));
        add(Faunas.BONGO, b -> b.rainfall(230, 500).minTemperature(15).minForest(2));
        add(Faunas.MOOSE, b -> b.rainfall(150, 300).temperature(-15, 10).minForest(2));
        add(Faunas.GROUSE, b -> b.rainfall(150, 400).temperature(-12, 13));
        add(Faunas.PHEASANT, b -> b.rainfall(100, 300).temperature(-5, 17).minForest(2));
        add(Faunas.TURKEY, b -> b.rainfall(250, 450).temperature(0, 17).maxForest(3));
        add(Faunas.PEAFOWL, b -> b.rainfall(190, 500).minTemperature(14).minForest(3));
        add(Faunas.WOLF, b -> b.rainfall(150, 420).temperature(-12, 17).maxForest(3));
        add(Faunas.HYENA, b -> b.rainfall(80, 380).minTemperature(15).maxForest(3));
        add(Faunas.DIREWOLF, b -> b.rainfall(150, 420).maxTemperature(-5).maxForest(3));
        add(Faunas.DONKEY, b -> b.rainfall(130, 400).minTemperature(-15).maxForest(2));
        add(Faunas.MULE, b -> b.rainfall(130, 400).minTemperature(-15).maxForest(2));
        add(Faunas.HORSE, b -> b.rainfall(130, 400).minTemperature(-15).maxForest(2));
    }
    
    private void add(Faunas.Id<?> fauna, UnaryOperator<Fauna.Builder> builder)
    {
        add(fauna.fauna(), builder.apply(new Fauna.Builder()).build());
    }
}
