/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.vein.Vein;

public class WorldGenLooseRocks implements IWorldGenerator
{
    private final boolean generateOres;
    private double factor;

    public WorldGenLooseRocks(boolean generateOres)
    {
        this.generateOres = generateOres;
        factor = 1;
    }

    public void setFactor(double factor)
    {
        if (factor < 0) factor = 0;
        if (factor > 1) factor = 1;
        this.factor = factor;
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (chunkGenerator instanceof ChunkGenTFC && world.provider.getDimension() == 0)
        {
            final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
            final ChunkDataTFC baseChunkData = ChunkDataTFC.get(world, chunkBlockPos);

            // Get the proper list of veins
            List<Vein> veins = Collections.emptyList();
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
                if (!chunkData[0].isInitialized())
                {
                    return;
                }

                // Default to 35 below the surface, like classic
                int lowestYScan = Math.max(10, world.getTopSolidOrLiquidBlock(chunkBlockPos).getY() - ConfigTFC.General.WORLD.looseRockScan);


                veins = WorldGenOreVeins.getNearbyVeins(chunkX, chunkZ, world.getSeed(), 1);
                if (!veins.isEmpty())
                {
                    veins.removeIf(v -> {
                        if (v.getType() == null || !v.getType().hasLooseRocks() || v.getHighestY() < lowestYScan)
                        {
                            return true;
                        }
                        for (ChunkDataTFC data : chunkData)
                        {
                            // No need to check for initialized chunk data, ores will be empty.
                            if (data.getGeneratedVeins().contains(v))
                            {
                                return false;
                            }
                        }
                        return true;
                    });
                }
            }

            for (int i = 0; i < ConfigTFC.General.WORLD.looseRocksFrequency * factor; i++)
            {
                BlockPos pos = new BlockPos(
                    xoff + random.nextInt(16),
                    0,
                    zoff + random.nextInt(16)
                );
                Rock rock = baseChunkData.getRock1(pos);
                generateRock(world, pos.up(world.getTopSolidOrLiquidBlock(pos).getY()), getRandomVein(veins, pos, random), rock);
            }
        }
    }

    private void generateRock(World world, BlockPos pos, @Nullable Vein vein, Rock rock)
    {
        // Use air, so it doesn't replace other replaceable world gen
        // This matches the check in BlockPlacedItemFlat for if the block can stay
        // Also, only add on soil, since this is called by the world regen handler later
        if (world.isAirBlock(pos) && world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) && BlocksTFC.isSoil(world.getBlockState(pos.down())))
        {
            world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState(), 2);
            TEPlacedItemFlat tile = Helpers.getTE(world, pos, TEPlacedItemFlat.class);
            if (tile != null)
            {
                ItemStack stack = ItemStack.EMPTY;
                if (vein != null && vein.getType() != null)
                {
                    if (ConfigTFC.General.WORLD.enableLooseOres)
                    {
                        stack = vein.getType().getLooseRockItem();
                    }
                }
                if (stack.isEmpty())
                {
                    if (ConfigTFC.General.WORLD.enableLooseRocks)
                    {
                        stack = ItemRock.get(rock, 1);
                    }
                }
                if (!stack.isEmpty())
                {
                    tile.setStack(stack);
                }
            }
        }
    }

    @Nullable
    private Vein getRandomVein(List<Vein> veins, BlockPos pos, Random rand)
    {
        if (!veins.isEmpty() && rand.nextDouble() < 0.4)
        {
            Vein vein = veins.get(rand.nextInt(veins.size()));
            if (vein.inRange(pos.getX(), pos.getZ(), 8))
            {
                return vein;
            }
        }
        return null;
    }
}
