/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.food;

public enum Nutrient
{
    GRAIN,
    FRUIT,
    VEGETABLES,
    MEAT,
    DAIRY;

    public static final int TOTAL = values().length;
}
