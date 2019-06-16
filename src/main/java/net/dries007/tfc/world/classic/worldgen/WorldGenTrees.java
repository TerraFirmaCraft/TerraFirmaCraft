/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.api.util.ITreeGenerator;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.trees.TreeGenBushes;

public class WorldGenTrees implements IWorldGenerator
{
    private static final ITreeGenerator GEN_BUSHES = new TreeGenBushes();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;

        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        final Biome b = world.getBiome(chunkBlockPos);
        if (!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN || b == BiomesTFC.LAKE || b == BiomesTFC.RIVER)
            return;

        final TemplateManager manager = ((WorldServer) world).getStructureTemplateManager();
        final float diversity = chunkData.getFloraDiversity();
        final float density = chunkData.getFloraDensity();

        List<Tree> trees = chunkData.getValidTrees();
        Collections.rotate(trees, -(int) (diversity * (trees.size() - 1f)));

        int stickDensity = 3 + (int) (4f * density + 1.5f * trees.size());
        if (trees.isEmpty())
            stickDensity = 1 + (int) (1.5f * density);
        generateLooseSticks(random, chunkX, chunkZ, world, stickDensity);

        // This is to avoid giant regions of no trees whatsoever.
        // It will create sparse trees ( < 1 per chunk) by averaging the climate data to make it more temperate
        // The thought is in very harsh conditions, a few trees might survive outside their typical temperature zone
        if (trees.isEmpty())
        {
            if (random.nextFloat() > 0.2f)
                return;

            Tree extra = chunkData.getSparseGenTree();
            if (extra != null)
            {
                final int x = chunkX * 16 + random.nextInt(16) + 8;
                final int z = chunkZ * 16 + random.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                extra.makeTree(manager, world, pos, random);
            }
            return;
        }

        final int spawnTries = (int) (density * density * 20f);
        final int maxTrees = Math.min(trees.size(), Math.min(5, (int) (1 + (density + diversity) * 2.5f)));
        trees = trees.subList(0, maxTrees);

        for (int i = 0; i < spawnTries; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
            final Tree tree = getTree(trees, density, random);

            tree.makeTree(manager, world, pos, random);
        }

        trees.removeIf(t -> !t.hasBushes());
        // Small bushes in high density areas
        if (density > 0.6f && !trees.isEmpty()) // Density requirement is the same for jungles (kapok trees) to generate
        {
            for (int i = 0; i < trees.size() * 4f * density; i++)
            {
                final int x = chunkX * 16 + random.nextInt(16) + 8;
                final int z = chunkZ * 16 + random.nextInt(16) + 8;
                final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
                final Tree tree = getTree(trees, density, random);

                if (GEN_BUSHES.canGenerateTree(world, pos, tree))
                    GEN_BUSHES.generateTree(manager, world, pos, tree, random);
            }
        }
    }

    private Tree getTree(List<Tree> trees, float density, Random random)
    {
        if (trees.size() == 1 || random.nextFloat() < 0.8f - density * 0.4f)
            return trees.get(0);
        return trees.get(1 + random.nextInt(trees.size() - 1));
    }

    private void generateLooseSticks(Random rand, int chunkX, int chunkZ, World world, int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            final int x = chunkX * 16 + rand.nextInt(16) + 8;
            final int z = chunkZ * 16 + rand.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

            if (world.getBlockState(pos).getMaterial().isReplaceable() && !world.getBlockState(pos).getMaterial().isLiquid() && world.getBlockState(pos.down()).isOpaqueCube())
            {
                world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState());
                TEPlacedItemFlat tile = (TEPlacedItemFlat) world.getTileEntity(pos);
                if (tile != null)
                {
                    tile.setStack(new ItemStack(Items.STICK));
                }
            }
        }
    }

}
