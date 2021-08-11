/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.Metaballs3D;

public class DebugMetaballsFeature extends Feature<NoneFeatureConfiguration>
{
    public DebugMetaballsFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel world, ChunkGenerator generator, Random random, BlockPos pos, NoneFeatureConfiguration config)
    {
        ChunkPos chunkPos = new ChunkPos(pos);
        if ((chunkPos.x & 1) == 0 && (chunkPos.z & 1) == 0)
        {
            final Metaballs3D noise = new Metaballs3D(16, random);
            final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
            for (int x = 0; x < 32; x++)
            {
                for (int z = 0; z < 32; z++)
                {
                    for (int y = 0; y < 32; y++)
                    {
                        mutablePos.set(pos).move(x - 8, y + 200, z - 8);
                        if (noise.inside(x - 16, y - 16, z - 16))
                        {
                            world.setBlock(mutablePos, Blocks.IRON_BLOCK.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        return true;
    }
}
