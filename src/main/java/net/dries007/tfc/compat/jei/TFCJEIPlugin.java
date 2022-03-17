/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.compat.jei.category.*;
import net.dries007.tfc.util.Helpers;

@JeiPlugin
public class TFCJEIPlugin implements IModPlugin
{
    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type)
    {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    private static <C extends Container, T extends Recipe<C>> List<T> getRecipes(net.minecraft.world.item.crafting.RecipeType<T> type, Predicate<T> filter)
    {
        return getRecipes(type).stream().filter(filter).collect(Collectors.toList());
    }

    //todo: use forge registry
    @SuppressWarnings("deprecation")
    private static void addCatalystTag(IRecipeCatalystRegistration r, TagKey<Item> tag, RecipeType<?> recipeType)
    {
        Helpers.getAllTagValues(tag, Registry.ITEM).forEach(item -> r.addRecipeCatalyst(new ItemStack(item), recipeType));
    }

    private static <T> RecipeType<T> type(String name, Class<T> tClass)
    {
        return RecipeType.create(TerraFirmaCraft.MOD_ID, name, tClass);
    }

    private static final ResourceLocation CLAY_DISABLED_TEXTURE = Helpers.identifier("textures/gui/knapping/clay_ball_disabled.png");
    private static final ResourceLocation FIRE_CLAY_DISABLED_TEXTURE = Helpers.identifier("textures/gui/knapping/fire_clay_disabled.png");
    private static final ResourceLocation CLAY_TEXTURE = Helpers.identifier("textures/gui/knapping/clay_ball.png");
    private static final ResourceLocation FIRE_CLAY_TEXTURE = Helpers.identifier("textures/gui/knapping/fire_clay.png");
    private static final ResourceLocation LEATHER_TEXTURE = Helpers.identifier("textures/gui/knapping/leather.png");

    public static final RecipeType<HeatingRecipe> HEATING = type("heating", HeatingRecipe.class);
    public static final RecipeType<ScrapingRecipe> SCRAPING = type("scraping", ScrapingRecipe.class);
    public static final RecipeType<QuernRecipe> QUERN = type("quern", QuernRecipe.class);
    public static final RecipeType<KnappingRecipe> CLAY_KNAPPING = type("clay_knapping", KnappingRecipe.class);
    public static final RecipeType<KnappingRecipe> FIRE_CLAY_KNAPPING = type("fire_clay_knapping", KnappingRecipe.class);
    public static final RecipeType<KnappingRecipe> LEATHER_KNAPPING = type("leather_knapping", KnappingRecipe.class);
    public static final RecipeType<RockKnappingRecipe> ROCK_KNAPPING = type("rock_knapping", RockKnappingRecipe.class);
    public static final RecipeType<PotRecipe> SOUP_POT = type("soup_pot", PotRecipe.class);
    public static final RecipeType<PotRecipe> FLUID_POT = type("fluid_pot", PotRecipe.class);
    public static final RecipeType<CastingRecipe> CASTING = type("casting", CastingRecipe.class);
    public static final RecipeType<LoomRecipe> LOOM = type("loom", LoomRecipe.class);
    public static final RecipeType<AlloyRecipe> ALLOYING = type("alloying", AlloyRecipe.class);


    @Override
    public ResourceLocation getPluginUid()
    {
        return Helpers.identifier("jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration r)
    {
        IGuiHelper gui = r.getJeiHelpers().getGuiHelper();
        r.addRecipeCategories(new HeatingCategory(HEATING, gui));
        r.addRecipeCategories(new QuernRecipeCategory(QUERN, gui));
        r.addRecipeCategories(new ScrapingRecipeCategory(SCRAPING, gui));
        r.addRecipeCategories(new KnappingRecipeCategory<>(CLAY_KNAPPING, gui, new ItemStack(Items.CLAY_BALL), CLAY_TEXTURE, CLAY_DISABLED_TEXTURE));
        r.addRecipeCategories(new KnappingRecipeCategory<>(FIRE_CLAY_KNAPPING, gui, new ItemStack(TFCItems.FIRE_CLAY.get()), FIRE_CLAY_TEXTURE, FIRE_CLAY_DISABLED_TEXTURE));
        r.addRecipeCategories(new KnappingRecipeCategory<>(LEATHER_KNAPPING, gui, new ItemStack(Items.LEATHER), LEATHER_TEXTURE, null));
        r.addRecipeCategories(new RockKnappingRecipeCategory(ROCK_KNAPPING, gui));
        r.addRecipeCategories(new SoupPotRecipeCategory(SOUP_POT, gui));
        r.addRecipeCategories(new FluidPotRecipeCategory(FLUID_POT, gui));
        r.addRecipeCategories(new CastingRecipeCategory(CASTING, gui));
        r.addRecipeCategories(new LoomRecipeCategory(LOOM, gui));
        r.addRecipeCategories(new AlloyRecipeCategory(ALLOYING, gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(HEATING, getRecipes(TFCRecipeTypes.HEATING.get()));
        r.addRecipes(SCRAPING, getRecipes(TFCRecipeTypes.SCRAPING.get()));
        r.addRecipes(QUERN, getRecipes(TFCRecipeTypes.QUERN.get()));
        r.addRecipes(CLAY_KNAPPING, getRecipes(TFCRecipeTypes.CLAY_KNAPPING.get()));
        r.addRecipes(FIRE_CLAY_KNAPPING, getRecipes(TFCRecipeTypes.FIRE_CLAY_KNAPPING.get()));
        r.addRecipes(LEATHER_KNAPPING, getRecipes(TFCRecipeTypes.LEATHER_KNAPPING.get()));
        r.addRecipes(ROCK_KNAPPING, getRecipes(TFCRecipeTypes.ROCK_KNAPPING.get()));
        r.addRecipes(SOUP_POT, getRecipes(TFCRecipeTypes.POT.get(), recipe -> recipe.getSerializer() == TFCRecipeSerializers.POT_SOUP.get()));
        r.addRecipes(FLUID_POT, getRecipes(TFCRecipeTypes.POT.get(), recipe -> recipe.getSerializer() == TFCRecipeSerializers.POT_FLUID.get()));
        r.addRecipes(CASTING, getRecipes(TFCRecipeTypes.CASTING.get()));
        r.addRecipes(LOOM, getRecipes(TFCRecipeTypes.LOOM.get()));
        r.addRecipes(ALLOYING, getRecipes(TFCRecipeTypes.ALLOY.get()));

        //todo: ingredient info goes here
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.FIREPIT.get()), HEATING);
        addCatalystTag(r, TFCTags.Items.KNIVES, SCRAPING);
        addCatalystTag(r, TFCTags.Items.HANDSTONE, QUERN);
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.QUERN.get()), QUERN);
        addCatalystTag(r, TFCTags.Items.CLAY_KNAPPING, CLAY_KNAPPING);
        addCatalystTag(r, TFCTags.Items.FIRE_CLAY_KNAPPING, FIRE_CLAY_KNAPPING);
        addCatalystTag(r, TFCTags.Items.LEATHER_KNAPPING, LEATHER_KNAPPING);
        addCatalystTag(r, TFCTags.Items.ROCK_KNAPPING, ROCK_KNAPPING);
        r.addRecipeCatalyst(new ItemStack(TFCItems.POT.get()), FLUID_POT);
        r.addRecipeCatalyst(new ItemStack(TFCItems.POT.get()), SOUP_POT);
        addCatalystTag(r, TFCTags.Items.LOOMS, LOOM);
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.CRUCIBLE.get()), ALLOYING);
        r.addRecipeCatalyst(new ItemStack(TFCItems.VESSEL.get()), ALLOYING);
        TFCItems.GLAZED_VESSELS.values().forEach(i -> r.addRecipeCatalyst(new ItemStack(i.get()), ALLOYING));
    }

}
