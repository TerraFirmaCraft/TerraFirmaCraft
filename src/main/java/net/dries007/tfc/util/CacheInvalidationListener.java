/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;

import net.dries007.tfc.common.commands.LocateVeinCommand;

/**
 * This is a manager for various cache invalidations, either on resource reload or server start/stop
 */
public enum CacheInvalidationListener implements SyncReloadListener
{
    INSTANCE;

    private final List<Runnable> actions;

    CacheInvalidationListener()
    {
        actions = new ArrayList<>();
    }

    public void doOnInvalidate(Runnable action)
    {
        this.actions.add(action);
    }

    @Override
    public void reloadSync()
    {
        LocateVeinCommand.clearCache();
        actions.forEach(Runnable::run);
    }
}