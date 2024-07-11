/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.Objects;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to the {@link ModConfigSpec} as part of the config object, which avoids accesses to the config (which are by far
 * the most common), having to unwrap the pair every time.
 */
public abstract class BaseConfig
{
    private @Nullable ModConfigSpec spec;

    void updateSpec(ModConfigSpec spec)
    {
        this.spec = spec;
    }

    public ModConfigSpec spec()
    {
        return Objects.requireNonNull(spec);
    }
}
