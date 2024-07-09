/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.world.biome.RegionBiomeSource;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public final class TFCWorldGen
{
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = DeferredRegister.create(Registries.CHUNK_GENERATOR, MOD_ID);
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCE = DeferredRegister.create(Registries.BIOME_SOURCE, MOD_ID);

    static
    {
        CHUNK_GENERATOR.register("overworld", () -> TFCChunkGenerator.CODEC);
        BIOME_SOURCE.register("overworld", () -> RegionBiomeSource.CODEC);
    }
}
