/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Function;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;

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
