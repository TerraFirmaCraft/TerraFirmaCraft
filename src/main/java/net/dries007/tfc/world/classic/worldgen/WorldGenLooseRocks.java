/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEWorldItem;
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
                if (!v.oreSpawnData.ore.isGraded()) return true;

                int minScanY = (WorldTypeTFC.ROCKLAYER2 + WorldTypeTFC.ROCKLAYER3) / 2;
                int maxScanY = WorldTypeTFC.SEALEVEL + chunkData.getSeaLevelOffset(v.pos);
                return v.pos.getY() <= minScanY || v.pos.getY() >= maxScanY || !v.oreSpawnData.baseRocks.contains(chunkData.getRock1(0, 0));

            });
        }

        // Set constant values here
        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;

        for (int i = 0; i < 12; i++)
        {
            BlockPos pos = new BlockPos(
                xoff + random.nextInt(16),
                0,
                zoff + random.nextInt(16)
            );
            Rock rock = chunkData.getRock1(pos);
            generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY()), getRandomVein(veins, random), rock);
        }
    }

    private void generateRock(World world, BlockPos pos, @Nullable VeinType vein, Rock rock)
    {

        if (world.getBlockState(pos).getMaterial().isReplaceable() && !world.getBlockState(pos).getMaterial().isLiquid() && world.getBlockState(pos.down()).isFullCube())
        {
            //noinspection ConstantConditions
            world.setBlockState(pos, BlocksTFC.WORLD_ITEM.getDefaultState(), 2);
            TEWorldItem tile = (TEWorldItem) world.getTileEntity(pos);
            if (tile != null)
                tile.inventory.setStackInSlot(0, vein == null ? ItemRock.get(rock, 1) : ItemSmallOre.get(vein.oreSpawnData.ore, 1));
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
