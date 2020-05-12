/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.config;

public enum HealthDisplayFormat
{
    TFC("TFC", "%.0f / %.0f"),
    VANILLA("Vanilla", "%.1f / %.1f"),
    TFC_CURRENT_HEALTH("TFC - Current Health Only", "%.0f"),
    VANILLA_CURRENT_HEALTH("Vanilla - Current Health Only", "%.1f");

    private final String name;
    private final String format;

    HealthDisplayFormat(String name, String format)
    {
        this.name = name;
        this.format = format;
    }

    /**
     * Shows this text in config instead of the enum name
     */
    @Override
    public String toString()
    {
        return name;
    }

    public String format(Object... args)
    {
        return String.format(format, args);
    }
}
