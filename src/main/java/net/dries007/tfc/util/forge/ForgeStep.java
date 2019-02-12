/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.forge;

import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public enum ForgeStep
{
    HIT_LIGHT(-3, 16, 34, 128, 192),
    HIT_MEDIUM(-6, 34, 34, 160, 192),
    HIT_HARD(-9, 16, 52, 192, 192),
    DRAW(-15, 34, 52, 224, 192),
    PUNCH(2, 126, 34, 128, 224),
    BEND(7, 144, 34, 160, 224),
    UPSET(13, 126, 52, 192, 224),
    SHRINK(16, 144, 52, 224, 224);

    private static final ForgeStep[] VALUES = values();

    @Nullable
    public static ForgeStep valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    private final int stepAmount;
    private final int x, y, u, v;

    ForgeStep(int stepAmount, int x, int y, int u, int v)
    {
        this.stepAmount = stepAmount;
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

    public int getStepAmount()
    {
        return stepAmount;
    }

    @SideOnly(Side.CLIENT)
    public int getX()
    {
        return x;
    }

    @SideOnly(Side.CLIENT)
    public int getY()
    {
        return y;
    }

    @SideOnly(Side.CLIENT)
    public int getU()
    {
        return u;
    }

    @SideOnly(Side.CLIENT)
    public int getV()
    {
        return v;
    }
}
