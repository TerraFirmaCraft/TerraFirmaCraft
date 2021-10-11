/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Aquifer;

public interface AquiferExtension extends Aquifer
{
    ChunkPos getPos();
}
