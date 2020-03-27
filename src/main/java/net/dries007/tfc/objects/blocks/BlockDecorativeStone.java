package net.dries007.tfc.objects.blocks;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class BlockDecorativeStone extends Block implements IItemSize
{
    public BlockDecorativeStone(MapColor blockMapColorIn)
    {
        super(Material.ROCK, blockMapColorIn);
        setSoundType(SoundType.STONE);
        setHardness(1.0F);
    }

    @Nonnull
    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.VERY_SMALL;
    }

    @Nonnull
    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
