package net.dries007.tfc.images;

import java.awt.*;

import net.minecraft.util.math.MathHelper;

public final class Colors
{
    public static final ColorTransformer LINEAR_GRAY = value -> {
        int x = MathHelper.clamp((int) (255 * value), 0, 255);
        return new Color(x, x, x);
    };
    public static final ColorTransformer LINEAR_BLUE_RED = value -> {
        int x = MathHelper.clamp((int) (255 * value), 0, 255);
        return new Color(x, 0, 255 - x);
    };
    public static final ColorTransformer LINEAR_GREEN_YELLOW = value -> {
        int x = MathHelper.clamp((int) (255 * value), 0, 255);
        return new Color(x, 255, 0);
    };

    private static final Color[] COLORS_20 = new Color[] {
        new Color(0xFFB300),
        new Color(0x803E75),
        new Color(0xFF6800),
        new Color(0xA6BDD7),
        new Color(0xC10020),
        new Color(0xCEA262),
        new Color(0x817066),
        new Color(0x007D34),
        new Color(0xF6768E),
        new Color(0x00538A),
        new Color(0xFF7A5C),
        new Color(0x53377A),
        new Color(0xFF8E00),
        new Color(0xB32851),
        new Color(0xF4C800),
        new Color(0x7F180D),
        new Color(0x93AA00),
        new Color(0x593315),
        new Color(0xF13A13),
        new Color(0x232C16),
    };

    public static final ColorTransformer DISCRETE_20 = value -> COLORS_20[MathHelper.clamp((int) value * COLORS_20.length, 0, COLORS_20.length - 1)];
}
