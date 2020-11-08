package net.dries007.tfc.world.carver;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.chunkdata.RockData;

import static net.dries007.tfc.common.blocks.TFCBlockStateProperties.SUPPORTED;

/**
 * The default TFC implementation of {@link IBlockCarver}
 */
public abstract class BlockCarver implements IBlockCarver
{
    protected final Set<Block> carvableBlocks, supportableBlocks;
    protected final Map<Block, Block> exposedBlockReplacements;

    protected WorldGenRegion world;

    public BlockCarver()
    {
        carvableBlocks = new HashSet<>();
        supportableBlocks = new HashSet<>();
        exposedBlockReplacements = new HashMap<>();

        // This needs to run post rock reload
        RockManager.INSTANCE.addCallback(this::reload);

        reload();
    }

    protected void reload()
    {
        carvableBlocks.clear();
        exposedBlockReplacements.clear();

        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            carvableBlocks.add(rock.getBlock(Rock.BlockType.RAW));
            carvableBlocks.add(rock.getBlock(Rock.BlockType.GRAVEL));
            carvableBlocks.add(rock.getBlock(Rock.BlockType.COBBLE));
        }
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get());
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
            exposedBlockReplacements.put(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get(), TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
        }
        for (SandBlockType sand : SandBlockType.values())
        {
            supportableBlocks.add(TFCBlocks.SAND.get(sand).get());
        }

        supportableBlocks.addAll(carvableBlocks);
    }

    /**
     * If the state can be supported. Any block carved must implicitly support the above block
     */
    @SuppressWarnings("deprecation")
    protected boolean isSupportable(BlockState state)
    {
        return state.isAir() || state.hasProperty(TFCBlockStateProperties.SUPPORTED) || supportableBlocks.contains(state.getBlock());
    }

    /**
     * Set the block to be supported. This should be called from any carved block, with the above position and state.
     * If the block can be supported via property, set that. Otherwise replace with raw rock of the correct type (supported)
     */
    @SuppressWarnings("deprecation")
    protected void setSupported(IChunk chunk, BlockPos pos, BlockState state, RockData rockData)
    {
        // Don't try and support air blocks
        if (!state.isAir())
        {
            if (state.hasProperty(SUPPORTED))
            {
                chunk.setBlockState(pos, state.setValue(SUPPORTED, true), false);
            }
            else
            {
                chunk.setBlockState(pos, rockData.getRock(pos).getBlock(Rock.BlockType.RAW).defaultBlockState().setValue(SUPPORTED, true), false);
            }
        }
    }
}
