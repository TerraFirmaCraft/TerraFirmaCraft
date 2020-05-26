package net.dries007.tfc.util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.server.MinecraftServer;

import net.dries007.tfc.objects.recipes.CollapseRecipe;
import net.dries007.tfc.objects.recipes.LandslideRecipe;
import net.dries007.tfc.objects.recipes.TFCRecipeTypes;

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

        // Register this listener as a reload listener
        this.server.getResourceManager().addReloadListener(this);
    }

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
        }, gameExecutor);
    }

    @SuppressWarnings("unchecked")
    private <C extends IInventory, R extends IRecipe<C>> Collection<R> getRecipes(IRecipeType<R> recipeType)
    {
        // For christ sake Mojang, why did you not return a Map<ResourceLocation, T>... like seriously.
        return server.getRecipeManager().getRecipes(recipeType).values().stream().map(r -> (R) r).collect(Collectors.toList());
    }
}
