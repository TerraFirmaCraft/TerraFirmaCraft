/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
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

    private static void addCatalystTag(IRecipeCatalystRegistration r, TagKey<Item> tag, RecipeType<?> recipeType)
    {
        Helpers.getAllTagValues(tag, ForgeRegistries.ITEMS).forEach(item -> r.addRecipeCatalyst(new ItemStack(item), recipeType));
    }

    private static List<ItemStack> tagToItemList(TagKey<Item> tag)
    {
        return Helpers.getAllTagValues(tag, ForgeRegistries.ITEMS).stream().map(Item::getDefaultInstance).collect(Collectors.toList());
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
    public static final RecipeType<SealedBarrelRecipe> SEALED_BARREL = type("sealed_barrel", SealedBarrelRecipe.class);
    public static final RecipeType<InstantBarrelRecipe> INSTANT_BARREL = type("instant_barrel", InstantBarrelRecipe.class);
    public static final RecipeType<BloomeryRecipe> BLOOMERY = type("bloomery", BloomeryRecipe.class);
    public static final RecipeType<WeldingRecipe> WELDING = type("welding", WeldingRecipe.class);
    public static final RecipeType<AnvilRecipe> ANVIL = type("anvil", AnvilRecipe.class);
    public static final RecipeType<ChiselRecipe> CHISEL = type("chisel", ChiselRecipe.class);


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
        r.addRecipeCategories(new SealedBarrelRecipeCategory(SEALED_BARREL, gui));
        r.addRecipeCategories(new InstantBarrelRecipeCategory(INSTANT_BARREL, gui));
        r.addRecipeCategories(new BloomeryRecipeCategory(BLOOMERY, gui));
        r.addRecipeCategories(new WeldingRecipeCategory(WELDING, gui));
        r.addRecipeCategories(new AnvilRecipeCategory(ANVIL, gui));
        r.addRecipeCategories(new ChiselRecipeCategory(CHISEL, gui));
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
        r.addRecipes(SEALED_BARREL, getRecipes(TFCRecipeTypes.BARREL_SEALED.get()));
        r.addRecipes(INSTANT_BARREL, getRecipes(TFCRecipeTypes.BARREL_INSTANT.get()));
        r.addRecipes(BLOOMERY, getRecipes(TFCRecipeTypes.BLOOMERY.get()));
        r.addRecipes(WELDING, getRecipes(TFCRecipeTypes.WELDING.get()));
        r.addRecipes(ANVIL, getRecipes(TFCRecipeTypes.ANVIL.get()));
        r.addRecipes(CHISEL, getRecipes(TFCRecipeTypes.CHISEL.get()));

        addIngredientInfo(r);
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
        woodCatalyst(r, Wood.BlockType.LOOM, LOOM);
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.CRUCIBLE.get()), ALLOYING);
        r.addRecipeCatalyst(new ItemStack(TFCItems.VESSEL.get()), ALLOYING);
        mapCatalyst(r, TFCItems.GLAZED_VESSELS, ALLOYING);
        woodCatalyst(r, Wood.BlockType.BARREL, SEALED_BARREL);
        woodCatalyst(r, Wood.BlockType.BARREL, INSTANT_BARREL);
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.BLOOMERY.get()), BLOOMERY);
        addCatalystTag(r, TFCTags.Items.ANVILS, WELDING);
        addCatalystTag(r, TFCTags.Items.ANVILS, ANVIL);
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration r)
    {
        // todo: add bait, salting recipes
    }

    private void addIngredientInfo(IRecipeRegistration r)
    {
        //todo: 1.12 parity
        r.addIngredientInfo(tagToItemList(TFCTags.Items.COMPOST_GREENS), VanillaTypes.ITEM, new TranslatableComponent("tfc.jei.compost_greens"));
        r.addIngredientInfo(tagToItemList(TFCTags.Items.COMPOST_BROWNS), VanillaTypes.ITEM, new TranslatableComponent("tfc.jei.compost_browns"));
        r.addIngredientInfo(tagToItemList(TFCTags.Items.COMPOST_POISONS), VanillaTypes.ITEM, new TranslatableComponent("tfc.jei.compost_poisons"));
        r.addIngredientInfo(new ItemStack(TFCItems.COMPOST.get()), VanillaTypes.ITEM, new TranslatableComponent("tfc.jei.compost"));
        r.addIngredientInfo(new ItemStack(TFCItems.ROTTEN_COMPOST.get()), VanillaTypes.ITEM, new TranslatableComponent("tfc.jei.rotten_compost"));

    }

    public static void woodCatalyst(IRecipeCatalystRegistration r, Wood.BlockType wood, RecipeType<?> recipeType)
    {
        TFCBlocks.WOODS.values().stream().map(map -> map.get(wood)).forEach(i -> r.addRecipeCatalyst(new ItemStack(i.get()), recipeType));
    }
    public static <T> void mapCatalyst(IRecipeCatalystRegistration r, Map<T, RegistryObject<Item>> map, RecipeType<?> recipeType)
    {
        map.values().forEach(i -> r.addRecipeCatalyst(new ItemStack(i.get()), recipeType));
    }
}
