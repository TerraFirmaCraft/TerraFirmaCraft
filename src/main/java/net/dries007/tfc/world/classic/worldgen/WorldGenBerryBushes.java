package net.dries007.tfc.world.classic.worldgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.IBerryBush;
import net.dries007.tfc.objects.blocks.agriculture.BlockBerryBush;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenBerryBushes implements IWorldGenerator
{
    private static final List<IBerryBush> BUSHES = new ArrayList<>();

    public static void register(IBerryBush bush)
    {
        BUSHES.add(bush);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (random.nextInt(Math.max(40 - BUSHES.size(), 20)) == 0)
        {
            IBerryBush bush = BUSHES.get(random.nextInt(BUSHES.size()));
            BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);

            float temperature = ClimateTFC.getAverageBiomeTemp(world, chunkBlockPos);
            float rainfall = ChunkDataTFC.getRainfall(world, chunkBlockPos);

            if (bush.isValidConditions(temperature, rainfall))
            {
                final int x = (chunkX << 4) + random.nextInt(16) + 8;
                final int z = (chunkZ << 4) + random.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

                world.setBlockState(pos, BlockBerryBush.get(bush).getDefaultState());
            }
        }
    }
}
