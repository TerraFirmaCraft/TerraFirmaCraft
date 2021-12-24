/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.jei;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.jei.category.*;
import net.dries007.tfc.util.Helpers;

@JeiPlugin
public class TFCJEIPlugin implements IModPlugin
{
    private static <C extends Container, T extends Recipe<C>> Collection<T> getRecipes(RecipeType<T> type)
    {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        return level.getRecipeManager().getAllRecipesFor(type);
    }

    private static void addCatalystTag(IRecipeCatalystRegistration r, Tag<Item> tag, ResourceLocation uId)
    {
        tag.getValues().forEach(item -> r.addRecipeCatalyst(new ItemStack(item), uId));
    }

    private static final ResourceLocation CLAY_DISABLED_TEXTURE = Helpers.identifier("textures/gui/knapping/clay_ball_disabled.png");
    private static final ResourceLocation FIRE_CLAY_DISABLED_TEXTURE = Helpers.identifier("textures/gui/knapping/fire_clay_disabled.png");
    private static final ResourceLocation CLAY_TEXTURE = Helpers.identifier("textures/gui/knapping/clay_ball.png");
    private static final ResourceLocation FIRE_CLAY_TEXTURE = Helpers.identifier("textures/gui/knapping/fire_clay.png");
    private static final ResourceLocation LEATHER_TEXTURE = Helpers.identifier("textures/gui/knapping/leather.png");

    private static final ResourceLocation HEATING = Helpers.identifier("heating");
    private static final ResourceLocation SCRAPING = Helpers.identifier("scraping");
    private static final ResourceLocation QUERN = Helpers.identifier("quern");
    private static final ResourceLocation CLAY_KNAPPING = Helpers.identifier("clay_knapping");
    private static final ResourceLocation FIRE_CLAY_KNAPPING = Helpers.identifier("fire_clay_knapping");
    private static final ResourceLocation LEATHER_KNAPPING = Helpers.identifier("leather_knapping");
    private static final ResourceLocation ROCK_KNAPPING = Helpers.identifier("rock_knapping");

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
        r.addRecipeCategories(new KnappingRecipeCategory<>(CLAY_KNAPPING, gui, new ItemStack(Items.CLAY_BALL), KnappingRecipe.class, CLAY_TEXTURE, CLAY_DISABLED_TEXTURE));
        r.addRecipeCategories(new KnappingRecipeCategory<>(FIRE_CLAY_KNAPPING, gui, new ItemStack(TFCItems.FIRE_CLAY.get()), KnappingRecipe.class, FIRE_CLAY_TEXTURE, FIRE_CLAY_DISABLED_TEXTURE));
        r.addRecipeCategories(new KnappingRecipeCategory<>(LEATHER_KNAPPING, gui, new ItemStack(Items.LEATHER), KnappingRecipe.class, LEATHER_TEXTURE, null));
        r.addRecipeCategories(new RockKnappingRecipeCategory(ROCK_KNAPPING, gui));
    }

    @Override
    public void registerRecipes(IRecipeRegistration r)
    {
        r.addRecipes(getRecipes(TFCRecipeTypes.HEATING), HEATING);
        r.addRecipes(getRecipes(TFCRecipeTypes.SCRAPING), SCRAPING);
        r.addRecipes(getRecipes(TFCRecipeTypes.QUERN), QUERN);
        r.addRecipes(getRecipes(TFCRecipeTypes.CLAY_KNAPPING), CLAY_KNAPPING);
        r.addRecipes(getRecipes(TFCRecipeTypes.FIRE_CLAY_KNAPPING), FIRE_CLAY_KNAPPING);
        r.addRecipes(getRecipes(TFCRecipeTypes.LEATHER_KNAPPING), LEATHER_KNAPPING);
        r.addRecipes(getRecipes(TFCRecipeTypes.ROCK_KNAPPING), ROCK_KNAPPING);

        //todo: ingredient info goes here
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r)
    {
        r.addRecipeCatalyst(new ItemStack(TFCBlocks.FIREPIT.get()), HEATING);
        addCatalystTag(r, TFCTags.Items.KNIVES, SCRAPING);
        addCatalystTag(r, TFCTags.Items.HANDSTONE, QUERN);
        addCatalystTag(r, TFCTags.Items.CLAY_KNAPPING, CLAY_KNAPPING);
        addCatalystTag(r, TFCTags.Items.FIRE_CLAY_KNAPPING, FIRE_CLAY_KNAPPING);
        addCatalystTag(r, TFCTags.Items.LEATHER_KNAPPING, LEATHER_KNAPPING);
        addCatalystTag(r, TFCTags.Items.ROCK_KNAPPING, ROCK_KNAPPING);
    }

}
