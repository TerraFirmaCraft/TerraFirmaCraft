/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.WorldTypeTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

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

        // Set constant values here
        int xoff = chunkX * 16 + 8;
        int zoff = chunkZ * 16 + 8;
        // Get the proper list of veins
        List<Vein> veins = WorldGenOreVeins.getNearbyVeins(chunkX, chunkZ, world.getSeed(), 1);
        if (!veins.isEmpty())
        {
            veins.removeIf(v -> {
                if (!v.type.hasLooseRocks()) return true;

                int minScanY = (WorldTypeTFC.ROCKLAYER2 + WorldTypeTFC.ROCKLAYER3) / 2;
                int maxScanY = WorldTypeTFC.SEALEVEL + chunkData.getSeaLevelOffset(v.pos);

                // This is intensive and a painful check to have to do, but unfortunately necessary. In 1.14 this will be gone.
                for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(xoff - 7, minScanY, zoff - 7, xoff + 22, maxScanY, zoff + 22))
                {
                    if (v.type.isOreBlock(world.getBlockState(pos)))
                    {
                        return false;
                    }
                }
                return true;
            });
        }

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

    private void generateRock(World world, BlockPos pos, @Nullable Vein vein, Rock rock)
    {
        // Use air, so it doesn't replace other replaceable world gen
        // This matches the check in BlockPlacedItemFlat for if the block can stay
        if (world.isAirBlock(pos) && world.getBlockState(pos.down()).isFullBlock())
        {
            world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState(), 2);
            TEPlacedItemFlat tile = Helpers.getTE(world, pos, TEPlacedItemFlat.class);
            if (tile != null)
            {
                ItemStack stack = ItemStack.EMPTY;
                if (vein != null)
                {
                    stack = vein.type.getLooseRockItem();
                }
                if (stack.isEmpty())
                {
                    stack = ItemRock.get(rock, 1);
                }
                tile.setStack(stack);
            }
        }
    }

    @Nullable
    private Vein getRandomVein(List<Vein> veins, Random rand)
    {
        if (!veins.isEmpty() && rand.nextDouble() < 0.4)
        {
            return veins.get(rand.nextInt(veins.size()));
        }
        return null;
    }
}
