/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.MinecraftServer;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.command.LocateVeinCommand;
import net.dries007.tfc.common.recipes.*;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;

import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

/**
 * This is a manager for various cache invalidations, either on resource reload or server start/stop
 */
public enum CacheInvalidationListener implements PreparableReloadListener
{
    INSTANCE;

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return CompletableFuture.runAsync(() -> {}, backgroundExecutor).thenCompose(stage::wait).thenRunAsync(this::invalidateAll, gameExecutor);
    }

    public void invalidateAll()
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
        {
            CollapseRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.COLLAPSE));
            LandslideRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.LANDSLIDE));
            HeatingRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.HEATING));
            QuernRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.QUERN));
            ScrapingRecipe.CACHE.reload(getRecipes(server, TFCRecipeTypes.SCRAPING));

            HeatCapability.CACHE.reload(HeatCapability.MANAGER.getValues());
            MetalItemManager.CACHE.reload(MetalItemManager.MANAGER.getValues());
            FuelManager.CACHE.reload(FuelManager.MANAGER.getValues());
            FoodCapability.CACHE.reload(FoodCapability.MANAGER.getValues());
            ItemSizeManager.CACHE.reload(ItemSizeManager.MANAGER.getValues());

            ItemSizeManager.resetItemSizes();
            InteractionManager.reload();
        }

        ChunkDataCache.clearAll();
        LocateVeinCommand.clearCache();
    }

    @SuppressWarnings("unchecked")
    private <C extends Container, R extends Recipe<C>> Collection<R> getRecipes(MinecraftServer server, RecipeType<R> recipeType)
    {
        // todo: mixin / accessor
        return (Collection<R>) server.getRecipeManager().byType(recipeType).values();
        // (Collection<R>) ((RecipeManagerAccessor) server.getRecipeManager()).call$byType(recipeType).values();
    }
}