/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.metal;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockTrapDoorMetalTFC extends BlockTrapDoor
{
    private static final Map<Metal, BlockTrapDoorMetalTFC> MAP = new HashMap<>();

    public static BlockTrapDoorMetalTFC get(Metal metal)
    {
        return MAP.get(metal);
    }

    public final Metal metal;

    public BlockTrapDoorMetalTFC(Metal metal)
    {
        super(Material.IRON);
        this.metal = metal;
        if (MAP.put(metal, this) != null) throw new IllegalStateException("There can only be one.");
        setHardness(1F);
        setSoundType(SoundType.METAL);
        OreDictionaryHelper.register(this, "trapdoorMetal");
    }
}
