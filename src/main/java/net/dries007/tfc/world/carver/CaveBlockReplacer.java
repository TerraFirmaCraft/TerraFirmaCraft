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

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.types.RockManager;

import static net.dries007.tfc.objects.blocks.rock.RawRockBlock.SUPPORTED;

/**
 * This is a block replacer, used for carvers to replace blocks with cave air, unsupported blocks with supported variants, and exposed blocks with surface variants.
 */
public class CaveBlockReplacer
{
    protected final Set<Block> carvableBlocks, carvableBlocksAboveSeaLevel;
    protected final Map<Block, Block> exposedBlockReplacements;

    public CaveBlockReplacer()
    {
        carvableBlocks = new HashSet<>();
        carvableBlocksAboveSeaLevel = new HashSet<>();
        exposedBlockReplacements = new HashMap<>();

        // This needs to run post rock reload
        RockManager.INSTANCE.addCallback(this::reload);

        reload();
    }

    public boolean carveBlock(IChunk chunk, BlockPos pos, BitSet carvingMask)
    {
        int maskIndex = (pos.getX() & 15) | ((pos.getZ() & 15) << 4) | (pos.getY() << 8);
        if (!carvingMask.get(maskIndex))
        {
            BlockPos posUp = pos.up();
            BlockState stateAt = chunk.getBlockState(pos);
            BlockState stateAbove = chunk.getBlockState(posUp);

            Set<Block> carvableBlockChecks = pos.getY() > TFCConfig.COMMON.seaLevel.get() ? carvableBlocksAboveSeaLevel : carvableBlocks;
            if (carvableBlockChecks.contains(stateAt.getBlock()) && (carvableBlockChecks.contains(stateAbove.getBlock()) || stateAbove.isAir(chunk, posUp)))
            {
                if (pos.getY() < 11)
                {
                    chunk.setBlockState(pos, Blocks.LAVA.getDefaultState(), false);
                }
                else
                {
                    chunk.setBlockState(pos, Blocks.CAVE_AIR.getDefaultState(), false);
                }
                carvingMask.set(maskIndex);

                // Adjust above and below blocks
                if (stateAbove.has(SUPPORTED))
                {
                    chunk.setBlockState(posUp, stateAbove.with(SUPPORTED, true), false);
                }

                // Check below state for replacements
                BlockPos posDown = pos.down();
                BlockState stateBelow = chunk.getBlockState(posDown);
                if (exposedBlockReplacements.containsKey(stateBelow.getBlock()))
                {
                    chunk.setBlockState(posDown, exposedBlockReplacements.get(stateBelow.getBlock()).getDefaultState(), false);
                }
                return true;
            }
        }
        return false;
    }

    private void reload()
    {
        carvableBlocks.clear();
        carvableBlocksAboveSeaLevel.clear();
        exposedBlockReplacements.clear();

        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            carvableBlocks.add(rock.getBlock(Rock.BlockType.RAW));
            carvableBlocksAboveSeaLevel.add(rock.getBlock(Rock.BlockType.RAW));
            carvableBlocksAboveSeaLevel.add(rock.getBlock(Rock.BlockType.GRAVEL));
        }
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            carvableBlocksAboveSeaLevel.add(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get());
            carvableBlocksAboveSeaLevel.add(TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
            exposedBlockReplacements.put(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get(), TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
        }
    }
}
