/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import net.dries007.tfc.common.capabilities.heat.HeatManager;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.types.FuelManager;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.mixin.item.crafting.RecipeManagerAccessor;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;

/**
 * This is a manager for various cache invalidations, either on resource reload or server start/stop
 */
public enum CacheInvalidationListener implements IFutureReloadListener
{
    INSTANCE;

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
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

            HeatManager.reload();
            MetalItemManager.reload();
            FuelManager.reload();

            InteractionManager.reload();
        }

        ChunkDataCache.clearAll();
    }

    @SuppressWarnings("unchecked")
    private <C extends IInventory, R extends IRecipe<C>> Collection<R> getRecipes(MinecraftServer server, IRecipeType<R> recipeType)
    {
        return (Collection<R>) ((RecipeManagerAccessor) server.getRecipeManager()).call$byType(recipeType).values();
    }
}