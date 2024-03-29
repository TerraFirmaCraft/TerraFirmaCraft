/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import org.jetbrains.annotations.Nullable;

public interface RandomStateExtension
{
    void tfc$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex);
    @Nullable ChunkGeneratorExtension tfc$getChunkGeneratorExtension();
}
