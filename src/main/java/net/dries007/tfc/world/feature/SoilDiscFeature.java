package net.dries007.tfc.world.feature;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public class SoilDiscFeature extends Feature<SoilDiscConfig>
{
    public SoilDiscFeature(Codec<SoilDiscConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, SoilDiscConfig config)
    {
        boolean placed = false;
        final int radius = config.getRadius(random);
        final int radiusSquared = radius * radius;
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = pos.getX() - radius; x <= pos.getX() + radius; ++x)
        {
            for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; ++z)
            {
                final int relX = x - pos.getX();
                final int relZ = z - pos.getZ();
                if (relX * relX + relZ * relZ <= radiusSquared)
                {
                    for (int y = pos.getY() - config.getHeight(); y <= pos.getY() + config.getHeight(); ++y)
                    {
                        mutablePos.set(x, y, z);

                        final BlockState stateAt = world.getBlockState(mutablePos);
                        final BlockState stateReplacement = config.getState(stateAt);
                        if (stateReplacement != null)
                        {
                            world.setBlock(mutablePos, stateReplacement, 2);
                            placed = true;
                        }
                    }
                }
            }
        }
        // todo: debug, please remove
        if (placed)
        {
            world.setBlock(pos.above(10), Blocks.REDSTONE_BLOCK.defaultBlockState(), 2);
        }
        return placed;
    }
}
