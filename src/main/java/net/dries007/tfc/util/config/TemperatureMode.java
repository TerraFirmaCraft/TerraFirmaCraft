/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.config;

public enum TemperatureMode
{
    CYCLIC("Cyclic"),
    ENDLESS("Endless");

    private final String name;

    TemperatureMode(String name)
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
