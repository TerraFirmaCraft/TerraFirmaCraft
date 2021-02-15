/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.noise.Metaballs3D;

public class DebugMetaballsFeature extends Feature<NoFeatureConfig>
{
    public DebugMetaballsFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        ChunkPos chunkPos = new ChunkPos(pos);
        if ((chunkPos.x & 1) == 0 && (chunkPos.z & 1) == 0)
        {
            final Metaballs3D noise = new Metaballs3D(16, random);
            final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
            for (int x = 0; x < 32; x++)
            {
                for (int z = 0; z < 32; z++)
                {
                    for (int y = 0; y < 32; y++)
                    {
                        mutablePos.setPos(pos).move(x - 8, y + 200, z - 8);
                        if (noise.noise(x - 16, y - 16, z - 16) > 0.5f)
                        {
                            world.setBlockState(mutablePos, Blocks.IRON_BLOCK.getDefaultState(), 3);
                        }
                    }
                }
            }
        }
        return true;
    }
}
