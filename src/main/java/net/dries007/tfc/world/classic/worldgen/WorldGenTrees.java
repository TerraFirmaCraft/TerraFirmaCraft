/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.world.classic.worldgen;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.template.*;
import net.minecraftforge.fml.common.IWorldGenerator;

import net.dries007.tfc.api.ITreeGenerator;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLeavesTFC;
import net.dries007.tfc.objects.te.TEWorldItem;
import net.dries007.tfc.world.classic.ChunkGenTFC;
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;
import net.dries007.tfc.world.classic.worldgen.trees.TreeGenBushes;

public class WorldGenTrees implements IWorldGenerator
{

    private static final ITreeGenerator GEN_BUSHES = new TreeGenBushes();

    /**
     * This is a copy of the method included in the Template class, with some key differences.
     * This will ignore TEs / Entities, and does less checks for bad usage, since it will only be used for tree worldgen
     * It will do an additional check that the block is replaceable; important for tree growth; as to not replace other blocks
     *
     * @param worldIn     the world
     * @param pos         the position
     * @param template    the template
     * @param placementIn the placement settings
     */
    public static void addStructureToWorld(World worldIn, BlockPos pos, Template template, PlacementSettings placementIn)
    {
        int flags = 2;
        ITemplateProcessor templateProcessor = new BlockRotationProcessor(pos, placementIn);
        StructureBoundingBox structureboundingbox = placementIn.getBoundingBox();

        for (Template.BlockInfo template$blockinfo : template.blocks)
        {
            BlockPos blockpos = Template.transformedBlockPos(placementIn, template$blockinfo.pos).add(pos);
            Template.BlockInfo template$blockinfo1 = templateProcessor.processBlock(worldIn, blockpos, template$blockinfo);

            if (template$blockinfo1 != null)
            {
                Block block1 = template$blockinfo1.blockState.getBlock();

                if ((!placementIn.getIgnoreStructureBlock() || block1 != Blocks.STRUCTURE_BLOCK) && (structureboundingbox == null || structureboundingbox.isVecInside(blockpos)))
                {
                    IBlockState iblockstate = template$blockinfo1.blockState.withMirror(placementIn.getMirror());
                    IBlockState iblockstate1 = iblockstate.withRotation(placementIn.getRotation());

                    if (worldIn.getBlockState(blockpos).getMaterial().isReplaceable() || worldIn.getBlockState(blockpos).getBlock() instanceof BlockLeavesTFC)
                        worldIn.setBlockState(blockpos, iblockstate1, flags);

                }
            }
        }

        for (Template.BlockInfo template$blockinfo2 : template.blocks)
        {
            BlockPos blockpos1 = Template.transformedBlockPos(placementIn, template$blockinfo2.pos).add(pos);

            if (structureboundingbox == null || structureboundingbox.isVecInside(blockpos1))
            {
                worldIn.notifyNeighborsRespectDebug(blockpos1, template$blockinfo2.blockState.getBlock(), false);
            }

        }
    }

    private Tree getTree(List<Tree> trees, float density, Random random)
    {
        if (trees.size() == 1 || random.nextFloat() < 0.8f - density * 0.4f)
            return trees.get(0);
        return trees.get(1 + random.nextInt(trees.size() - 1));
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        if (!(chunkGenerator instanceof ChunkGenTFC)) return;

        final BlockPos chunkBlockPos = new BlockPos(chunkX << 4, 0, chunkZ << 4);
        ChunkDataTFC chunkData = ChunkDataTFC.get(world, chunkBlockPos);
        if (!chunkData.isInitialized()) return;

        final Biome b = world.getBiome(chunkBlockPos);
        //noinspection ConstantConditions
        if(!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN || b == BiomesTFC.LAKE || b == BiomesTFC.RIVER) return;

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

        final int spawnTries = 2 + (int) (density * 16f);
        final int maxTrees = Math.min(trees.size(), Math.min(5, (int) (1 + (density + diversity) * 2.5f)));
        trees = trees.subList(0, maxTrees);

        for (int i = 0; i < spawnTries; i++)
        {
            final int x = chunkX * 16 + random.nextInt(16) + 8;
            final int z = chunkZ * 16 + random.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x,0,z));
            final Tree tree = getTree(trees, density, random);

            tree.makeTree(manager, world, pos, random);
        }

        trees.removeIf(t -> !t.hasBushes);
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

    private void generateLooseSticks(Random rand, int chunkX, int chunkZ, World world, int amount)
    {
        for (int i = 0; i < amount; i++)
        {
            final int x = chunkX * 16 + rand.nextInt(16) + 8;
            final int z = chunkZ * 16 + rand.nextInt(16) + 8;
            final BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));

            if (world.getBlockState(pos).getMaterial().isReplaceable() && !world.getBlockState(pos).getMaterial().isLiquid() && world.getBlockState(pos.down()).isOpaqueCube())
            {
                //noinspection ConstantConditions
                world.setBlockState(pos, BlocksTFC.WORLD_ITEM.getDefaultState());
                TEWorldItem tile = (TEWorldItem) world.getTileEntity(pos);
                if (tile != null)
                    tile.inventory.setStackInSlot(0, new ItemStack(Items.STICK));
            }
        }
    }

}
