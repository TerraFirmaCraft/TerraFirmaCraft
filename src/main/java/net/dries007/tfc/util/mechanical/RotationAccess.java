/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.mechanical;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.Helpers;

public interface RotationAccess
{
    static RotationAccess of(Level level)
    {
        return pos -> {
            final BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null)
            {
                return Helpers.getCapability(entity, RotationCapability.CAPABILITY);
            }
            return null;
        };
    }

    /**
     * Retrieves the rotation node at a given position, potentially belonging to another network, or disconnected.
     */
    @Nullable
    Node getNode(BlockPos pos);
}
