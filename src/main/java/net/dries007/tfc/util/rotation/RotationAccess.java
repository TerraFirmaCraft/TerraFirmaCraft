/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.rotation;

import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;


@Deprecated
interface RotationAccess
{
    /**
     * Retrieves the rotation node at a given position, potentially belonging to another network, or disconnected.
     */
    @Nullable
    Node getNode(BlockPos pos);
}
