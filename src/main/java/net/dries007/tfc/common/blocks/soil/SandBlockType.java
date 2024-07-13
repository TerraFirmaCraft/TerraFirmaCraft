/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.soil;

import java.awt.Color;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public enum SandBlockType
{
    BROWN(new Color(112, 113, 89).getRGB(), MapColor.DIRT),
    WHITE(new Color(202, 202, 201).getRGB(), MapColor.QUARTZ),
    BLACK(new Color(56, 56, 56).getRGB(), MapColor.TERRACOTTA_BLACK),
    RED(new Color(125, 99, 84).getRGB(), MapColor.TERRACOTTA_RED),
    YELLOW(new Color(215, 196, 140).getRGB(), MapColor.SAND),
    GREEN(new Color(106, 116, 81).getRGB(), MapColor.COLOR_GREEN),
    PINK(new Color(150, 101, 97).getRGB(), MapColor.TERRACOTTA_PINK);

    private static final SandBlockType[] VALUES = values();

    public static SandBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : BROWN;
    }

    private final int dustColor;
    private final MapColor mapColor;

    SandBlockType(int dustColor, MapColor materialColor)
    {
        this.dustColor = dustColor;
        this.mapColor = materialColor;
    }

    public int getDustColor()
    {
        return dustColor;
    }

    public MapColor getMaterialColor()
    {
        return mapColor;
    }

    public Block create()
    {
        return new ColoredBlock(getDustColor(), BlockBehaviour.Properties.ofFullCopy(Blocks.SAND).mapColor(getMaterialColor()).strength(0.5F).sound(SoundType.SAND));
    }
}