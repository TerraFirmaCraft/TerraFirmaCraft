/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.emi.recipe.EmiAlloyingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAnvilRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiBlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiBloomeryRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiCastingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiChiselRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiGlassworkingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiWeldingRecipe;
import net.dries007.tfc.compat.emi.recipe.GenericRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

/**
 * todo: it is worth having a native EMI plugin, as otherwise it will populate from JEI, which keeps both JEI and EMI
 * runtime loaded. Ultimately this is poor, and I would like to provide first-class EMI compat
 */
@EmiEntrypoint
public final class EmiIntegration implements EmiPlugin
{
    private static final List<EmiRecipeCategory> CATEGORIES = new ArrayList<>();
    public static final EmiRecipeCategory ALLOYING = createCategory("alloying", TFCBlocks.CRUCIBLE);
    public static final EmiRecipeCategory BLOOMERY = createCategory("bloomery", TFCBlocks.BLOOMERY);
    public static final EmiRecipeCategory BLAST_FURNACE = createCategory("blast_furnace", TFCBlocks.BLAST_FURNACE);
    public static final EmiRecipeCategory CASTING = createCategory("casting", TFCItems.MOLDS.get(Metal.ItemType.INGOT));
    public static final EmiRecipeCategory CHISEL = createCategory("chisel", TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.CHISEL));
    public static final EmiRecipeCategory ANVIL = createCategory("anvil", TFCBlocks.METALS.get(Metal.BRONZE).get(Metal.BlockType.ANVIL));
    public static final EmiRecipeCategory HEATING = createCategory("heating", TFCBlocks.FIREPIT);
    public static final EmiRecipeCategory BARREL = createCategory("barrel", TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.BARREL));
    public static final EmiRecipeCategory WELDING = createCategory("welding", TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER));
    public static final EmiRecipeCategory GLASSWORKING = createCategory("glassworking", TFCItems.BLOWPIPE_WITH_GLASS);

    private static EmiRecipeCategory createCategory(String name, ItemLike item)
    {
        EmiRecipeCategory category = new EmiRecipeCategory(Helpers.identifier(name), EmiStack.of(item));
        CATEGORIES.add(category);
        return category;
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> recipes(Supplier<RecipeType<T>> type)
    {
        return ClientHelpers.getLevelOrThrow().getRecipeManager()
            .getAllRecipesFor(type.get())
            .stream()
            .toList();
    }

    @Override
    public void register(EmiRegistry registry)
    {
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);
        CATEGORIES.clear();
    }

    private void registerCategories(EmiRegistry registry)
    {
        for (EmiRecipeCategory category : CATEGORIES)
        {
            registry.addCategory(category);
        }
        ALLOYING.sorter = sortByInput();
        GLASSWORKING.sorter = sortByInput();
        ANVIL.sorter = basicSorter();
        WELDING.sorter = basicSorter();
        CASTING.sorter = basicSorter();
        HEATING.sorter = basicSorter();
    }

    private void registerWorkstations(EmiRegistry registry)
    {
        registry.addWorkstation(HEATING, EmiStack.of(TFCBlocks.FIREPIT));
        registry.addWorkstation(BARREL, EmiIngredient.of(TFCTags.Items.BARRELS));
        registry.addWorkstation(ALLOYING, EmiStack.of(TFCBlocks.CRUCIBLE));
        registry.addWorkstation(ALLOYING, EmiIngredient.of(TFCTags.Items.FIRED_VESSELS));
        registry.addWorkstation(ANVIL, EmiIngredient.of(TFCTags.Blocks.ANVILS));
        registry.addWorkstation(WELDING, EmiIngredient.of(TFCTags.Blocks.ANVILS));
        registry.addWorkstation(BLAST_FURNACE, EmiStack.of(TFCBlocks.BLAST_FURNACE));
        registry.addWorkstation(CASTING, EmiIngredient.of(TFCTags.Items.FIRED_MOLDS));
        registry.addWorkstation(BLOOMERY, EmiStack.of(TFCBlocks.BLOOMERY));
        registry.addWorkstation(CHISEL, EmiIngredient.of(TFCTags.Items.TOOLS_CHISEL));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.BLOWPIPE_WITH_GLASS));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.GEM_SAW));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.JACKS));
    }

    private void registerRecipes(EmiRegistry registry)
    {
        basicRecipeMapping(registry, TFCRecipeTypes.HEATING, EmiHeatingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.ALLOY, EmiAlloyingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.ANVIL, EmiAnvilRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.WELDING, EmiWeldingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BLAST_FURNACE, EmiBlastFurnaceRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.CASTING, EmiCastingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BLOOMERY, EmiBloomeryRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.CHISEL, EmiChiselRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.GLASSWORKING, EmiGlassworkingRecipe::new);
    }

    private static <C extends RecipeInput, T extends Recipe<C>> void basicRecipeMapping(EmiRegistry registry, Supplier<RecipeType<T>> type, BiFunction<ResourceLocation, T, EmiRecipe> mapper)
    {
        for (RecipeHolder<T> recipe : recipes(type))
        {
            registry.addRecipe(mapper.apply(recipe.id(), recipe.value()));
        }
    }

    private static Comparator<EmiRecipe> sortByInput()
    {
        return Comparator.comparingInt(r -> r.getInputs().size());
    }

    private static Comparator<EmiRecipe> basicSorter()
    {
        return (o1, o2) -> {
            if (o1 instanceof GenericRecipe<?> recipe)
            {
                return recipe.compareTo(o2);
            }
            return 0;
        };
    }

}
