/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.chunkdata;

public enum ForestType
{
    OLD_GROWTH,
    NORMAL,
    SPARSE,
    NONE;

    private static final ForestType[] VALUES = values();

    public static ForestType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : NONE;
    }
}