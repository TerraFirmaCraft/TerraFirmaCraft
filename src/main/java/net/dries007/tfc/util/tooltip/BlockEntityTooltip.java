/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.tooltip;

import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A tooltip to display on a block or block entity.
 */
@FunctionalInterface
public interface BlockEntityTooltip
{
    void display(Level level, BlockState state, BlockPos pos, @Nullable BlockEntity entity, Consumer<Component> tooltip);
}
