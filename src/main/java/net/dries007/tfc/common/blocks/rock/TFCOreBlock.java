/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class TFCOreBlock extends Block
{
    public TFCOreBlock()
    {
        super(Block.Properties.of(Material.STONE).sound(SoundType.STONE).strength(10, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0));
    }
}