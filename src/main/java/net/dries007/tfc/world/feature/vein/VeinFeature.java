package net.dries007.tfc.world.feature.vein;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.FastRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;

public abstract class VeinFeature<C extends VeinConfig, V extends Vein> extends Feature<C>
{
    private final Random chunkRandom;

    public VeinFeature(Codec<C> codec)
    {
        super(codec);

        this.chunkRandom = new Random();
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos pos, C config)
    {
        final List<V> veins = getNearbyVeins(worldIn, new ChunkPos(pos), config.getChunkRadius(), config);
        if (!veins.isEmpty())
        {
            for (V vein : veins)
            {
                place(worldIn, random, pos.getX(), pos.getZ(), vein, config);
            }
            return true;
        }
        return false;
    }

    protected void place(ISeedReader world, Random random, int xOff, int zOff, V vein, C config)
    {
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        for (int x = xOff; x < 16 + xOff; x++)
        {
            for (int z = zOff; z < 16 + zOff; z++)
            {
                // Do checks here that are specific to the the horizontal position, not the vertical one
                if (inRange(vein, x, z, config))
                {
                    for (int y = config.getMinY(); y <= config.getMaxY(); y++)
                    {
                        mutablePos.set(x, y, z);
                        BlockState stoneState = world.getBlockState(mutablePos);
                        BlockState oreState = getStateToGenerate(stoneState, random, config);
                        if (oreState != null)
                        {
                            if (random.nextFloat() < getChanceToGenerate(mutablePos, vein, config))
                            {
                                setBlock(world, mutablePos, oreState);
                            }
                        }
                    }
                }
            }
        }
    }

    protected boolean inRange(int x, int z, C config)
    {
        return -config.getSize() <= x && x <= config.getSize() && -config.getSize() <= z && z <= config.getSize();
    }

    @Nullable
    protected BlockState getStateToGenerate(BlockState stoneState, Random random, C config)
    {
        return config.getStateToGenerate(stoneState, random);
    }

    protected abstract float getChanceToGenerate(int x, int y, int z, V vein, C config);

    protected List<V> getNearbyVeins(ISeedReader world, ChunkPos pos, int radius, C config)
    {
        List<V> veins = new ArrayList<>();
        for (int x = pos.x - radius; x <= pos.x + radius; x++)
        {
            for (int z = pos.z - radius; z <= pos.z + radius; z++)
            {
                getVeinsAtChunk(world, x, z, veins, config);
            }
        }
        return veins;
    }

    protected void getVeinsAtChunk(ISeedReader world, int chunkPosX, int chunkPosZ, List<V> veins, C config)
    {
        chunkRandom.setSeed(FastRandom.next(FastRandom.next(world.getSeed(), chunkPosX), chunkPosZ));
        if (chunkRandom.nextInt(config.getRarity()) == 0)
        {
            veins.add(createVein(chunkPosX << 4, chunkPosZ << 4, chunkRandom, config));
        }
    }

    protected abstract V createVein(int chunkX, int chunkZ, Random random, C config);

    protected final BlockPos defaultPos(int chunkX, int chunkZ, Random random, C config)
    {
        return new BlockPos(chunkX + random.nextInt(16), defaultYPos(config.getSize(), random, config), chunkZ + random.nextInt(16));
    }

    protected final int defaultYPos(int verticalShrinkRange, Random rand, C config)
    {
        final int actualRange = config.getMaxY() - config.getMinY() - 2 * verticalShrinkRange;
        if (actualRange > 0)
        {
            return config.getMinY() + verticalShrinkRange + rand.nextInt(actualRange);
        }
        else
        {
            return (config.getMinY() + config.getMaxY()) / 2;
        }
    }

    private boolean inRange(V vein, int x, int z, C config)
    {
        return inRange(vein.getPos().getX() - x, vein.getPos().getZ() - z, config);
    }

    private float getChanceToGenerate(BlockPos pos, V vein, C config)
    {
        return getChanceToGenerate(pos.getX() - vein.getPos().getX(), pos.getY() - vein.getPos().getY(), pos.getZ() - vein.getPos().getZ(), vein, config);
    }
}
