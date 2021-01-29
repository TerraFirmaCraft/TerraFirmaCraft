/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.ArrayList;

import net.minecraftforge.common.ForgeConfigSpec;

import net.dries007.tfc.util.Cache;

public class CachingConfig
{
    private final ArrayList<Cache> cache;

    protected CachingConfig()
    {
        this.cache = new ArrayList<>();
    }

    public void reload()
    {
        for (Cache e : cache)
        {
            e.reload();
        }
    }

    protected Cache.Boolean wrap(ForgeConfigSpec.BooleanValue config)
    {
        return wrap(new Cache.Boolean(config::get));
    }

    protected Cache.Int wrap(ForgeConfigSpec.IntValue config)
    {
        return wrap(new Cache.Int(config::get));
    }

    protected Cache.Double wrap(ForgeConfigSpec.DoubleValue config)
    {
        return wrap(new Cache.Double(config::get));
    }

    protected <T> Cache.Object<T> wrap(ForgeConfigSpec.ConfigValue<T> config)
    {
        return wrap(new Cache.Object<>(config::get));
    }

    private <T extends Cache> T wrap(T t)
    {
        cache.add(t);
        return t;
    }
}
