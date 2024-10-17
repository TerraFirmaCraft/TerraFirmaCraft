/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.Locale;
import java.util.Objects;
import com.google.common.base.CaseFormat;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * Provides access to the {@link ModConfigSpec} as part of the config object, which avoids accesses to the config (which are by far
 * the most common), having to unwrap the pair every time.
 */
public abstract class BaseConfig
{
    private @Nullable ModConfigSpec spec;

    public final void updateSpec(ModConfigSpec spec)
    {
        this.spec = spec;
    }

    public final ModConfigSpec spec()
    {
        return Objects.requireNonNull(spec);
    }

    /**
     * @return A name for a config value specialized on {@code value}, for example "douglasFir[suffix]"
     */
    protected final String getConfigName(Enum<?> value, String suffix)
    {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, value.name()) + suffix;
    }

    /**
     * @return A user-friendly proper name for an enum, for example "douglas fir"
     */
    protected final String getUserFriendlyName(Enum<?> value)
    {
        return value.name().toLowerCase(Locale.ROOT).replace('_', ' ');
    }
}
