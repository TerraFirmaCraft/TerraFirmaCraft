/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public interface SyncReloadListener extends PreparableReloadListener
{
    void reloadSync();

    @Override
    default CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return CompletableFuture.runAsync(() -> {}, backgroundExecutor).thenCompose(stage::wait).thenRunAsync(this::reloadSync, gameExecutor);
    }
}
