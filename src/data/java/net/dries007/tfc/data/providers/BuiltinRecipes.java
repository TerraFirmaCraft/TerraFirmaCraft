package net.dries007.tfc.data.providers;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.items.HideItemType;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.LoomRecipe;
import net.dries007.tfc.common.recipes.QuernRecipe;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.ingredients.BlockIngredient;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.data.DataAccessor;
import net.dries007.tfc.data.recipes.AlloyRecipes;
import net.dries007.tfc.data.recipes.AnvilRecipes;
import net.dries007.tfc.data.recipes.BarrelRecipes;
import net.dries007.tfc.data.recipes.CastingRecipes;
import net.dries007.tfc.data.recipes.ChiselRecipes;
import net.dries007.tfc.data.recipes.GlassRecipes;
import net.dries007.tfc.data.recipes.HeatRecipes;
import net.dries007.tfc.data.recipes.QuernRecipes;
import net.dries007.tfc.data.recipes.WeldingRecipes;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.data.FluidHeat;
import net.dries007.tfc.util.registry.RegistryHolder;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class BuiltinRecipes extends RecipeProvider implements
    AnvilRecipes,
    AlloyRecipes,
    BarrelRecipes,
    CastingRecipes,
    ChiselRecipes,
    GlassRecipes,
    HeatRecipes,
    QuernRecipes,
    WeldingRecipes
{
    final DataAccessor<FluidHeat> fluidHeat;
    RecipeOutput output;
    HolderLookup.Provider lookup;

    public BuiltinRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, DataAccessor<FluidHeat> fluidHeat)
    {
        super(output, lookup);
        this.fluidHeat = fluidHeat;
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider lookup)
    {
        this.lookup = lookup;
        return super.run(output, lookup);
    }

    @Override
    @SuppressWarnings("RedundantTypeArguments") // Actually necessary, IDEA is wrong
    protected void buildRecipes(RecipeOutput output)
    {
        this.output = output;

        anvilRecipes();
        alloyRecipes();
        barrelRecipes();
        castingRecipes();
        chiselRecipes();
        glassRecipes();
        heatRecipes();
        quernRecipes();
        weldingRecipes();

        // Bloomery Recipes
        add(new BloomeryRecipe(
            SizedFluidIngredient.of(fluidOf(Metal.CAST_IRON), 100),
            SizedIngredient.of(Items.CHARCOAL, 2),
            ItemStackProvider.of(TFCItems.RAW_IRON_BLOOM),
            15 * ICalendar.TICKS_IN_HOUR
        ));

        // Blast Furnace Recipes
        add("pig_iron", new BlastFurnaceRecipe(
            SizedFluidIngredient.of(fluidOf(Metal.CAST_IRON), 1),
            Ingredient.of(TFCTags.Items.FLUX),
            new FluidStack(fluidOf(Metal.PIG_IRON), 1)
        ));

        // Loom Recipes
        add(new LoomRecipe(SizedIngredient.of(TFCItems.JUTE_FIBER, 12), ItemStackProvider.of(TFCItems.BURLAP_CLOTH), 12, Helpers.identifier("block/burlap")));
        add(new LoomRecipe(SizedIngredient.of(TFCItems.WOOL_YARN, 16), ItemStackProvider.of(TFCItems.WOOL_CLOTH), 16, Helpers.identifierMC("block/white_wool")));
        add(new LoomRecipe(SizedIngredient.of(Tags.Items.STRINGS, 24), ItemStackProvider.of(TFCItems.SILK_CLOTH), 24, Helpers.identifierMC("block/white_wool")));
        add(new LoomRecipe(SizedIngredient.of(TFCItems.WOOL_CLOTH, 4), ItemStackProvider.of(Items.WHITE_WOOL, 8), 4, Helpers.identifierMC("block/white_wool")));
        add(new LoomRecipe(SizedIngredient.of(TFCItems.SOAKED_PAPYRUS_STRIP, 4), ItemStackProvider.of(TFCItems.UNREFINED_PAPER), 8, Helpers.identifier("block/unrefined_paper")));

        // Scraping Recipes
        add(new ScrapingRecipe(
            Ingredient.of(TFCItems.UNREFINED_PAPER), ItemStackProvider.of(Items.PAPER),
            Helpers.identifier("block/unrefined_paper"), Helpers.identifier("block/paper"),
            ItemStackProvider.empty()
        ));

        for (HideItemType.Size size : HideItemType.Size.values())
        {
            final String sizeId = size.name().toLowerCase(Locale.ROOT);
            add(new ScrapingRecipe(
                Ingredient.of(TFCItems.HIDES.get(HideItemType.SHEEPSKIN).get(size)),
                ItemStackProvider.of(TFCItems.HIDES.get(HideItemType.RAW).get(size)),
                Helpers.identifier("item/hide/%s/sheepskin".formatted(sizeId)),
                Helpers.identifier("item/hide/%s/raw".formatted(sizeId)),
                ItemStackProvider.of(TFCItems.WOOL)
            ));
            add(new ScrapingRecipe(
                Ingredient.of(TFCItems.HIDES.get(HideItemType.SOAKED).get(size)),
                ItemStackProvider.of(TFCItems.HIDES.get(HideItemType.SCRAPED).get(size)),
                Helpers.identifier("item/hide/%s/soaked".formatted(sizeId)),
                Helpers.identifier("item/hide/%s/scraped".formatted(sizeId)),
                ItemStackProvider.empty()
            ));
        }

        // Collapse Recipes
        TFCBlocks.ROCK_BLOCKS.forEach((rock, blocks) -> {
            add(new CollapseRecipe(BlockIngredient.of(blocks.get(Rock.BlockType.SPIKE).get()), Optional.empty()));
            add(new CollapseRecipe(BlockIngredient.of(Stream.of(
                blocks.get(Rock.BlockType.RAW).get(),
                blocks.get(Rock.BlockType.HARDENED).get(),
                blocks.get(Rock.BlockType.SMOOTH).get(),
                blocks.get(Rock.BlockType.CRACKED_BRICKS).get(),
                TFCBlocks.GRADED_ORES.get(rock).values()
                    .stream()
                    .map(m -> m.get(Ore.Grade.POOR).get()),
                TFCBlocks.ORES.get(rock).values()
                    .stream()
                    .map(RegistryHolder::get)
            ).<Block>flatMap(Helpers::flatten)), Optional.of(blocks.get(Rock.BlockType.COBBLE).get().defaultBlockState())));
            TFCBlocks.GRADED_ORES.get(rock).forEach((ore, oreBlocks) -> {
                add(new CollapseRecipe(
                    BlockIngredient.of(oreBlocks.get(Ore.Grade.RICH).get()),
                    Optional.of(oreBlocks.get(Ore.Grade.NORMAL).get().defaultBlockState())));
                add(new CollapseRecipe(
                    BlockIngredient.of(oreBlocks.get(Ore.Grade.NORMAL).get()),
                    Optional.of(oreBlocks.get(Ore.Grade.POOR).get().defaultBlockState())));
            });
        });

        // Landslide Recipes
        TFCBlocks.ROCK_BLOCKS.forEach((rock, blocks) ->
            Stream.of(Rock.BlockType.COBBLE, Rock.BlockType.MOSSY_COBBLE, Rock.BlockType.GRAVEL).forEach(type -> add(
                new LandslideRecipe(BlockIngredient.of(blocks.get(type).get()), Optional.empty()))));
        TFCBlocks.SAND.values().forEach(block -> add(
            new LandslideRecipe(BlockIngredient.of(block.get()), Optional.empty())));
        TFCBlocks.ORE_DEPOSITS.values().forEach(map -> map.values().forEach(block -> add(
            new LandslideRecipe(BlockIngredient.of(block.get()), Optional.empty()))));

        for (SoilBlockType.Variant type : SoilBlockType.Variant.values())
        {
            final var blocks = pivot(TFCBlocks.SOIL, type);

            add(new LandslideRecipe(BlockIngredient.of(
                blocks.get(SoilBlockType.CLAY).get(),
                blocks.get(SoilBlockType.CLAY_GRASS).get()
            ), Optional.of(blocks.get(SoilBlockType.CLAY).get().defaultBlockState())));
            add(new LandslideRecipe(BlockIngredient.of(
                blocks.get(SoilBlockType.DIRT).get(),
                blocks.get(SoilBlockType.GRASS).get(),
                blocks.get(SoilBlockType.GRASS_PATH).get(),
                blocks.get(SoilBlockType.FARMLAND).get(),
                blocks.get(SoilBlockType.ROOTED_DIRT).get()
            ), Optional.of(blocks.get(SoilBlockType.DIRT).get().defaultBlockState())));
            add(new LandslideRecipe(BlockIngredient.of(
                blocks.get(SoilBlockType.MUD).get()
            ), Optional.of(blocks.get(SoilBlockType.MUD).get().defaultBlockState())));
        }

        Stream.of(
            TFCBlocks.WHITE_KAOLIN_CLAY,
            TFCBlocks.PINK_KAOLIN_CLAY,
            TFCBlocks.RED_KAOLIN_CLAY
        ).forEach(block -> add(new LandslideRecipe(BlockIngredient.of(block.get()), Optional.empty())));
    }

    @Override
    public DataAccessor<FluidHeat> fluidHeat()
    {
        return fluidHeat;
    }

    @Override
    public HolderLookup.Provider lookup()
    {
        return lookup;
    }

    @Override
    public void add(String prefix, String name, Recipe<?> recipe)
    {
        output.accept(Helpers.identifier((prefix + "/" + name).toLowerCase(Locale.ROOT)), recipe, null);
    }
}
