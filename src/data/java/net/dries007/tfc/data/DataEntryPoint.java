/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data;

import java.util.Set;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.data.providers.BuiltinBlockTags;
import net.dries007.tfc.data.providers.BuiltinDensityFunctions;
import net.dries007.tfc.data.providers.BuiltinDrinkables;
import net.dries007.tfc.data.providers.BuiltinFluidHeat;
import net.dries007.tfc.data.providers.BuiltinFluidTags;
import net.dries007.tfc.data.providers.BuiltinFuels;
import net.dries007.tfc.data.providers.BuiltinItemHeat;
import net.dries007.tfc.data.providers.BuiltinItemSizes;
import net.dries007.tfc.data.providers.BuiltinItemTags;
import net.dries007.tfc.data.providers.BuiltinKnappingTypes;
import net.dries007.tfc.data.providers.BuiltinLampFuels;
import net.dries007.tfc.data.providers.BuiltinRecipes;
import net.dries007.tfc.data.providers.BuiltinSupports;

import static net.dries007.tfc.TerraFirmaCraft.*;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DataEntryPoint
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        final PackOutput output = event.getGenerator().getPackOutput();

        final var lookup = add(event, new DatapackBuiltinEntriesProvider(
            event.getGenerator().getPackOutput(), event.getLookupProvider(),
            new RegistrySetBuilder()
                .add(Registries.DENSITY_FUNCTION, BuiltinDensityFunctions::load)
            ,
            Set.of(MOD_ID, "minecraft")))
            .getRegistryProvider();
        final var blockTags = add(event, new BuiltinBlockTags(event, lookup)).contentsGetter();
        final var fluidHeat = add(event, new BuiltinFluidHeat(output, lookup)).output();
        final var drinkables = add(event, new BuiltinDrinkables(output, lookup)).output();

        add(event, new BuiltinRecipes(output, lookup));

        add(event, new BuiltinItemTags(event, lookup, blockTags));
        add(event, new BuiltinFluidTags(event, lookup, drinkables));

        add(event, new BuiltinItemHeat(output, lookup, fluidHeat));
        add(event, new BuiltinSupports(output, lookup));
        add(event, new BuiltinKnappingTypes(output, lookup));
        add(event, new BuiltinLampFuels(output, lookup));
        add(event, new BuiltinItemSizes(output, lookup));
        add(event, new BuiltinFuels(output, lookup));
    }

    private static <T extends DataProvider> T add(GatherDataEvent event, T provider)
    {
        return event.getGenerator().addProvider(true, provider);
    }
}
