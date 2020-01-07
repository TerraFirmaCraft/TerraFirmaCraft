/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.soil;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;

public class TFCSandBlock extends FallingBlock
{
    private final int color;

    public TFCSandBlock(Properties properties, int color)
    {
        super(properties);
        this.color = color;
    }

    @Override
    public int getDustColor(BlockState state)
    {
        return color;
    }

    public enum Type
    {
        BROWN,
        WHITE,
        BLACK,
        RED,
        YELLOW,
        GRAY

        // todo: color property
    }
}
