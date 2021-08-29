/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

public enum HealthDisplayStyle
{
    TFC((curr, max) -> String.format("%.0f / %.0f", curr * 50, max * 50)),
    VANILLA((curr, max) -> String.format("%.1f / %.1f", curr, max)),
    TFC_CURRENT((curr, max) -> String.format("%.0f", curr * 50)),
    VANILLA_CURRENT((curr, max) -> String.format("%.1f", curr));

    private final Formatter formatter;

    HealthDisplayStyle(Formatter formatter)
    {
        this.formatter = formatter;
    }

    public String format(float currentHealth, float maxHealth)
    {
        return formatter.apply(currentHealth, maxHealth);
    }

    @FunctionalInterface
    public interface Formatter
    {
        String apply(float currentHealth, float maxHealth);
    }
}
