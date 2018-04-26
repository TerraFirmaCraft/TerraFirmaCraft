/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.EnumMap;

import net.minecraft.block.BlockButtonStone;
import net.minecraft.block.SoundType;

import net.dries007.tfc.objects.Rock;

public class BlockButtonStoneTFC extends BlockButtonStone
{
    private static final EnumMap<Rock, BlockButtonStoneTFC> MAP = new EnumMap<>(Rock.class);

    public static BlockButtonStoneTFC get(Rock rock)
    {
        return MAP.get(rock);
    }

    public final Rock rock;

    public BlockButtonStoneTFC(Rock rock)
    {
        this.rock = rock;
        if (MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(0.5F);
        setSoundType(SoundType.STONE);
    }
}
