/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockPeat extends Block
{
    public BlockPeat(Material material)
    {
        super(material);
        setSoundType(SoundType.GROUND);
        setHardness(0.6F);
        setHarvestLevel("shovel", 0);
        OreDictionaryHelper.register(this, "peat");
        Blocks.FIRE.setFireInfo(this, 5, 10);
    }
}
