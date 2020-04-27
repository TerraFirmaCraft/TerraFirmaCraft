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

    public ChunkBlockReplacer()
    {
        replacements = new HashMap<>();

        // Stone -> raw rock
        register(Blocks.STONE, (rockData, x, y, z, rainfall, temperature, random) -> {
            if (y < rockData.getBottomRockHeight())
            {
                return rockData.getBottomRock(x, z).getBlock(Rock.BlockType.RAW).getDefaultState();
            }
            else
            {
                return rockData.getTopRock(x, z).getBlock(Rock.BlockType.RAW).getDefaultState();
            }
        });

        // Dirt -> under surface material (dirt in high rainfall, sandstone (?) in low rainfall)
        register(Blocks.DIRT, (rockData, x, y, z, rainfall, temperature, noise) -> {
            if (rainfall > TFCConfig.COMMON.sandGrassRainfallCutoff.get() + TFCConfig.COMMON.sandGrassRainfallSpread.get() * noise)
            {
                return TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(rockData.getSoil(x, z)).get().getDefaultState();
            }
            else
            {
                return TFCBlocks.SAND.get(rockData.getSand(x, z)).get().getDefaultState();
            }
        });

        // Grass -> top layer surface material (grass in high rainfall, sand in low rainfall)
        register(Blocks.GRASS_BLOCK, new IBlockReplacer()
        {
            @Override
            public BlockState getReplacement(RockData rockData, int x, int y, int z, float rainfall, float temperature, float noise)
            {
                if (rainfall > TFCConfig.COMMON.sandGrassRainfallCutoff.get() + TFCConfig.COMMON.sandGrassRainfallSpread.get() * noise)
                {
                    return TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(rockData.getSoil(x, z)).get().getDefaultState();
                }
                else
                {
                    return TFCBlocks.SAND.get(rockData.getSand(x, z)).get().getDefaultState();
                }
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

        // Sand -> ocean floor material (gravel in low temperature, sand in high temperature)
        register(Blocks.SAND, (rockData, x, y, z, rainfall, temperature, noise) -> {
            if (temperature > TFCConfig.COMMON.sandGravelTemperatureCutoff.get() + TFCConfig.COMMON.sandGravelTemperatureSpread.get() * noise)
            {
                return TFCBlocks.SAND.get(rockData.getSand(x, z)).get().getDefaultState();
            }
            else
            {
                return rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).getDefaultState();
            }
        });
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
                float noise = random.nextFloat() - random.nextFloat(); // One simple gaussian noise value per column
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
    }

    protected void register(Block block, IBlockReplacer replacer)
    {
        if (replacements.containsKey(block))
        {
            LOGGER.debug("Replaced entry {} in ChunkBlockReplacer", block);
        }
        replacements.put(block, replacer);
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
