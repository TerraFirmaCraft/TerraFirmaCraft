/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;
import java.util.function.ToIntFunction;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.WorldGenSettings;

public final class RarityBasedWorldGen implements IWorldGenerator
{
    private final ToIntFunction<WorldGenSettings> getRarityFunction;
    private final IWorldGenerator worldGenerator;

    public RarityBasedWorldGen(ToIntFunction<WorldGenSettings> getRarityFunction, IWorldGenerator worldGenerator)
    {
        this.getRarityFunction = getRarityFunction;
        this.worldGenerator = worldGenerator;
    }

    @Override
    public final void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC)
        {
            int rarity = getRarityFunction.applyAsInt(((ChunkGenTFC) chunkGenerator).s);
            if (rarity != 0 && random.nextInt(rarity) == 0)
                worldGenerator.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }
}
