/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldGenSoilPits implements IWorldGenerator
{
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;
        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);

        BlockPos pos = world.getTopSolidOrLiquidBlock(chunkBlockPos.add(8 + random.nextInt(16), 0, 8 + random.nextInt(16)));
        generateClay(world, random, pos);

        pos = world.getTopSolidOrLiquidBlock(chunkBlockPos.add(8 + random.nextInt(16), 0, 8 + random.nextInt(16)));
        if (generatePeat(world, random, pos))
        {
            if (random.nextInt(5) == 0)
            {
//                if (!cloudberryGen.generate(world, random, pos)) //todo add berry gen
//                    cranberryGen.generate(world, random, pos);
            }
        }
    }

    private void generateClay(World world, Random rng, BlockPos start)
    {
        int radius = rng.nextInt(14) + 2;
        int depth = rng.nextInt(3) + 1;
        if (rng.nextInt(30) != 0 || start.getY() > WorldTypeTFC.SEALEVEL + 6) return;

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                if (x * x + z * z > radius * radius) continue;
                final BlockPos posHorizontal = start.add(x, 0, z);
                if (ChunkDataTFC.getRainfall(world, posHorizontal) < 500) continue;

                boolean flag = false;
                for (int y = -depth; y <= +depth; y++)
                {
                    final BlockPos pos = posHorizontal.add(0, y, 0);
                    final IBlockState current = world.getBlockState(pos);
                    if (BlocksTFC.isDirt(current))
                    {
                        world.setBlockState(pos, BlockRockVariant.get(ChunkDataTFC.getRockHeight(world, pos), Rock.Type.CLAY).getDefaultState(), 2);
                        flag = true;
                    }
                    else if (BlocksTFC.isGrass(current))
                    {
                        world.setBlockState(pos, BlockRockVariant.get(ChunkDataTFC.getRockHeight(world, pos), Rock.Type.CLAY_GRASS).getDefaultState(), 2);
                        flag = true;
                    }
                }
                if (flag && rng.nextInt(15) == 0)
                {
                    final BlockPos pos = world.getTopSolidOrLiquidBlock(posHorizontal);

                    for (Plant plant : TFCRegistries.PLANTS.getValuesCollection())
                    {
                        if (plant.getIsClayMarking())
                        {
                            BlockPlantTFC plantBlock = BlockPlantTFC.get(plant);
                            IBlockState state = plantBlock.getDefaultState();
                            int plantAge = plant.getAgeForWorldgen(rng, ClimateTFC.getHeightAdjustedBiomeTemp(world, pos));

                            if (!world.provider.isNether() && !world.isOutsideBuildHeight(pos) &&
                                plant.isValidLocation(ClimateTFC.getHeightAdjustedBiomeTemp(world, pos), ChunkDataTFC.getRainfall(world, pos), world.getLightFor(EnumSkyBlock.SKY, pos)) &&
                                world.isAirBlock(pos) &&
                                plantBlock.canBlockStay(world, pos, state))
                            {
                                world.setBlockState(pos, state.withProperty(BlockPlantTFC.AGE, plantAge), 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean generatePeat(World world, Random rng, BlockPos start)
    {
        int radius = rng.nextInt(16) + 8;
        byte depth = 2;
        boolean flag = false;

        if (rng.nextInt(30) != 0 || start.getY() > WorldTypeTFC.SEALEVEL) return false;

        for (int x = -radius; x <= radius; ++x)
        {
            for (int z = -radius; z <= radius; ++z)
            {
                if (x * x + z * z > radius * radius) continue;

                for (int y = -depth; y <= depth; ++y)
                {
                    final BlockPos pos = start.add(x, y, z);
                    if (!ClimateTFC.isSwamp(world, pos)) continue;
                    final IBlockState current = world.getBlockState(pos);

                    if (BlocksTFC.isGrass(current))
                    {
                        world.setBlockState(pos, BlocksTFC.PEAT_GRASS.getDefaultState(), 2);
                        flag = true;
                    }
                    else if (BlocksTFC.isDirt(current) || BlocksTFC.isClay(current))
                    {
                        world.setBlockState(pos, BlocksTFC.PEAT.getDefaultState(), 2);
                        flag = true;
                    }
                }
            }
        }
        return flag;
    }
}
