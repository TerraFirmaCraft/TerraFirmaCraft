/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.objects.Ore;
import net.dries007.tfc.objects.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public class WorldGenLooseRocks implements IWorldGenerator
{

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;
        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        // Check dimension is overworld
        if (world.provider.getDimension() != 0) return;

        // Get the proper list of veins
        List<VeinType> veins = WorldGenOre.getNearbyVeins(chunkX, chunkZ, world.getSeed(), 1);
        if (!veins.isEmpty())
        {
            veins.removeIf(v -> {
                if (v.oreSpawnData.ore == null) return true;
                if (!v.oreSpawnData.ore.graded) return true;

                int minScanY = (WorldTypeTFC.ROCKLAYER2 + WorldTypeTFC.ROCKLAYER3) / 2;
                int maxScanY = WorldTypeTFC.SEALEVEL + chunkData.getSeaLevelOffset(v.pos);
                return v.pos.getY() <= minScanY || v.pos.getY() >= maxScanY || !v.oreSpawnData.baseRocks.contains(chunkData.getRock1(0, 0).rock);

            });
        }

        // Set constant values here
        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;

        for (int i = 0; i < 8; i++)
        {
            BlockPos pos = new BlockPos(
                xoff + random.nextInt(16),
                0,
                zoff + random.nextInt(16)
            );
            Rock rock = chunkData.getRock1(pos).rock;
            generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY() - 1), getRandomVein(veins, random), rock);
        }
    }

    private void generateRock(World world, BlockPos pos, @Nullable VeinType vein, Rock rock)
    {

        IBlockState stateAt = world.getBlockState(pos);
        if (world.isAirBlock(pos.up()) && stateAt.isFullCube())
        {
            // todo: replace this with actual ore stone blocks
            // ores are gotten from vein.oreSpawnData.ore
            // the current rock can be gotten from the rock
            world.setBlockState(pos.up(), vein == null ? BlockRockVariant.get(rock, Rock.Type.RAW).getDefaultState() : BlockOreTFC.get(vein.oreSpawnData.ore, Rock.ANDESITE, Ore.Grade.NORMAL), 2);
        }
    }

    @Nullable
    private VeinType getRandomVein(List<VeinType> veins, Random rand)
    {
        if (veins.isEmpty()) return null;
        if (rand.nextDouble() <= 0.4) return null;
        return veins.get(rand.nextInt(veins.size()));
    }
}
