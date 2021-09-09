/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import net.dries007.tfc.common.blocks.soil.SandBlockType;

public enum SandstoneBlockType
{
    RAW(color -> BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).strength(0.8f).requiresCorrectToolForDrops()),
    SMOOTH(color -> BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).strength(1.2f).requiresCorrectToolForDrops()),
    CUT(color -> BlockBehaviour.Properties.of(Material.STONE, color.getMaterialColor()).strength(1.2f).requiresCorrectToolForDrops()),
    // todo: chiseled?
    ;

    private final Function<SandBlockType, BlockBehaviour.Properties> factory;

    SandstoneBlockType(Function<SandBlockType, BlockBehaviour.Properties> factory)
    {
        this.factory = factory;
    }

    public BlockBehaviour.Properties properties(SandBlockType color)
    {
        return factory.apply(color);
    }
}
