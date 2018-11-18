/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.forge;

import javax.annotation.Nullable;

public enum ForgeStep
{
    HIT_LIGHT(-3),
    HIT_MEDIUM(-6),
    HIT_HARD(-9),
    DRAW(-15),
    PUNCH(2),
    BEND(7),
    UPSET(13),
    SHRINK(16);

    private static final ForgeStep[] values = values();

    @Nullable
    public static ForgeStep valueOf(int id)
    {
        return id >= 0 && id < values.length ? values[id] : null;
    }

    private final int stepAmount;

    ForgeStep(int stepAmount)
    {
        this.stepAmount = stepAmount;
    }

    public int getStepAmount()
    {
        return stepAmount;
    }
}
