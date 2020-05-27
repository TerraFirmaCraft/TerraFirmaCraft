/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.blocks.soil.TFCGrassBlock;
import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Replaces world gen blocks with TFC equivalents.
 * This allows us to use minecraft blocks for the majority of early world gen, which works much better with vanilla systems such as surface builders.
 */
public class ChunkBlockReplacer
{
    private static final Logger LOGGER = LogManager.getLogger();

    protected final Map<Block, IBlockReplacer> replacements;
    protected final Map<Block, Block> carvingReplacementRules;

    public ChunkBlockReplacer()
    {
        replacements = new HashMap<>();
        carvingReplacementRules = new HashMap<>();

        // Stone -> raw rock
        register(Blocks.STONE, (rockData, x, y, z, rainfall, temperature, random) -> {
            if (y < rockData.getRockHeight(x, z))
            {
                return rockData.getBottomRock(x, z).getBlock(Rock.BlockType.RAW).getDefaultState();
            }
            else
            {
                return rockData.getTopRock(x, z).getBlock(Rock.BlockType.RAW).getDefaultState();
            }
        });

        // Dirt -> under surface material. Replaced with dirt, or inland sand (deco rock layer)
        register(Blocks.DIRT, (rockData, x, y, z, rainfall, temperature, noise) -> getSoilBlock(SoilBlockType.DIRT, rockData, x, z, rainfall, noise));

        // Replace exposed dirt with grass in cave carvers
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            registerCarving(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get(), TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
        }

        // Grass -> top layer surface material (grass in high rainfall, sand in low rainfall)
        register(Blocks.GRASS_BLOCK, new IBlockReplacer()
        {
            @Override
            public BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature, float noise)
            {
                return getSoilBlock(SoilBlockType.GRASS, rockData, x, z, rainfall, noise);
            }

            @Override
            public void updatePostPlacement(IWorld world, BlockPos pos, BlockState state)
            {
                if (state.getBlock() instanceof TFCGrassBlock)
                {
                    world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 0);
                }
            }
        });

        // Gravel -> accent block used in some mountains
        register(Blocks.GRAVEL, (rockData, x, y, z, rainfall, temperature, random) -> rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).getDefaultState());

        // Sand -> ocean floor material. Switch based on the biome temp category
        register(Blocks.SAND, (rockData, x, y, z, rainfall, temperature, noise) -> {
            if (temperature > TFCConfig.COMMON.sandGravelTemperatureCutoff.get() + TFCConfig.COMMON.sandGravelTemperatureRange.get() * noise)
            {
                return TFCBlocks.SAND.get(rockData.getMidRock(x, z).getSandColor()).get().getDefaultState();
            }
            else
            {
                return rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).getDefaultState();
            }
        });

        // Sandstone (normal / red) used for shores. Replace with sand / gravel, using mid rock sand color where necessary.
        register(Blocks.SANDSTONE, (rockData, x, y, z, rainfall, temperature, noise) -> TFCBlocks.SAND.get(rockData.getMidRock(x, z).getSandColor()).get().getDefaultState());
        register(Blocks.RED_SANDSTONE, (rockData, x, y, z, rainfall, temperature, noise) -> rockData.getMidRock(x, z).getBlock(Rock.BlockType.GRAVEL).getDefaultState());
    }

    public void replace(IWorld worldGenRegion, IChunk chunk, Random random, RockData rockData, float rainfall, float temperature)
    {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int xStart = chunk.getPos().getXStart();
        int zStart = chunk.getPos().getZStart();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                float noise = random.nextFloat() - random.nextFloat(); // One simple "gaussian" noise value per column
                for (int y = 0; y <= chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE, x, z); y++)
                {
                    pos.setPos(xStart + x, y, zStart + z);
                    Block defaultBlock = chunk.getBlockState(pos).getBlock();
                    IBlockReplacer replacer = replacements.get(defaultBlock);
                    if (replacer != null)
                    {
                        BlockState replacementState = replacer.getReplacement(rockData, x, y, z, rainfall, temperature, noise);
                        chunk.setBlockState(pos, replacementState, false);
                        replacer.updatePostPlacement(worldGenRegion, pos, replacementState);
                    }
                }
            }
        }

        // todo: chunk fixer-upper
        // tasks:
        // 1) try and remove floating water by placing suitable blocks beneath it,
    }

    public void replaceCarving(IChunk chunk, BlockPos pos, BlockState stateAbove)
    {
        // Replace exposed surface materials
        BlockState stateDown = chunk.getBlockState(pos.down());
        if (carvingReplacementRules.containsKey(stateDown.getBlock()))
        {
            chunk.setBlockState(pos.down(), carvingReplacementRules.get(stateDown.getBlock()).getDefaultState(), false);
        }
    }

    protected void register(Block block, IBlockReplacer replacer)
    {
        if (replacements.containsKey(block))
        {
            LOGGER.debug("Replaced entry {} in ChunkBlockReplacer", block);
        }
        replacements.put(block, replacer);
    }

    protected void registerCarving(Block block, Block replacement)
    {
        if (carvingReplacementRules.containsKey(block))
        {
            LOGGER.debug("Replaced entry {} in ChunkBlockReplacer#carvingReplacementRules", block);
        }
        carvingReplacementRules.put(block, replacement);
    }

    private BlockState getSoilBlock(SoilBlockType soil, RockData rockData, int x, int z, float rainfall, float noise)
    {
        if (rainfall < TFCConfig.COMMON.sandRainfallCutoff.get() + TFCConfig.COMMON.sandRainfallRange.get() * noise)
        {
            return TFCBlocks.SAND.get(rockData.getTopRock(x, z).getSandColor()).get().getDefaultState();
        }
        else if (rainfall < TFCConfig.COMMON.sandyLoamRainfallCutoff.get() + TFCConfig.COMMON.sandyLoamRainfallRange.get() * noise)
        {
            return TFCBlocks.SOIL.get(soil).get(SoilBlockType.Variant.SANDY_LOAM).get().getDefaultState();
        }
        else if (rainfall < TFCConfig.COMMON.siltyLoamRainfallCutoff.get() + TFCConfig.COMMON.siltyLoamRainfallRange.get() * noise)
        {
            return TFCBlocks.SOIL.get(soil).get(SoilBlockType.Variant.SILTY_LOAM).get().getDefaultState();
        }
        else
        {
            return TFCBlocks.SOIL.get(soil).get(SoilBlockType.Variant.SILT).get().getDefaultState();
        }
    }

    @FunctionalInterface
    public interface IBlockReplacer
    {
        BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature, float noise);

        /**
         * Override to do additional things on post placement, such as schedule ticks
         */
        default void updatePostPlacement(IWorld world, BlockPos pos, BlockState state) {}
    }
}
