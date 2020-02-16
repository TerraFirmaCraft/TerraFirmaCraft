package net.dries007.tfc.util.tags;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;

public class TFCBlockTags
{
    public static final Tag<Block> GRASS = create("grass");

    private static Tag<Block> create(String id)
    {
        return new BlockTags.Wrapper(new ResourceLocation(TerraFirmaCraft.MOD_ID, id));
    }
}
