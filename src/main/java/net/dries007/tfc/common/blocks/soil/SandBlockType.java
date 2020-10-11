/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks.soil;

import java.awt.*;

public enum SandBlockType
{
    BROWN(new Color(112, 113, 89).getRGB()),
    WHITE(new Color(202, 202, 201).getRGB()),
    BLACK(new Color(56, 56, 56).getRGB()),
    RED(new Color(125, 99, 84).getRGB()),
    YELLOW(new Color(215, 196, 140).getRGB()),
    GREEN(new Color(106, 116, 81).getRGB()),
    PINK(new Color(150, 101, 97).getRGB());

    private static final SandBlockType[] VALUES = values();

    public static SandBlockType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : BROWN;
    }

    private final int dustColor;

    SandBlockType(int dustColor)
    {
        this.dustColor = dustColor;
    }

    public int getDustColor()
    {
        return dustColor;
    }
}