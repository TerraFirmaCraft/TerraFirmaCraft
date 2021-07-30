package net.dries007.tfc.util;


import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.plants.BlockShortGrassTFC;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.WorldGenLooseRocks;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

public class RegenRocksSticks extends WorldGenLooseRocks
{
    public RegenRocksSticks(boolean generateOres)
    {
        super(generateOres);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC && world.provider.getDimension() == 0)
        {
            final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
            final ChunkDataTFC baseChunkData = ChunkDataTFC.get(world, chunkBlockPos);

            // Get the proper list of veins
            Set<Vein> veins = Sets.newHashSet();
            int xoff = chunkX * 16 + 8;
            int zoff = chunkZ * 16 + 8;

            if (generateOres)
            {
                // Grab 2x2 area
                ChunkDataTFC[] chunkData = {
                    baseChunkData, // This chunk
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 0)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(0, 0, 16)),
                    ChunkDataTFC.get(world, chunkBlockPos.add(16, 0, 16))
                };
                if (!chunkData[0].isInitialized()) return;

                // Default to 35 below the surface, like classic
                int lowestYScan = Math.max(10, world.getTopSolidOrLiquidBlock(chunkBlockPos).getY() - ConfigTFC.General.WORLD.looseRockScan);

                for (ChunkDataTFC data : chunkData)
                {
                    veins.addAll(data.getGeneratedVeins());
                }

                if (!veins.isEmpty())
                {
                    veins.removeIf(v -> v.getType() == null || !v.getType().hasLooseRocks() || v.getHighestY() < lowestYScan);
                }
            }

            for (int i = 0; i < ConfigTFC.General.WORLD.looseRocksFrequency * factor; i++)
            {
                BlockPos pos = new BlockPos(xoff + random.nextInt(16), 0, zoff + random.nextInt(16));
                Rock rock = baseChunkData.getRock1(pos);
                generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY()), getRandomVein(Arrays.asList(veins.toArray(new Vein[0])), pos, random), rock);
            }
        }
    }

    @Override
    protected void generateRock(World world, BlockPos pos, @Nullable Vein vein, Rock rock)
    {
        if (isReplaceable(world, pos))
        {
            super.generateRock(world, pos, vein, rock);
        }
    }

    /*@Nullable
    private Vein getRandomVein(Set<Vein> veins, BlockPos pos, Random rand)
    {
        if (!veins.isEmpty() && rand.nextDouble() < 0.4)
        {
            Optional<Vein> vein = veins.stream().findAny();
            if (!veins.isEmpty())
            {
                Vein veintarget = vein.get();
                if (veintarget.inRange(pos.getX(), pos.getZ(), 8))
                {
                    return veintarget;
                }
            }
        }
        return null;
    }*/

    private static Boolean isReplaceable(World world, BlockPos pos)
    {
        //Modified to allow replacement of grass during spring regen
        Block test = world.getBlockState(pos).getBlock();
        return test instanceof BlockShortGrassTFC || test.isAir(world.getBlockState(pos), world, pos);
    }
}
