/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.network.StreamCodecs;

public enum ForgeStep implements StringRepresentable
{
    HIT_LIGHT(-3, 53, 50, 128, 224),
    HIT_MEDIUM(-6, 71, 50, 160, 224),
    HIT_HARD(-9, 53, 68, 192, 224),
    DRAW(-15, 71, 68, 224, 224),
    PUNCH(2, 89, 50, 0, 224),
    BEND(7, 107, 50, 32, 224),
    UPSET(13, 89, 68, 64, 224),
    SHRINK(16, 107, 68, 96, 224);

    public static final int LIMIT = 150;

    public static final Codec<ForgeStep> CODEC = StringRepresentable.fromValues(ForgeStep::values);
    public static final StreamCodec<ByteBuf, ForgeStep> STREAM_CODEC = StreamCodecs.forEnum(ForgeStep::values);

    public static final ForgeStep[] VALUES = values();
    private static final int[] PATHS;

    static
    {
        PATHS = new int[LIMIT];
        Arrays.fill(PATHS, -1);
        PATHS[0] = 0;

        final IntPriorityQueue queue = new IntArrayFIFOQueue();
        final IntList buffer = new IntArrayList(8);

        int reached = 1;
        queue.enqueue(0);
        for (int steps = 1; reached < LIMIT; steps++)
        {
            while (!queue.isEmpty())
            {
                final int value = queue.dequeueInt();
                for (ForgeStep step : VALUES)
                {
                    final int nextValue = value + step.step;
                    if (nextValue >= 0 && nextValue < LIMIT && PATHS[nextValue] == -1)
                    {
                        PATHS[nextValue] = steps;
                        buffer.add(nextValue);
                        reached++;
                    }
                }
            }
            buffer.forEach(queue::enqueue);
            buffer.clear();
        }
    }

    /**
     * @return The step by ordinal, or {@code null} or an invalid index.
     */
    @Nullable
    public static ForgeStep valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    public static int getOptimalStepsToTarget(int target)
    {
        return target < 0 || target >= PATHS.length ? Integer.MAX_VALUE : PATHS[target];
    }

    private final String serializedName;
    private final int step;
    private final int buttonX, buttonY, iconX, iconY;

    ForgeStep(int step, int buttonX, int buttonY, int iconX, int iconY)
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
        this.step = step;
        this.buttonX = buttonX;
        this.buttonY = buttonY;
        this.iconX = iconX;
        this.iconY = iconY;
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    public int step()
    {
        return step;
    }

    public int buttonX()
    {
        return buttonX;
    }

    public int buttonY()
    {
        return buttonY;
    }

    public int iconX()
    {
        return iconX;
    }

    public int iconY()
    {
        return iconY;
    }
}