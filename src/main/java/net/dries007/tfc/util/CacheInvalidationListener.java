/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.server.ServerLifecycleHooks;

import net.dries007.tfc.common.commands.LocateVeinCommand;
import net.dries007.tfc.common.recipes.*;

/**
 * This is a manager for various cache invalidations, either on resource reload or server start/stop
 */
public enum CacheInvalidationListener implements SyncReloadListener
{
    INSTANCE;

    @Override
    public void reloadSync()
    {
        final MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            final RecipeManager manager = server.getRecipeManager();

            CollapseRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.COLLAPSE.get()));
            LandslideRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.LANDSLIDE.get()));
            HeatingRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.HEATING.get()));
            QuernRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.QUERN.get()));
            ScrapingRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.SCRAPING.get()));
            CastingRecipe.CACHE.reload(manager.getAllRecipesFor(TFCRecipeTypes.CASTING.get()));

            InteractionManager.reload();
        }

        LocateVeinCommand.clearCache();
    }
}