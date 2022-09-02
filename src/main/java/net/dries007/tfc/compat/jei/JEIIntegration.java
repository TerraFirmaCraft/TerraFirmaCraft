/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.KnappingScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.compat.jei.category.*;
import net.dries007.tfc.compat.jei.extension.AdvancedShapelessExtension;
import net.dries007.tfc.util.Helpers;

@JeiPlugin
public final class JEIIntegration implements IModPlugin
{
    public static final IIngredientTypeWithSubtypes<Item, ItemStack> ITEM_STACK = VanillaTypes.ITEM_STACK;
    public static final IIngredientType<FluidStack> FLUID_STACK = ForgeTypes.FLUID_STACK;

    public static final RecipeType<HeatingRecipe> HEATING = type("heating", HeatingRecipe.class);
    public static final RecipeType<ScrapingRecipe> SCRAPING = type("scraping", ScrapingRecipe.class);
    public static final RecipeType<QuernRecipe> QUERN = type("quern", QuernRecipe.class);
    public static final RecipeType<KnappingRecipe> CLAY_KNAPPING = type("clay_knapping", KnappingRecipe.class);
    public static final RecipeType<KnappingRecipe> FIRE_CLAY_KNAPPING = type("fire_clay_knapping", KnappingRecipe.class);
    public static final RecipeType<KnappingRecipe> LEATHER_KNAPPING = type("leather_knapping", KnappingRecipe.class);
    public static final RecipeType<RockKnappingRecipe> ROCK_KNAPPING = type("rock_knapping", RockKnappingRecipe.class);
    public static final RecipeType<PotRecipe> SOUP_POT = type("soup_pot", PotRecipe.class);
    public static final RecipeType<PotRecipe> SIMPLE_POT = type("simple_pot", PotRecipe.class);
    public static final RecipeType<CastingRecipe> CASTING = type("casting", CastingRecipe.class);
    public static final RecipeType<LoomRecipe> LOOM = type("loom", LoomRecipe.class);
    public static final RecipeType<AlloyRecipe> ALLOYING = type("alloying", AlloyRecipe.class);
    public static final RecipeType<SealedBarrelRecipe> SEALED_BARREL = type("sealed_barrel", SealedBarrelRecipe.class);
    public static final RecipeType<InstantBarrelRecipe> INSTANT_BARREL = type("instant_barrel", InstantBarrelRecipe.class);
    public static final RecipeType<InstantFluidBarrelRecipe> INSTANT_FLUID_BARREL = type("instant_fluid_barrel", InstantFluidBarrelRecipe.class);
    public static final RecipeType<BloomeryRecipe> BLOOMERY = type("bloomery", BloomeryRecipe.class);
    public static final RecipeType<WeldingRecipe> WELDING = type("welding", WeldingRecipe.class);
    public static final RecipeType<AnvilRecipe> ANVIL = type("anvil", AnvilRecipe.class);
    public static final RecipeType<ChiselRecipe> CHISEL = type("chisel", ChiselRecipe.class);

    private static <T> RecipeType<T> type(String name, Class<T> tClass)
    {
        return RecipeType.create(TerraFirmaCraft.MOD_ID, name, tClass);
    }

    private static <C extends Container, T extends Recipe<C>> List<T> recipes(net.minecraft.world.item.crafting.RecipeType<T> type)
    {
        return ClientHelpers.getLevelOrThrow().getRecipeManager().getAllRecipesFor(type);
    }

    private static <C extends Container, T extends Recipe<C>> List<T> recipes(net.minecraft.world.item.crafting.RecipeType<T> type, Predicate<T> filter)
    {
        return recipes(type).stream().filter(filter).collect(Collectors.toList());
    }

    private static void addRecipeCatalyst(IRecipeCatalystRegistration registry, TagKey<Item> tag, RecipeType<?> recipeType)
    {
        Helpers.getAllTagValues(tag, ForgeRegistries.ITEMS).forEach(item -> registry.addRecipeCatalyst(new ItemStack(item), recipeType));
    }

    private static void addRecipeCatalyst(IRecipeCatalystRegistration registry, Wood.BlockType wood, RecipeType<?> recipeType)
    {
        TFCBlocks.WOODS.values().stream().map(map -> map.get(wood)).forEach(i -> registry.addRecipeCatalyst(new ItemStack(i.get()), recipeType));
    }

    @Override
    public ResourceLocation getPluginUid()
    {
        return Helpers.identifier("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        final IGuiHelper gui = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(
            new HeatingCategory(HEATING, gui),
            new QuernRecipeCategory(QUERN, gui),
            new ScrapingRecipeCategory(SCRAPING, gui),
            new KnappingRecipeCategory<>(CLAY_KNAPPING, gui, new ItemStack(Items.CLAY_BALL), KnappingScreen.CLAY, KnappingScreen.CLAY_DISABLED),
            new KnappingRecipeCategory<>(FIRE_CLAY_KNAPPING, gui, new ItemStack(TFCItems.FIRE_CLAY.get()), KnappingScreen.FIRE_CLAY, KnappingScreen.FIRE_CLAY_DISABLED),
            new KnappingRecipeCategory<>(LEATHER_KNAPPING, gui, new ItemStack(Items.LEATHER), KnappingScreen.LEATHER, null),
            new RockKnappingRecipeCategory(ROCK_KNAPPING, gui),
            new SoupPotRecipeCategory(SOUP_POT, gui),
            new SimplePotRecipeCategory(SIMPLE_POT, gui),
            new CastingRecipeCategory(CASTING, gui),
            new LoomRecipeCategory(LOOM, gui),
            new AlloyRecipeCategory(ALLOYING, gui),
            new SealedBarrelRecipeCategory(SEALED_BARREL, gui),
            new InstantBarrelRecipeCategory(INSTANT_BARREL, gui),
            new InstantFluidBarrelRecipeCategory(INSTANT_FLUID_BARREL, gui),
            new BloomeryRecipeCategory(BLOOMERY, gui),
            new WeldingRecipeCategory(WELDING, gui),
            new AnvilRecipeCategory(ANVIL, gui),
            new ChiselRecipeCategory(CHISEL, gui)
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registry)
    {
        registry.addRecipes(HEATING, recipes(TFCRecipeTypes.HEATING.get()));
        registry.addRecipes(SCRAPING, recipes(TFCRecipeTypes.SCRAPING.get()));
        registry.addRecipes(QUERN, recipes(TFCRecipeTypes.QUERN.get()));
        registry.addRecipes(CLAY_KNAPPING, recipes(TFCRecipeTypes.CLAY_KNAPPING.get()));
        registry.addRecipes(FIRE_CLAY_KNAPPING, recipes(TFCRecipeTypes.FIRE_CLAY_KNAPPING.get()));
        registry.addRecipes(LEATHER_KNAPPING, recipes(TFCRecipeTypes.LEATHER_KNAPPING.get()));
        registry.addRecipes(ROCK_KNAPPING, recipes(TFCRecipeTypes.ROCK_KNAPPING.get()));
        registry.addRecipes(SOUP_POT, recipes(TFCRecipeTypes.POT.get(), recipe -> recipe.getSerializer() == TFCRecipeSerializers.POT_SOUP.get()));
        registry.addRecipes(SIMPLE_POT, recipes(TFCRecipeTypes.POT.get(), recipe -> recipe.getSerializer() == TFCRecipeSerializers.POT_SIMPLE.get()));
        registry.addRecipes(CASTING, recipes(TFCRecipeTypes.CASTING.get()));
        registry.addRecipes(LOOM, recipes(TFCRecipeTypes.LOOM.get()));
        registry.addRecipes(ALLOYING, recipes(TFCRecipeTypes.ALLOY.get()));
        registry.addRecipes(SEALED_BARREL, recipes(TFCRecipeTypes.BARREL_SEALED.get()));
        registry.addRecipes(INSTANT_BARREL, recipes(TFCRecipeTypes.BARREL_INSTANT.get()));
        registry.addRecipes(INSTANT_FLUID_BARREL, recipes(TFCRecipeTypes.BARREL_INSTANT_FLUID.get()));
        registry.addRecipes(BLOOMERY, recipes(TFCRecipeTypes.BLOOMERY.get()));
        registry.addRecipes(WELDING, recipes(TFCRecipeTypes.WELDING.get()));
        registry.addRecipes(ANVIL, recipes(TFCRecipeTypes.ANVIL.get()));
        registry.addRecipes(CHISEL, recipes(TFCRecipeTypes.CHISEL.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registry)
    {
        registry.addRecipeCatalyst(new ItemStack(TFCBlocks.FIREPIT.get()), HEATING);
        registry.addRecipeCatalyst(new ItemStack(TFCBlocks.QUERN.get()), QUERN);
        registry.addRecipeCatalyst(new ItemStack(TFCItems.POT.get()), SIMPLE_POT);
        registry.addRecipeCatalyst(new ItemStack(TFCItems.POT.get()), SOUP_POT);
        registry.addRecipeCatalyst(new ItemStack(TFCBlocks.CRUCIBLE.get()), ALLOYING);
        registry.addRecipeCatalyst(new ItemStack(TFCItems.VESSEL.get()), ALLOYING);
        registry.addRecipeCatalyst(new ItemStack(TFCBlocks.BLOOMERY.get()), BLOOMERY);

        for (RegistryObject<Item> reg : TFCItems.GLAZED_VESSELS.values())
        {
            registry.addRecipeCatalyst(new ItemStack(reg.get()), ALLOYING);
        }

        addRecipeCatalyst(registry, TFCTags.Items.KNIVES, SCRAPING);
        addRecipeCatalyst(registry, TFCTags.Items.HANDSTONE, QUERN);
        addRecipeCatalyst(registry, TFCTags.Items.CLAY_KNAPPING, CLAY_KNAPPING);
        addRecipeCatalyst(registry, TFCTags.Items.FIRE_CLAY_KNAPPING, FIRE_CLAY_KNAPPING);
        addRecipeCatalyst(registry, TFCTags.Items.LEATHER_KNAPPING, LEATHER_KNAPPING);
        addRecipeCatalyst(registry, TFCTags.Items.ROCK_KNAPPING, ROCK_KNAPPING);
        addRecipeCatalyst(registry, TFCTags.Items.ANVILS, WELDING);
        addRecipeCatalyst(registry, TFCTags.Items.ANVILS, ANVIL);

        addRecipeCatalyst(registry, Wood.BlockType.LOOM, LOOM);
        addRecipeCatalyst(registry, Wood.BlockType.BARREL, SEALED_BARREL);
        addRecipeCatalyst(registry, Wood.BlockType.BARREL, INSTANT_BARREL);
        addRecipeCatalyst(registry, Wood.BlockType.BARREL, INSTANT_FLUID_BARREL);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registry)
    {
        registry.getCraftingCategory().addCategoryExtension(AdvancedShapelessRecipe.class, AdvancedShapelessExtension::new);
    }
}
