package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.FastRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.datafixers.Dynamic;
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

    public static List<Vein<?>> getNearbyVeins(IWorld world, ChunkPos pos, int radius)
    {
        List<Vein<?>> veins = new ArrayList<>();
        for (int x = pos.x - radius; x <= pos.x + radius; x++)
        {
            for (int z = pos.z - radius; z <= pos.z + radius; z++)
            {
                getVeinsAtChunk(world, new ChunkPos(x, z), veins);
            }
        }
        return veins;
    }

    private static void getVeinsAtChunk(IWorld world, ChunkPos pos, List<Vein<?>> veins)
    {
        RANDOM.setSeed(FastRandom.mix(FastRandom.mix(world.getSeed(), pos.x), pos.z));
        for (VeinType<?> type : VeinTypeManager.INSTANCE.getOrderedValues())
        {
            if (RANDOM.nextInt(type.getRarity()) == 0 && type.canGenerateVein(world, pos))
            {
                veins.add(type.createVein(pos.getXStart(), pos.getZStart(), RANDOM));
            }
        }
    }

    @SuppressWarnings("unused")
    public VeinsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory)
    {
        super(configFactory);
    }

    public VeinsFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        List<Vein<?>> veins = getNearbyVeins(worldIn, new ChunkPos(pos), CHUNK_RADIUS);
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
                        vein.getType().getStateToGenerate(stoneState, random).ifPresent(oreState -> {
                            if (random.nextFloat() < vein.getChanceToGenerate(posAt))
                            {
                                setBlockState(world, posAt, oreState);
                            }
                        });
                    }
                }
            }
        }
    }
}
