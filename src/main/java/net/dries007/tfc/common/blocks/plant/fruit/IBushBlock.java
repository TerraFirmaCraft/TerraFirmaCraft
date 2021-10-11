/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant.fruit;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

// todo: this is bad because I'm mid-rework of bushes but it'll do for now.
public interface IBushBlock
{
    void onUpdate(Level level, BlockPos pos, BlockState state);
}
