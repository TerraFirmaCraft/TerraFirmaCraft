/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.soil;

import java.util.function.Supplier;

import net.minecraft.block.Block;

public class TFCDirtBlock extends Block
{
    private final Supplier<Block> grass;

    public TFCDirtBlock(Properties properties, Supplier<Block> grass)
    {
        super(properties);
        this.grass = grass;
    }

    public Block getGrass()
    {
        return grass.get();
    }
}
