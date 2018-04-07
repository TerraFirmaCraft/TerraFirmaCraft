package net.dries007.tfc.world.classic.worldgen;

import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.capabilities.ChunkDataTFC;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * todo: this causes cascading world gen!
 */
public class WorldGenSurfaceFissureCluster implements IWorldGenerator
{
    private final WorldGenFissure fissureGenAir;
    private final WorldGenFissure fissureGenFluid;

    public WorldGenSurfaceFissureCluster(boolean lava)
    {
        fissureGenFluid = new WorldGenFissure(lava ? ChunkGenTFC.LAVA : ChunkGenTFC.FRESH_WATER);
        fissureGenAir = new WorldGenFissure(ChunkGenTFC.AIR);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        final BlockPos start = new ChunkPos(chunkX, chunkZ).getBlock(random.nextInt(16) + 8, 0, random.nextInt(16) + 8);
        if (ChunkDataTFC.isStable(world, start)) return; // todo: this short-circuits a BUNCH of stuff, idk if it's a good idea.
        for (int i = 3 + random.nextInt(10); i > 0; i--)
        {
            BlockPos pos = world.getTopSolidOrLiquidBlock(start.add(-30 + random.nextInt(60), 0, -30 + random.nextInt(60))).add(0, -1, 0);
            if (random.nextInt(10) == 0) fissureGenAir.generate(world, random, pos);
            else fissureGenFluid.generate(world, random, pos);
        }
    }
}
