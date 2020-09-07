/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;

/**
 * Replaces world gen blocks with TFC equivalents.
 * This allows us to use minecraft blocks for the majority of early world gen, which works much better with vanilla systems such as surface builders.
 *
 * This structure is necessary in order to not bastardize the vanilla generation pipeline
 */
public class ChunkBlockReplacer
{
    protected final Map<Block, IBlockReplacer> replacements;
    private final BlockPos.Mutable mutablePos;
    private final long seed;

    public ChunkBlockReplacer(long seed)
    {
        this.seed = seed;
        this.mutablePos = new BlockPos.Mutable();
        this.replacements = new HashMap<>();

        // Stone block variants (Stone / Cobblestone)
        Function<Rock.BlockType, IBlockReplacer> factory = rockType -> (rockData, x, y, z, rainfall, temperature) -> {
            if (y < rockData.getRockHeight(x, z))
            {
                return rockData.getBottomRock(x, z).getBlock(rockType).getDefaultState();
            }
            else
            {
                return rockData.getTopRock(x, z).getBlock(rockType).getDefaultState();
            }
        };
        register(Blocks.STONE, factory.apply(Rock.BlockType.RAW));
        register(Blocks.COBBLESTONE, factory.apply(Rock.BlockType.COBBLE));

        // Dirt -> under surface material. Replaced with dirt, or inland sand
        // Grass -> top layer surface material (grass in high rainfall, sand in low rainfall)
        register(Blocks.DIRT, new SoilBlockReplacer(SoilBlockType.DIRT));
        register(Blocks.GRASS_BLOCK, new SoilBlockReplacer(SoilBlockType.GRASS));

        // Gravel -> surface material. Replace with rock type gravel
        register(Blocks.GRAVEL, (rockData, x, y, z, rainfall, temperature) -> rockData.getTopRock(x, z).getBlock(Rock.BlockType.GRAVEL).getDefaultState());

        // Sand -> Desert sand layer. Replace with sand color from top rock layer
        register(Blocks.SAND, (rockData, x, y, z, rainfall, temperature) -> TFCBlocks.SAND.get(rockData.getTopRock(x, z).getDesertSandColor()).get().getDefaultState());

        // Red Sand -> Beach sand layer. Replace with the beach sand color from top rock layer
        register(Blocks.RED_SAND, (rockData, x, y, z, rainfall, temperature) -> TFCBlocks.SAND.get(rockData.getTopRock(x, z).getBeachSandColor()).get().getDefaultState());

        // Red Sandstone -> Beach variant sand layer. If tropical, replace with pink sand.
        register(Blocks.RED_SANDSTONE, (rockData, x, y, z, rainfall, temperature) -> {
            if (rainfall > 300f && temperature > 15f)
            {
                return TFCBlocks.SAND.get(SandBlockType.PINK).get().getDefaultState();
            }
            else if (rainfall < 300f)
            {
                return TFCBlocks.SAND.get(SandBlockType.BLACK).get().getDefaultState();
            }
            else
            {
                return TFCBlocks.SAND.get(rockData.getMidRock(x, z).getBeachSandColor()).get().getDefaultState();
            }
        });
    }

    public void replace(IWorld worldGenRegion, IChunk chunk, ChunkData data)
    {
        final int xStart = chunk.getPos().getXStart();
        final int zStart = chunk.getPos().getZStart();
        final RockData rockData = data.getRockData();

        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                float temperature = data.getAverageTemp(x, z);
                float rainfall = data.getRainfall(x, z);

                for (int y = 0; y <= chunk.getTopBlockY(Heightmap.Type.WORLD_SURFACE_WG, x, z); y++)
                {
                    mutablePos.setPos(xStart + x, y, zStart + z);

                    // Base replacement
                    BlockState stateAt = chunk.getBlockState(mutablePos);
                    IBlockReplacer replacer = replacements.get(stateAt.getBlock());
                    if (replacer != null)
                    {
                        stateAt = replacer.getReplacement(rockData, xStart + x, y, zStart + z, rainfall, temperature);
                        chunk.setBlockState(mutablePos, stateAt, false);
                        replacer.updatePostPlacement(worldGenRegion, mutablePos, stateAt);
                    }
                }
            }
        }
    }

    public void register(Block block, IBlockReplacer replacer)
    {
        if (replacements.containsKey(block))
        {
            throw new IllegalStateException("Block " + block.getRegistryName() + " is already assigned to a replacement");
        }
        replacer.setSeed(seed);
        replacements.put(block, replacer);
    }
}
