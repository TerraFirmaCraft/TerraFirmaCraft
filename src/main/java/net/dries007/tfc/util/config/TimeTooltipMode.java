/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.config;

/**
 * Used in config to toggle modes instead of an integer
 */
public enum TimeTooltipMode
{
    NONE("None"),
    TICKS("Ticks"),
    MINECRAFT_HOURS("Minecraft Hours"),
    REAL_MINUTES("Real Minutes");

    private final String name;

    TimeTooltipMode(String name)
    {
        this.name = name;
    }

    /**
     * Shows this text in config instead of the enum name
     */
    @Override
    public String toString()
    {
        return name;
    }
}
