/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.awt.*;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public enum SandBlockType
{
    BROWN(new Color(112, 113, 89).getRGB(), MaterialColor.DIRT),
    WHITE(new Color(202, 202, 201).getRGB(), MaterialColor.QUARTZ),
    BLACK(new Color(56, 56, 56).getRGB(), MaterialColor.TERRACOTTA_BLACK),
    RED(new Color(125, 99, 84).getRGB(), MaterialColor.TERRACOTTA_RED),
    YELLOW(new Color(215, 196, 140).getRGB(), MaterialColor.SAND),
    GREEN(new Color(106, 116, 81).getRGB(), MaterialColor.COLOR_GREEN),
    PINK(new Color(150, 101, 97).getRGB(), MaterialColor.TERRACOTTA_PINK);

    private static final SandBlockType[] VALUES = values();

    public static SandBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : BROWN;
    }

    private final int dustColor;
    private final MaterialColor materialColor;

    SandBlockType(int dustColor, MaterialColor materialColor)
    {
        this.dustColor = dustColor;
        this.materialColor = materialColor;
    }

    public MaterialColor getMaterialColor()
    {
        return materialColor;
    }

    public Block create()
    {
        return new TFCSandBlock(dustColor, BlockBehaviour.Properties.of(Material.SAND, materialColor).strength(0.5F).sound(SoundType.SAND));
    }
}