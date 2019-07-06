/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockFireBrick extends Block
{
    public BlockFireBrick()
    {
        super(Material.ROCK);
        setSoundType(SoundType.STONE);
        setHardness(1.0F);
    }
}
