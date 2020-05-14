package net.dries007.tfc.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;

import net.dries007.tfc.objects.recipes.RecipeCache;

/**
 * This is a simple reload listener that just notifies anything in need of being notified when resources reload (cache invalidations, etc.)
 */
public enum TFCReloadListener implements IFutureReloadListener
{
    INSTANCE;

    @Override
    public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return CompletableFuture.runAsync(() -> {}, backgroundExecutor).thenCompose(stage::markCompleteAwaitingOthers).thenRunAsync(RecipeCache.INSTANCE::invalidate, gameExecutor);
    }
}
