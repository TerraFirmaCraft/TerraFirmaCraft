package net.dries007.tfc.objects.blocks.soil;

import java.awt.*;
import javax.annotation.Nonnull;

public enum SandBlockType
{
    BROWN(new Color(30, 30, 30).getRGB()),
    WHITE(new Color(30, 30, 30).getRGB()),
    BLACK(new Color(30, 30, 30).getRGB()),
    RED(new Color(30, 30, 30).getRGB()),
    YELLOW(new Color(30, 30, 30).getRGB()),
    GREEN(new Color(30, 30, 30).getRGB()),
    PINK(new Color(30,30,30).getRGB());

    public static final int TOTAL = values().length;


    private static final SandBlockType[] VALUES = values();

    @Nonnull
    public static SandBlockType valueOf(int i)
    {
        return i >= 0 && i < TOTAL ? VALUES[i] : BROWN;
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
