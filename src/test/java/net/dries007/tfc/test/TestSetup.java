package net.dries007.tfc.test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.VanillaBlockTagsProvider;
import net.minecraft.data.tags.VanillaItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.internal.NeoForgeBlockTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeFluidTagsProvider;
import net.neoforged.neoforge.common.data.internal.NeoForgeItemTagsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;

import net.dries007.tfc.data.providers.BuiltinBlockTags;
import net.dries007.tfc.data.providers.BuiltinDrinkables;
import net.dries007.tfc.data.providers.BuiltinFluidHeat;
import net.dries007.tfc.data.providers.BuiltinFluidTags;
import net.dries007.tfc.data.providers.BuiltinFoods;
import net.dries007.tfc.data.providers.BuiltinItemHeat;
import net.dries007.tfc.data.providers.BuiltinItemTags;
import net.dries007.tfc.data.providers.BuiltinKnappingTypes;
import net.dries007.tfc.data.providers.BuiltinRecipes;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.FluidHeat;

/**
 * Bootstraps a number of useful pieces of data for unit tests. This is done using a hybrid of TFC and vanilla data generation,
 * plus a number of terrible hacks to get this to work... but... it does work. And it allows fast testing of complex mechanics
 * (molds, item/fluid heat components, heating, etc.)
 */
public interface TestSetup
{
    AtomicBoolean LOADED = new AtomicBoolean(false);
    Object LOCK = new Object();

    @BeforeAll
    @SuppressWarnings({"deprecation", "UnstableApiUsage", "DataFlowIssue"})
    static void beforeAll()
    {
        synchronized (LOCK)
        {
            if (LOADED.get()) return;

            final RegistryAccess.Frozen lookup = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            final CompletableFuture<HolderLookup.Provider> provider = CompletableFuture.completedFuture(lookup);
            final Path path = Path.of(".");
            final PackOutput output = new PackOutput(path);
            final GatherDataEvent event = new GatherDataEvent(null, new DataGenerator(path, null, true), new GatherDataEvent.DataGeneratorConfig(Set.of(), path, Set.of(), provider, true, true, true, true, true, true), null);
            final CompletableFuture<?> now = CompletableFuture.completedFuture(null);

            final TagMap itemTagMap = new TagMap();
            final TagMap blockTagMap = new TagMap();
            final TagMap fluidTagMap = new TagMap();

            new BuiltinDrinkables(output, provider).run(lookup); // Must run before fluid tags

            final CompletableFuture<TagsProvider.TagLookup<Block>> blockTagLookup = CompletableFuture.completedFuture(key -> Optional.ofNullable(blockTagMap.get(key.location())));

            add(blockTagMap, new VanillaBlockTagsProvider(output, provider));
            add(itemTagMap, new VanillaItemTagsProvider(output, provider, blockTagLookup));
            add(fluidTagMap, new FluidTagsProvider(output, provider));

            add(blockTagMap, new NeoForgeBlockTagsProvider(output, provider, null));
            add(itemTagMap, new NeoForgeItemTagsProvider(output, provider, blockTagLookup, null));
            add(fluidTagMap, new NeoForgeFluidTagsProvider(output, provider, null));

            add(blockTagMap, new BuiltinBlockTags(event, provider));
            add(itemTagMap, new BuiltinItemTags(event, provider, blockTagLookup));
            add(fluidTagMap, new BuiltinFluidTags(event, provider, now));

            resolve(blockTagMap, BuiltInRegistries.BLOCK);
            resolve(itemTagMap, BuiltInRegistries.ITEM);
            resolve(fluidTagMap, BuiltInRegistries.FLUID);

            new BuiltinFoods(output, provider).run(lookup);
            new BuiltinFluidHeat(output, provider).run(lookup);
            final var itemHeat = new BuiltinItemHeat(output, provider, now);
            itemHeat.run(lookup);
            new BuiltinKnappingTypes(output, provider).run(lookup); // Must run before recipes

            final RecipeManager recipeManager = new RecipeManager(lookup);
            final List<RecipeHolder<?>> holders = new ArrayList<>();
            new BuiltinRecipes(output, provider, now, itemHeat).buildRecipes(new RecipeOutput() {
                @Override
                public Advancement.Builder advancement()
                {
                    return Advancement.Builder.recipeAdvancement();
                }

                @Override
                public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions)
                {
                    holders.add(new RecipeHolder<>(id, recipe));
                }
            });
            recipeManager.replaceRecipes(holders);

            Helpers.setCachedRecipeManager(recipeManager);
            IndirectHashCollection.reloadAllCaches(recipeManager);
            FluidHeat.updateCache();

            LOADED.set(true);
        }
    }


    Field TAG_BUILDERS = Helpers.uncheck(() -> {
        final var field = TagsProvider.class.getDeclaredField("builders");
        field.setAccessible(true);
        return field;
    });
    Method TAG_CONTENTS_PROVIDER = Helpers.uncheck(() -> {
        final var method = TagsProvider.class.getDeclaredMethod("createContentsProvider");
        method.setAccessible(true);
        return method;
    });

    private static <T extends TagsProvider<?>> void add(TagMap map, T provider)
    {
        Helpers.uncheck(() -> {
            TAG_BUILDERS.set(provider, map);
            ((CompletableFuture<?>) TAG_CONTENTS_PROVIDER.invoke(provider)).get();
        });
    }

    private static <T> void resolve(TagMap map, Registry<T> registry)
    {
        final TagResolver<T> resolver = new TagResolver<>(map, registry);
        registry.bindTags(map.keySet()
            .stream()
            .collect(Collectors.toMap(
                e -> TagKey.create(registry.key(), e),
                e -> resolver.resolve(e).toList()
            )));
    }

    record TagResolver<T>(Map<ResourceLocation, TagBuilder> builder, Registry<T> registry)
    {
        Stream<Holder<T>> resolve(ResourceLocation id)
        {
            return Objects.requireNonNull(builder.get(id), () -> "No tag for " + id + " in registry " + registry.key().location())
                .build()
                .stream()
                .flatMap(e -> e.isTag()
                    ? e.isRequired() || builder.containsKey(e.getId())
                        ? resolve(e.getId())
                        : Stream.empty()
                    : Stream.of(registry.wrapAsHolder(registry.getOrThrow(ResourceKey.create(registry.key(), e.getId())))));
        }
    }

    class TagMap extends LinkedHashMap<ResourceLocation, TagBuilder>
    {
        @Override
        public void clear() {} // No-op
    }
}
