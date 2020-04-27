package net.dries007.tfc.objects.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class TFCOreBlock extends Block
{
    public TFCOreBlock()
    {
        super(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(10, 10).harvestTool(ToolType.PICKAXE).harvestLevel(0));
    }
}
