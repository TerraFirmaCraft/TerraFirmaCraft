/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.dries007.tfc.util.Helpers;

public enum OreDeposit
{
    NATIVE_COPPER,
    NATIVE_SILVER,
    NATIVE_GOLD,
    CASSITERITE;

    public static final ItemPropertyProviderBlock.Type ROCK_PROPERTY = ItemPropertyProviderBlock.of(Helpers.identifier("rock"));
    public static final ItemPropertyProviderBlock.Type ORE_PROPERTY = ItemPropertyProviderBlock.of(Helpers.identifier("ore"));
}
