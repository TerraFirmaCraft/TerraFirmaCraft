/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.forge;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.util.forge.ForgeStep.*;

public enum ForgeRule
{
    HIT_ANY(Order.ANY, HIT_LIGHT),
    HIT_NOT_LAST(Order.NOT_LAST, HIT_LIGHT),
    HIT_LAST(Order.LAST, HIT_LIGHT),
    HIT_SECOND_LAST(Order.SECOND_LAST, HIT_LIGHT),
    HIT_THIRD_LAST(Order.THIRD_LAST, HIT_LIGHT),
    DRAW_ANY(Order.ANY, DRAW),
    DRAW_LAST(Order.LAST, DRAW),
    DRAW_NOT_LAST(Order.NOT_LAST, DRAW),
    DRAW_SECOND_LAST(Order.SECOND_LAST, DRAW),
    DRAW_THIRD_LAST(Order.THIRD_LAST, DRAW),
    PUNCH_ANY(Order.ANY, PUNCH),
    PUNCH_LAST(Order.LAST, PUNCH),
    PUNCH_NOT_LAST(Order.NOT_LAST, PUNCH),
    PUNCH_SECOND_LAST(Order.SECOND_LAST, PUNCH),
    PUNCH_THIRD_LAST(Order.THIRD_LAST, PUNCH),
    BEND_ANY(Order.ANY, BEND),
    BEND_LAST(Order.LAST, BEND),
    BEND_NOT_LAST(Order.NOT_LAST, BEND),
    BEND_SECOND_LAST(Order.SECOND_LAST, BEND),
    BEND_THIRD_LAST(Order.THIRD_LAST, BEND),
    UPSET_ANY(Order.ANY, UPSET),
    UPSET_LAST(Order.LAST, UPSET),
    UPSET_NOT_LAST(Order.NOT_LAST, UPSET),
    UPSET_SECOND_LAST(Order.SECOND_LAST, UPSET),
    UPSET_THIRD_LAST(Order.THIRD_LAST, UPSET),
    SHRINK_ANY(Order.ANY, SHRINK),
    SHRINK_LAST(Order.LAST, SHRINK),
    SHRINK_NOT_LAST(Order.NOT_LAST, SHRINK),
    SHRINK_SECOND_LAST(Order.SECOND_LAST, SHRINK),
    SHRINK_THIRD_LAST(Order.THIRD_LAST, SHRINK);

    private static final ForgeRule[] VALUES = values();

    public static int getID(@Nullable ForgeRule rule)
    {
        return rule == null ? -1 : rule.ordinal();
    }

    @Nullable
    public static ForgeRule valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    private final Order order;
    private final ForgeStep type;

    ForgeRule(@Nonnull Order order, @Nonnull ForgeStep type)
    {
        this.order = order;
        if (type == HIT_MEDIUM || type == HIT_HARD)
            this.type = HIT_LIGHT;
        else
            this.type = type;
    }

    @SideOnly(Side.CLIENT)
    public int getU()
    {
        return this.type == HIT_LIGHT ? 218 : this.type.getU();
    }

    @SideOnly(Side.CLIENT)
    public int getV()
    {
        return this.type == HIT_LIGHT ? 18 : this.type.getV();
    }

    @SideOnly(Side.CLIENT)
    public int getW()
    {
        return order.v;
    }

    public boolean matches(@Nonnull ForgeSteps steps)
    {
        switch (this.order)
        {
            case ANY:
                return matchesStep(steps.getStep(2)) || matchesStep(steps.getStep(1)) || matchesStep(steps.getStep(0));
            case NOT_LAST:
                return matchesStep(steps.getStep(1)) || matchesStep(steps.getStep(0));
            case LAST:
                return matchesStep(steps.getStep(2));
            case SECOND_LAST:
                return matchesStep(steps.getStep(1));
            case THIRD_LAST:
                return matchesStep(steps.getStep(0));
            default:
                return false;
        }
    }

    private boolean matchesStep(@Nullable ForgeStep step)
    {
        switch (this.type)
        {
            case HIT_LIGHT:
                return step == HIT_LIGHT || step == ForgeStep.HIT_MEDIUM || step == ForgeStep.HIT_HARD;
            default:
                return type == step;
        }
    }

    private enum Order
    {
        ANY(88),
        LAST(0),
        NOT_LAST(66),
        SECOND_LAST(22),
        THIRD_LAST(44);

        private final int v;

        Order(int v)
        {
            this.v = v;
        }
    }

}
