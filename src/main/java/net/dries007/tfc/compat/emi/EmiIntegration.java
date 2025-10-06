/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.ItemStack;
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
import net.dries007.tfc.common.recipes.AlloyRecipe;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.BlastFurnaceRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAlloyingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAnvilRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiBlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiWeldingRecipe;
import net.dries007.tfc.compat.jei.category.AlloyRecipeCategory;
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
    public static final EmiRecipeCategory BLAST_FURNACE = createCategory("blast_furnace", TFCBlocks.BLAST_FURNACE);
    public static final EmiRecipeCategory ANVIL = createCategory("anvil", TFCBlocks.METALS.get(Metal.BRONZE).get(Metal.BlockType.ANVIL));
    public static final EmiRecipeCategory HEATING = createCategory("heating", TFCBlocks.FIREPIT);
    public static final EmiRecipeCategory BARREL = createCategory("barrel", TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.BARREL));
    public static final EmiRecipeCategory WELDING = createCategory("welding", TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER));

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
    }

    private void registerRecipes(EmiRegistry registry)
    {
        for (RecipeHolder<HeatingRecipe> recipe : recipes(TFCRecipeTypes.HEATING))
        {
            registry.addRecipe(new EmiHeatingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<AlloyRecipe> recipe : recipes(TFCRecipeTypes.ALLOY))
        {
            registry.addRecipe(new EmiAlloyingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<AnvilRecipe> recipe : recipes(TFCRecipeTypes.ANVIL))
        {
            registry.addRecipe(new EmiAnvilRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<WeldingRecipe> recipe : recipes(TFCRecipeTypes.WELDING))
        {
            registry.addRecipe(new EmiWeldingRecipe(recipe.id(), recipe.value()));
        }
        for (RecipeHolder<BlastFurnaceRecipe> recipe : recipes(TFCRecipeTypes.BLAST_FURNACE))
        {
            registry.addRecipe(new EmiBlastFurnaceRecipe(recipe.id(), recipe.value()));
        }
    }

}
