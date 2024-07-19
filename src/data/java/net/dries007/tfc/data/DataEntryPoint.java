/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCDamageTypes;
import net.dries007.tfc.data.providers.BuiltinBlockTags;
import net.dries007.tfc.data.providers.BuiltinDensityFunctions;
import net.dries007.tfc.data.providers.BuiltinDeposits;
import net.dries007.tfc.data.providers.BuiltinDrinkables;
import net.dries007.tfc.data.providers.BuiltinEntityDamageResist;
import net.dries007.tfc.data.providers.BuiltinFertilizers;
import net.dries007.tfc.data.providers.BuiltinFluidHeat;
import net.dries007.tfc.data.providers.BuiltinFluidTags;
import net.dries007.tfc.data.providers.BuiltinFoods;
import net.dries007.tfc.data.providers.BuiltinFuels;
import net.dries007.tfc.data.providers.BuiltinItemDamageResist;
import net.dries007.tfc.data.providers.BuiltinItemHeat;
import net.dries007.tfc.data.providers.BuiltinItemSizes;
import net.dries007.tfc.data.providers.BuiltinItemTags;
import net.dries007.tfc.data.providers.BuiltinKnappingTypes;
import net.dries007.tfc.data.providers.BuiltinLampFuels;
import net.dries007.tfc.data.providers.BuiltinPaintings;
import net.dries007.tfc.data.providers.BuiltinRecipes;
import net.dries007.tfc.data.providers.BuiltinSupports;
import net.dries007.tfc.util.PhysicalDamageType;

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
                .add(Registries.PAINTING_VARIANT, BuiltinPaintings::new)
            ,
            Set.of(MOD_ID, "minecraft")))
            .getRegistryProvider();
        final var blockTags = add(event, new BuiltinBlockTags(event, lookup)).contentsGetter();
        final var fluidHeat = add(event, new BuiltinFluidHeat(output, lookup)).output();
        final var drinkables = add(event, new BuiltinDrinkables(output, lookup)).output();

        add(event, new BuiltinRecipes(output, lookup, fluidHeat));

        add(event, new BuiltinItemTags(event, lookup, blockTags));
        add(event, new BuiltinFluidTags(event, lookup, drinkables));
        tags(event, Registries.PAINTING_VARIANT, lookup, (provider, add) -> provider
            .lookupOrThrow(Registries.PAINTING_VARIANT)
            .listElementIds()
            .filter(e -> e.location().getNamespace().equals(MOD_ID))
            .forEach(add.apply(PaintingVariantTags.PLACEABLE)));
        tags(event, Registries.DAMAGE_TYPE, lookup, (provider, add) -> {
            List.of(
                DamageTypes.IN_WALL,
                DamageTypes.CRAMMING,
                DamageTypes.FALL,
                DamageTypes.FLY_INTO_WALL,
                DamageTypes.FALLING_BLOCK,
                DamageTypes.FALLING_ANVIL
            ).forEach(add.apply(PhysicalDamageType.IS_CRUSHING));
            List.of(
                DamageTypes.CACTUS,
                DamageTypes.SWEET_BERRY_BUSH,
                DamageTypes.STALAGMITE,
                DamageTypes.FALLING_STALACTITE,
                DamageTypes.STING,
                DamageTypes.ARROW,
                DamageTypes.TRIDENT
            ).forEach(add.apply(PhysicalDamageType.IS_PIERCING));
            add.apply(PhysicalDamageType.IS_SLASHING);
        });

        add(event, new BuiltinDeposits(output, lookup));
        add(event, new BuiltinEntityDamageResist(output, lookup));
        add(event, new BuiltinFertilizers(output, lookup));
        add(event, new BuiltinFoods(output, lookup));
        add(event, new BuiltinFuels(output, lookup));
        add(event, new BuiltinItemDamageResist(output, lookup));
        add(event, new BuiltinItemHeat(output, lookup, fluidHeat));
        add(event, new BuiltinItemSizes(output, lookup));
        add(event, new BuiltinKnappingTypes(output, lookup));
        add(event, new BuiltinLampFuels(output, lookup));
        add(event, new BuiltinSupports(output, lookup));
        // todo: climate range
        // todo: fauna
    }

    private static <T extends DataProvider> T add(GatherDataEvent event, T provider)
    {
        return event.getGenerator().addProvider(true, provider);
    }

    private static <T> void tags(GatherDataEvent event, ResourceKey<Registry<T>> registry, CompletableFuture<HolderLookup.Provider> lookup,
                                 BiConsumer<HolderLookup.Provider, Function<TagKey<T>, Consumer<ResourceKey<T>>>> callback)
    {
        add(event, new TagsProvider<T>(event.getGenerator().getPackOutput(), registry, lookup, MOD_ID, event.getExistingFileHelper())
        {
            @Override
            protected void addTags(HolderLookup.Provider provider)
            {
                callback.accept(provider, tag -> tag(tag)::add);
            }
        });
    }
}
