/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.dries007.tfc.common.blocks.IBlockRain;

/**
 * Leaves blocks, which MUST
 * 1. Have the PERSISTENT properties as per {@link TFCLeavesBlock}
 * 2. Be treated as non-solid blocks, except for the purpose of snow layers
 */
public interface ILeavesBlock extends IBlockRain
{
}
