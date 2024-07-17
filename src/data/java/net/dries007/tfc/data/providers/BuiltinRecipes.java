package net.dries007.tfc.data.providers;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.BloomeryRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.data.recipes.AlloyRecipes;
import net.dries007.tfc.data.recipes.AnvilRecipes;
import net.dries007.tfc.data.recipes.BarrelRecipes;
import net.dries007.tfc.data.recipes.CastingRecipes;
import net.dries007.tfc.data.recipes.ChiselRecipes;
import net.dries007.tfc.data.recipes.WeldingRecipes;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.calendar.ICalendar;

@SuppressWarnings("NotNullFieldNotInitialized")
public final class BuiltinRecipes extends RecipeProvider implements
    AnvilRecipes,
    AlloyRecipes,
    BarrelRecipes,
    CastingRecipes,
    ChiselRecipes,
    WeldingRecipes
{
    RecipeOutput output;
    HolderLookup.Provider lookup;

    public BuiltinRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup);
    }

    @Override
    protected CompletableFuture<?> run(CachedOutput output, HolderLookup.Provider lookup)
    {
        this.lookup = lookup;
        return super.run(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        this.output = output;

        anvilRecipes();
        alloyRecipes();
        barrelRecipes();
        castingRecipes();
        chiselRecipes();
        weldingRecipes();

        add(new BloomeryRecipe(
            SizedFluidIngredient.of(fluidOf(Metal.CAST_IRON), 100),
            SizedIngredient.of(Items.CHARCOAL, 2),
            ItemStackProvider.of(TFCItems.RAW_IRON_BLOOM),
            15 * ICalendar.TICKS_IN_HOUR
        ));

        add("pig_iron", new BlastFurnaceRecipe(
            SizedFluidIngredient.of(fluidOf(Metal.CAST_IRON), 1),
            Ingredient.of(TFCTags.Items.FLUX),
            new FluidStack(fluidOf(Metal.PIG_IRON), 1)
        ));
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
