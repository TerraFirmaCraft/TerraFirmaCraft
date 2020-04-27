package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.FastRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import net.dries007.tfc.world.vein.Vein;
import net.dries007.tfc.world.vein.VeinType;
import net.dries007.tfc.world.vein.VeinTypeManager;

public class VeinsFeature extends Feature<NoFeatureConfig>
{
    private static final Random RANDOM = new Random();
    private static int CHUNK_RADIUS = 0;

    public static void resetChunkRadius()
    {
        CHUNK_RADIUS = 1 + VeinTypeManager.INSTANCE.getValues().stream().mapToInt(VeinType::getChunkRadius).max().orElse(0);
    }

    public static List<Vein<?>> getNearbyVeins(int chunkX, int chunkZ, long worldSeed, int radius)
    {
        List<Vein<?>> veins = new ArrayList<>();
        for (int x = chunkX - radius; x <= chunkX + radius; x++)
        {
            for (int z = chunkZ - radius; z <= chunkZ + radius; z++)
            {
                getVeinsAtChunk(veins, x, z, worldSeed);
            }
        }
        return veins;
    }

    private static void getVeinsAtChunk(List<Vein<?>> veins, int chunkX, int chunkZ, long worldSeed)
    {
        long seed = FastRandom.mix(worldSeed, chunkX);
        seed = FastRandom.mix(seed, chunkZ);
        RANDOM.setSeed(seed);
        for (VeinType<?> type : VeinTypeManager.INSTANCE.getOrderedValues())
        {
            if (RANDOM.nextInt(type.getRarity()) <= 5) // todo: change back to == 0, this is only for testing
            {
                veins.add(type.createVein(chunkX << 4, chunkZ << 4, RANDOM));
            }
        }
    }

    public VeinsFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        List<Vein<?>> veins = getNearbyVeins(pos.getX() >> 4, pos.getZ() >> 4, worldIn.getSeed(), CHUNK_RADIUS);
        if (!veins.isEmpty())
        {
            for (Vein<?> vein : veins)
            {
                generate(worldIn, rand, pos.getX(), pos.getZ(), vein);
            }
            return true;
        }
        return false;
    }

    private void generate(IWorld world, Random random, int xOff, int zOff, Vein<?> vein)
    {
        for (int x = xOff; x < 16 + xOff; x++)
        {
            for (int z = zOff; z < 16 + zOff; z++)
            {
                // Do checks here that are specific to the the horizontal position, not the vertical one
                if (vein.inRange(x, z))
                {
                    for (int y = vein.getType().getMinY(); y <= vein.getType().getMaxY(); y++)
                    {
                        BlockPos posAt = new BlockPos(x, y, z);
                        BlockState stoneState = world.getBlockState(posAt);
                        if (vein.getType().isValidState(stoneState) && random.nextFloat() < vein.getChanceToGenerate(posAt))
                        {
                            BlockState oreState = vein.getType().getStateToGenerate(stoneState, random);
                            setBlockState(world, posAt, oreState);
                        }
                    }
                }
            }
        }
    }
}
