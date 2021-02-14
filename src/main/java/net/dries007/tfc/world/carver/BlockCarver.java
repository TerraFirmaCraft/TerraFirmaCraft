/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.carver;

import java.util.*;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.world.chunkdata.RockData;

/**
 * A common class for single block carving logic
 * Holds context not provided by vanilla methods for carving
 */
public abstract class BlockCarver implements IContextCarver
{
    protected final Set<Block> carvableBlocks, supportableBlocks;
    protected final Map<Block, Block> exposedBlockReplacements;

    protected BitSet airCarvingMask;
    protected BitSet liquidCarvingMask;
    protected RockData rockData;

    public BlockCarver()
    {
        carvableBlocks = new HashSet<>();
        supportableBlocks = new HashSet<>();
        exposedBlockReplacements = new HashMap<>();

        // This needs to run post rock reload
        RockManager.INSTANCE.addCallback(this::reload);

        reload();
    }

    public abstract boolean carve(IChunk chunk, BlockPos pos, Random random, int seaLevel);

    @Override
    public void setContext(long worldSeed, BitSet airCarvingMask, BitSet liquidCarvingMask, RockData rockData, @Nullable BitSet waterAdjacencyMask)
    {
        this.airCarvingMask = airCarvingMask;
        this.liquidCarvingMask = liquidCarvingMask;
        this.rockData = rockData;
    }

    protected void reload()
    {
        carvableBlocks.clear();
        exposedBlockReplacements.clear();

        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            carvableBlocks.add(rock.getBlock(Rock.BlockType.RAW));
            carvableBlocks.add(rock.getBlock(Rock.BlockType.HARDENED));
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
     * If the state can be carved.
     */
    protected boolean isCarvable(BlockState state)
    {
        return carvableBlocks.contains(state.getBlock());
    }

    /**
     * If the state can be supported. Any block carved must ensure that exposed blocks (all but down) are supported
     */
    @SuppressWarnings("deprecation")
    protected boolean isSupportable(BlockState state)
    {
        return state.isAir() || supportableBlocks.contains(state.getBlock());
    }

    /**
     * Set the block to be supported. This should be called from any carved block, with the above position and state.
     * If the block can be supported via property, set that. Otherwise replace with raw rock of the correct type (supported), but only if the block above that is also not air (as otherwise this creates unsightly floating raw rock blocks.
     */
    @SuppressWarnings("deprecation")
    protected void setSupported(IChunk chunk, BlockPos pos, BlockState state, RockData rockData)
    {
        if (!state.isAir() && state.getFluidState().isEmpty() && !chunk.getBlockState(pos.up()).isAir())
        {
            chunk.setBlockState(pos, rockData.getRock(pos).getBlock(Rock.BlockType.HARDENED).getDefaultState(), false);
        }
    }
}
