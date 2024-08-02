/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.StreamCodecs;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.component.forge.ForgeStep.*;

public enum ForgeRule implements StringRepresentable
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

    public static final ForgeRule[] VALUES = values();
    public static final Codec<ForgeRule> CODEC = StringRepresentable.fromValues(ForgeRule::values);
    public static final StreamCodec<ByteBuf, ForgeRule> STREAM_CODEC = StreamCodecs.forEnum(ForgeRule::values);

    /**
     * @return {@code true} if a set of {@code rules} is self-consistent, meaning there exists at least one possible solution which satisfies all rules.
     */
    public static boolean isConsistent(ForgeRule... rules)
    {
        return isConsistent(List.of(rules));
    }

    public static boolean isConsistent(List<ForgeRule> rules)
    {
        if (rules.isEmpty() || rules.size() > 3)
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
     * Calculates the minimum number of steps to reach a total offset of {@code target} while satisfying the {@code rules}.
     * Assumes the rules are consistent as determined by {@link #isConsistent(List)}
     */
    public static int calculateOptimalStepsToTarget(int target, List<ForgeRule> rules)
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

    private final String serializedName;
    private final Order order;
    private final ForgeStep type;

    ForgeRule(Order order, ForgeStep type)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.order = order;
        this.type = type;

        assert type != HIT_MEDIUM && type != HIT_HARD;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
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

    public boolean matches(@Nullable ForgeStep last, @Nullable ForgeStep secondLast, @Nullable ForgeStep thirdLast)
    {
        return switch (order)
            {
                case ANY -> matches(last) || matches(secondLast) || matches(thirdLast);
                case NOT_LAST -> matches(secondLast) || matches(thirdLast);
                case LAST -> matches(last);
                case SECOND_LAST -> matches(secondLast);
                case THIRD_LAST -> matches(thirdLast);
            };
    }

    public Component getDescriptionId()
    {
        return (type == HIT_LIGHT ? Component.translatable("tfc.enum.forgestep.hit") : Helpers.translateEnum(type))
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