/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collection;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.commands.LocateVeinCommand;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.mixin.accessor.RecipeManagerAccessor;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;

/**
 * This is a manager for various cache invalidations, either on resource reload or server start/stop
 */
public enum CacheInvalidationListener implements SyncReloadListener
{
    INSTANCE;

    @Override
    public void reloadSync()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            CollapseRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.COLLAPSE));
            LandslideRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.LANDSLIDE));
            HeatingRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.HEATING));
            QuernRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.QUERN));
            ScrapingRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.SCRAPING));
            CastingRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.CASTING));

            HeatCapability.CACHE.reload(HeatCapability.MANAGER.getValues());
            FoodCapability.CACHE.reload(FoodCapability.MANAGER.getValues());

            ItemSizeManager.CACHE.reload(ItemSizeManager.MANAGER.getValues());

            Fuel.CACHE.reload(Fuel.MANAGER.getValues());
            Drinkable.CACHE.reload(Drinkable.MANAGER.getValues());

            ItemSizeManager.resetItemSizes();
            InteractionManager.reload();
        }

        LocateVeinCommand.clearCache();
    }

    @SuppressWarnings("unchecked")
    private <C extends Container, R extends Recipe<C>> Collection<R> getRecipes(MinecraftServer server, RecipeType<R> recipeType)
    {
        return (Collection<R>) ((RecipeManagerAccessor) server.getRecipeManager()).invoke$byType(recipeType).values();
    }
}