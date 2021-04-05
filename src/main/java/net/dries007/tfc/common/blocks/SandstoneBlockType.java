package net.dries007.tfc.common.blocks;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

import net.dries007.tfc.common.blocks.soil.SandBlockType;

public enum SandstoneBlockType
{
    RAW(color -> AbstractBlock.Properties.of(Material.STONE, color.getMaterialColor()).strength(0.8f)),
    SMOOTH(color -> AbstractBlock.Properties.of(Material.STONE, color.getMaterialColor()).strength(1.2f)),
    CUT(color -> AbstractBlock.Properties.of(Material.STONE, color.getMaterialColor()).strength(1.2f)),
    // todo: chiseled?
    ;

    private final Function<SandBlockType, AbstractBlock.Properties> factory;

    SandstoneBlockType(Function<SandBlockType, AbstractBlock.Properties> factory)
    {
        this.factory = factory;
    }

    public AbstractBlock.Properties properties(SandBlockType color)
    {
        return factory.apply(color);
    }
}
