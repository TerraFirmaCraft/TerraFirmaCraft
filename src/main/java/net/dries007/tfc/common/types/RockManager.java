/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.types;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.block.Block;

import net.dries007.tfc.util.data.DataManager;

public class RockManager extends DataManager.Instance<Rock>
{
    public static final RockManager INSTANCE = new RockManager();

    private final Map<Block, Rock> rockBlocks;

    private RockManager()
    {
        super(Rock::new, "rocks", "rock", false);

        rockBlocks = new HashMap<>();
    }

    /**
     * Gets the rock from a block from a O(1) lookup, rather than iterating the list of rocks
     */
    @Nullable
    public Rock getRock(Block block)
    {
        return rockBlocks.get(block);
    }

    @Override
    protected void postProcess()
    {
        rockBlocks.clear();
        for (Rock rock : types.values())
        {
            for (Rock.BlockType blockType : Rock.BlockType.values())
            {
                final Block optionalBlock = rock.getBlockOrNull(blockType);
                if (optionalBlock != null)
                {
                    rockBlocks.put(optionalBlock, rock);
                }
            }
        }

        super.postProcess();
    }
}