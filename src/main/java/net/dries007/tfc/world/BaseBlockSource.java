/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.BaseStoneSource;

public interface BaseBlockSource extends BaseStoneSource
{
    BlockState modifyFluid(BlockState fluidOrAir, int x, int z);
}
