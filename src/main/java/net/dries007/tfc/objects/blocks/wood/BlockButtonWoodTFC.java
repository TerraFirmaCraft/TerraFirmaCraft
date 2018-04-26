/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.EnumMap;

import net.minecraft.block.BlockButtonWood;
import net.minecraft.block.SoundType;
import net.minecraft.init.Blocks;

import net.dries007.tfc.objects.Wood;

public class BlockButtonWoodTFC extends BlockButtonWood
{
    private static final EnumMap<Wood, BlockButtonWoodTFC> MAP = new EnumMap<>(Wood.class);

    public static BlockButtonWoodTFC get(Wood wood)
    {
        return MAP.get(wood);
    }

    public final Wood wood;

    public BlockButtonWoodTFC(Wood wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(0.5F);
        setSoundType(SoundType.WOOD);
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }
}
