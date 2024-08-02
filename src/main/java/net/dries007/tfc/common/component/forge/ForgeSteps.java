/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.List;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import net.dries007.tfc.util.Helpers;

/**
 * Records the last three steps taken, and also the total number of steps taken since starting working.
 * @param steps The last three steps, in the order [thirdLast, secondLast, last]. If there are less than three total steps recorded, the list may
 *              contain up to three elements
 * @param total The total number of steps taken
 */
public record ForgeSteps(List<ForgeStep> steps, int total)
{
    public static final Codec<ForgeSteps> CODEC = RecordCodecBuilder.create(i -> i.group(
        ForgeStep.CODEC.listOf(0, 3).optionalFieldOf("steps", List.of()).forGetter(c -> c.steps),
        Codec.INT.optionalFieldOf("total", 0).forGetter(c -> c.total)
    ).apply(i, ForgeSteps::new));

    public static final StreamCodec<ByteBuf, ForgeSteps> STREAM_CODEC = StreamCodec.composite(
        ForgeStep.STREAM_CODEC.apply(ByteBufCodecs.list(3)), c -> c.steps,
        ByteBufCodecs.VAR_INT, c -> c.total,
        ForgeSteps::new
    );

    public static final ForgeSteps EMPTY = new ForgeSteps(List.of(), 0);

    public ForgeSteps withStep(ForgeStep step)
    {
        return new ForgeSteps(steps.size() == 3
            ? List.of(steps.get(1), steps.get(2), step)
            : Helpers.immutableAdd(steps, step),
            total + 1);
    }

    /**
     * Checks if this is fresh new (no forging has been done yet)
     *
     * @return {@code true} if has been worked at least once, {@code false} otherwise
     */
    public boolean isWorked()
    {
        return total > 0;
    }
}