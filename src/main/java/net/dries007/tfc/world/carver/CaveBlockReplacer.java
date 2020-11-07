/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.GenerationStage;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.RockManager;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.rock.RawRockBlock.SUPPORTED;

/**
 * This is a block replacer, used for carvers to replace blocks with cave air, unsupported blocks with supported variants, and exposed blocks with surface variants.
 */
public class CaveBlockReplacer
{
    protected final Set<Block> carvableBlocks;
    protected final Map<Block, Block> exposedBlockReplacements;

    public CaveBlockReplacer()
    {
        carvableBlocks = new HashSet<>();
        exposedBlockReplacements = new HashMap<>();

        // This needs to run post rock reload
        RockManager.INSTANCE.addCallback(this::reload);

        reload();
    }

    public boolean carveBlock(IChunk chunk, BlockPos pos, GenerationStage.Carving stage, BitSet carvingMask, BitSet liquidCarvingMask)
    {
        // First, check if the location has already been carved by the current carving mask
        final int maskIndex = Helpers.getCarvingMaskIndex(pos);
        if (!carvingMask.get(maskIndex))
        {
            // Next, if we're in the air carving stage, check that we aren't bordering the liquid carving stage
            if (stage != GenerationStage.Carving.AIR || checkNoAdjacent(pos, liquidCarvingMask))
            {
                final BlockState stateAt = chunk.getBlockState(pos);
                if (carvableBlocks.contains(stateAt.getBlock()))
                {
                    if (pos.getY() < 11)
                    {
                        chunk.setBlockState(pos, Blocks.LAVA.defaultBlockState(), false);
                    }
                    else
                    {
                        chunk.setBlockState(pos, Blocks.CAVE_AIR.defaultBlockState(), false);
                    }
                    carvingMask.set(maskIndex);

                    // Adjust above and below blocks
                    final BlockPos posUp = pos.above();
                    final BlockState stateAbove = chunk.getBlockState(posUp);
                    if (stateAbove.hasProperty(SUPPORTED))
                    {
                        chunk.setBlockState(posUp, stateAbove.setValue(SUPPORTED, true), false);
                    }

                    // Check below state for replacements
                    BlockPos posDown = pos.below();
                    BlockState stateBelow = chunk.getBlockState(posDown);
                    if (exposedBlockReplacements.containsKey(stateBelow.getBlock()))
                    {
                        chunk.setBlockState(posDown, exposedBlockReplacements.get(stateBelow.getBlock()).defaultBlockState(), false);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkNoAdjacent(BlockPos pos, BitSet mask)
    {
        final int index = (pos.getX() & 15) | ((pos.getZ() & 15) << 4) | (pos.getY() << 8);
        return !(mask.get(index) ||
            ((pos.getY() > 0) && mask.get(index - (1 << 8))) ||
            ((pos.getY() < 255) && mask.get(index + (1 << 8))) ||
            (((pos.getX() & 15) > 0) && mask.get(index - 1)) ||
            (((pos.getX() & 15) < 15) && mask.get(index + 1)) ||
            (((pos.getZ() & 15) > 0) && mask.get(index - (1 << 4))) ||
            (((pos.getZ() & 15) < 15) && mask.get(index + (1 << 4)))
        );
    }

    private void reload()
    {
        carvableBlocks.clear();
        exposedBlockReplacements.clear();

        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            carvableBlocks.add(rock.getBlock(Rock.BlockType.RAW));
            carvableBlocks.add(rock.getBlock(Rock.BlockType.GRAVEL));
        }
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get());
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
            exposedBlockReplacements.put(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get(), TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
        }
    }
}