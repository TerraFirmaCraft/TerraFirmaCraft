/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.common.capabilities.forge.ForgeStep.*;

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

    static
    {
        assert VALUES.length < Byte.MAX_VALUE; // ForgeRule is serialized to a single byte
    }

    @Nullable
    public static ForgeRule valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    /**
     * @return {@code true} if a set of {@code rules} is self-consistent, meaning there exists at least one possible solution which satisfies all rules.
     */
    public static boolean isConsistent(ForgeRule... rules)
    {
        if (rules.length == 0 || rules.length > 3)
        {
            return false;
        }
        ForgeRule last = null, secondLast = null, thirdLast = null, notLast1 = null, notLast2 = null;
        for (ForgeRule rule : rules)
        {
            if (rule == last || rule == secondLast || rule == thirdLast || rule == notLast1 || rule == notLast2)
            {
                continue;
            }
            switch (rule.order)
            {
                case THIRD_LAST -> {
                    if (thirdLast != null)
                    {
                        return false;
                    }
                    thirdLast = rule;
                }
                case SECOND_LAST -> {
                    if (secondLast != null)
                    {
                        return false;
                    }
                    secondLast = rule;
                }
                case LAST -> {
                    if (last != null)
                    {
                        return false;
                    }
                    last = rule;
                }
                case NOT_LAST -> {
                    if (notLast2 != null)
                    {
                        return false;
                    }
                    notLast2 = notLast1;
                    notLast1 = rule;
                }
            }
        }
        return conflict3(notLast1, secondLast, thirdLast)
            && conflict3(secondLast, notLast1, notLast2)
            && conflict3(thirdLast, notLast1, notLast2);
    }

    private static boolean conflict3(@Nullable ForgeRule rule1, @Nullable ForgeRule rule2, @Nullable ForgeRule rule3)
    {
        return rule1 == null || rule2 == null || rule3 == null || rule1.type == rule2.type || rule1.type == rule3.type;
    }

    /**
     * Calculates the minimum number of steps to reach a total offset of {@code target} while satisfying the {@code rules}. Assumes the rules are consistent as determined by {@link #isConsistent(ForgeRule[])}
     */
    public static int calculateOptimalStepsToTarget(int target, final ForgeRule... rules)
    {
        final ForgeRule[] lastSteps = {null, null, null};
        for (final ForgeRule rule : rules)
        {
            switch (rule.order)
            {
                case LAST -> lastSteps[0] = rule;
                case SECOND_LAST -> lastSteps[1] = rule;
                case THIRD_LAST -> lastSteps[2] = rule;
            }
        }
        for (final ForgeRule rule : rules)
        {
            if (rule.order == Order.NOT_LAST || rule.order == Order.ANY)
            {
                boolean placed = false;
                for (int i = 2; i >= 0; i--)
                {
                    if (lastSteps[i] != null && lastSteps[i].type == rule.type && (rule.order == Order.ANY || i > 0))
                    {
                        lastSteps[i] = rule;
                        placed = true;
                        break;
                    }
                }
                if (!placed)
                {
                    for (int i = 2; i >= 0; i--)
                    {
                        if (lastSteps[i] == null)
                        {
                            lastSteps[i] = rule;
                            break;
                        }
                    }
                }
            }
        }
        int requiredSteps = 0, requiredHits = 0;
        for (ForgeRule rule : lastSteps)
        {
            if (rule != null)
            {
                requiredSteps++;
                target -= rule.type.step();
                if (rule.type == HIT_LIGHT)
                {
                    requiredHits++;
                }
            }
        }
        int minimumSteps = ForgeStep.getOptimalStepsToTarget(target);
        for (int hit = 0; hit < requiredHits * 2; hit++)
        {
            target -= HIT_LIGHT.step();
            minimumSteps = Math.min(minimumSteps, ForgeStep.getOptimalStepsToTarget(target));
        }
        return requiredSteps + minimumSteps;
    }

    public static ForgeRule fromNetwork(FriendlyByteBuf buffer)
    {
        final ForgeRule rule = valueOf(buffer.readByte());
        return rule == null ? HIT_ANY : rule;
    }

    private final Order order;
    private final ForgeStep type;

    ForgeRule(Order order, ForgeStep type)
    {
        this.order = order;
        this.type = type;

        assert type != HIT_MEDIUM && type != HIT_HARD;
    }

    public void toNetwork(FriendlyByteBuf buffer)
    {
        buffer.writeByte(ordinal());
    }

    public int iconX()
    {
        return type == HIT_LIGHT ? 218 : type.iconX();
    }

    public int iconY()
    {
        return type == HIT_LIGHT ? 18 : type.iconY();
    }

    public int overlayY()
    {
        return order.y;
    }

    public boolean matches(ForgeSteps steps)
    {
        return switch (order)
            {
                case ANY -> matches(steps.last()) || matches(steps.secondLast()) || matches(steps.thirdLast());
                case NOT_LAST -> matches(steps.secondLast()) || matches(steps.thirdLast());
                case LAST -> matches(steps.last());
                case SECOND_LAST -> matches(steps.secondLast());
                case THIRD_LAST -> matches(steps.thirdLast());
            };
    }

    public Component getDescriptionId()
    {
        return (type == HIT_LIGHT ? Helpers.translatable("tfc.enum.forgestep.hit") : Helpers.translateEnum(type))
            .append(" ")
            .append(Helpers.translateEnum(order));
    }

    private boolean matches(@Nullable ForgeStep step)
    {
        if (this.type == HIT_LIGHT)
        {
            return step == HIT_LIGHT || step == HIT_MEDIUM || step == HIT_HARD;
        }
        return type == step;
    }

    private enum Order
    {
        ANY(88),
        LAST(0),
        NOT_LAST(66),
        SECOND_LAST(22),
        THIRD_LAST(44);

        private final int y;

        Order(int y)
        {
            this.y = y;
        }
    }
}