/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;

import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.common.recipes.LandslideRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.common.types.MetalItemManager;
import net.dries007.tfc.world.chunkdata.ChunkDataCache;

/**
 * This is a simple reload listener that just notifies anything in need of being notified when resources reload (cache invalidations, etc.)
 */
public enum TFCServerTracker implements IFutureReloadListener
{
    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();

    private MinecraftServer server;

    public void onServerStart(MinecraftServer server)
    {
        this.server = server;
        this.server.getResourceManager().addReloadListener(this);

        ChunkDataCache.clearAll();
    }

    public void onServerStop()
    {
        this.server = null;

        ChunkDataCache.clearAll();
    }

    @Nullable
    public MinecraftServer getServer()
    {
        return server;
    }

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return CompletableFuture.runAsync(() -> {
        }, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenRunAsync(() -> {
            LOGGER.debug("TFC Server Tracker Reloading");

            // Reload all recipe cache / ingredient maps.
            CollapseRecipe.CACHE.reload(getRecipes(TFCRecipeTypes.COLLAPSE));
            LandslideRecipe.CACHE.reload(getRecipes(TFCRecipeTypes.LANDSLIDE));
            HeatCapability.HeatManager.CACHE.reload(HeatCapability.HeatManager.INSTANCE.getValues());
            MetalItemManager.CACHE.reload(MetalItemManager.INSTANCE.getValues());
        }, gameExecutor);
    }

    @SuppressWarnings("unchecked")
    private <C extends IInventory, R extends IRecipe<C>> Collection<R> getRecipes(IRecipeType<R> recipeType)
    {
        return (Collection<R>) server.getRecipeManager().getRecipes(recipeType).values();
    }
}
