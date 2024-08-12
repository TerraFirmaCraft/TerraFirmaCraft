/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.data;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
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
import net.minecraft.tags.WorldPresetTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import net.dries007.tfc.data.providers.BuiltinBlockTags;
import net.dries007.tfc.data.providers.BuiltinClimateRanges;
import net.dries007.tfc.data.providers.BuiltinDamageTypes;
import net.dries007.tfc.data.providers.BuiltinDensityFunctions;
import net.dries007.tfc.data.providers.BuiltinDeposits;
import net.dries007.tfc.data.providers.BuiltinDrinkables;
import net.dries007.tfc.data.providers.BuiltinEntityDamageResist;
import net.dries007.tfc.data.providers.BuiltinEntityTags;
import net.dries007.tfc.data.providers.BuiltinFauna;
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
import net.dries007.tfc.data.providers.BuiltinWorldPreset;
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
                .add(Registries.WORLD_PRESET, BuiltinWorldPreset::load)
                .add(Registries.PAINTING_VARIANT, BuiltinPaintings::new)
                .add(Registries.DAMAGE_TYPE, BuiltinDamageTypes::new)
            , Set.of(MOD_ID, "minecraft"))).getRegistryProvider();
        final var fluidHeat = add(event, new BuiltinFluidHeat(output, lookup)).output();
        final var itemHeat = add(event, new BuiltinItemHeat(output, lookup, fluidHeat));

        final var drinkables = add(event, new BuiltinDrinkables(output, lookup)).output();
        final var knappingTypes = add(event, new BuiltinKnappingTypes(output, lookup)).output();

        add(event, new BuiltinRecipes(output, lookup, CompletableFuture.allOf(fluidHeat, knappingTypes), itemHeat));

        final var blockTags = add(event, new BuiltinBlockTags(event, lookup)).contentsGetter();

        add(event, new BuiltinItemTags(event, lookup, blockTags));
        add(event, new BuiltinFluidTags(event, lookup, drinkables));
        add(event, new BuiltinEntityTags(event, lookup));
        tags(event, Registries.PAINTING_VARIANT, lookup, (provider, tags) -> tags.tag(PaintingVariantTags.PLACEABLE).add(
            BuiltinPaintings.GOLDEN_FIELD,
            BuiltinPaintings.HOT_SPRING,
            BuiltinPaintings.LAKE,
            BuiltinPaintings.SUPPORTS,
            BuiltinPaintings.VOLCANO
        ));
        tags(event, Registries.DAMAGE_TYPE, lookup, (provider, tags) -> {
            tags.tag(PhysicalDamageType.IS_CRUSHING).add(
                DamageTypes.IN_WALL,
                DamageTypes.CRAMMING,
                DamageTypes.FALL,
                DamageTypes.FLY_INTO_WALL,
                DamageTypes.FALLING_BLOCK,
                DamageTypes.FALLING_ANVIL
            );
            tags.tag(PhysicalDamageType.IS_PIERCING).add(
                DamageTypes.CACTUS,
                DamageTypes.SWEET_BERRY_BUSH,
                DamageTypes.STALAGMITE,
                DamageTypes.FALLING_STALACTITE,
                DamageTypes.STING,
                DamageTypes.ARROW,
                DamageTypes.TRIDENT
            );
            tags.tag(PhysicalDamageType.IS_SLASHING);
        });
        tags(event, Registries.WORLD_PRESET, lookup, (provider, tags) -> tags.tag(WorldPresetTags.NORMAL).add(PRESET));

        add(event, new BuiltinDeposits(output, lookup));
        add(event, new BuiltinEntityDamageResist(output, lookup));
        add(event, new BuiltinFertilizers(output, lookup));
        add(event, new BuiltinFoods(output, lookup));
        add(event, new BuiltinFuels(output, lookup));
        add(event, new BuiltinItemDamageResist(output, lookup));
        add(event, new BuiltinItemSizes(output, lookup));
        add(event, new BuiltinLampFuels(output, lookup));
        add(event, new BuiltinSupports(output, lookup));
        add(event, new BuiltinClimateRanges(output, lookup));
        add(event, new BuiltinFauna(output, lookup));
    }

    private static <T extends DataProvider> T add(GatherDataEvent event, T provider)
    {
        return event.getGenerator().addProvider(true, provider);
    }

    private static <T> void tags(GatherDataEvent event, ResourceKey<Registry<T>> registry, CompletableFuture<HolderLookup.Provider> lookup, BiConsumer<HolderLookup.Provider, TagLookup<T>> callback)
    {
        add(event, new TagsProvider<T>(event.getGenerator().getPackOutput(), registry, lookup, MOD_ID, event.getExistingFileHelper())
        {
            @Override
            protected void addTags(HolderLookup.Provider provider)
            {
                callback.accept(provider, this::tag);
            }
        });
    }

    @FunctionalInterface
    interface TagLookup<T>
    {
        TagsProvider.TagAppender<T> tag(TagKey<T> tag);
    }
}
