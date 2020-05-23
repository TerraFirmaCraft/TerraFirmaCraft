/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockPressurePlateTFC extends BlockPressurePlate
{
    private static final Map<Rock, BlockPressurePlateTFC> MAP = new HashMap<>();

    public static BlockPressurePlateTFC get(Rock rock)
    {
        return MAP.get(rock);
    }

    public BlockPressurePlateTFC(Rock rock)
    {
        super(Material.ROCK, Sensitivity.MOBS);
        if (MAP.put(rock, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(0.5F);
        setSoundType(SoundType.STONE);

        OreDictionaryHelper.register(this, "pressure_plate_stone");
    }
}
