/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.config;

public enum QuiverSearch
{
    DISABLED("Disabled"),
    ARMOR("Armor"),
    HOTBAR("Hotbar"),
    INVENTORY("Inventory");

    private final String name;

    QuiverSearch(String name)
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
