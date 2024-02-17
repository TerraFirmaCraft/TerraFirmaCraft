/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.world.level.levelgen.RandomState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.RandomStateExtension;

@Mixin(RandomState.class)
public abstract class RandomStateMixin implements RandomStateExtension
{
    @Unique
    @Nullable
    private ChunkGeneratorExtension tfc$chunkGeneratorExtension = null;

    @Override
    public void tfc$setChunkGeneratorExtension(@Nullable ChunkGeneratorExtension ex)
    {
        tfc$chunkGeneratorExtension = ex;
    }

    @Nullable
    @Override
    public ChunkGeneratorExtension tfc$getChunkGeneratorExtension()
    {
        return tfc$chunkGeneratorExtension;
    }
}