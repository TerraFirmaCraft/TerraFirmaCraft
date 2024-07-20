/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.forge;

import java.util.Optional;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

/**
 * Records the last three steps taken, and also the total number of steps taken since starting working.
 */
public record ForgeSteps(
    Optional<ForgeStep> last,
    Optional<ForgeStep> secondLast,
    Optional<ForgeStep> thirdLast,
    int total
)
{
    public static final Codec<ForgeSteps> CODEC = RecordCodecBuilder.create(i -> i.group(
        ForgeStep.CODEC.optionalFieldOf("last").forGetter(c -> c.last),
        ForgeStep.CODEC.optionalFieldOf("second_last").forGetter(c -> c.secondLast),
        ForgeStep.CODEC.optionalFieldOf("third_last").forGetter(c -> c.thirdLast),
        Codec.INT.optionalFieldOf("total", 0).forGetter(c -> c.total)
    ).apply(i, ForgeSteps::new));

    public static final StreamCodec<ByteBuf, ForgeSteps> STREAM_CODEC = StreamCodec.composite(
        ForgeStep.STREAM_CODEC, c -> c.last,
        ForgeStep.STREAM_CODEC, c -> c.secondLast,
        ForgeStep.STREAM_CODEC, c -> c.thirdLast,
        ByteBufCodecs.VAR_INT, c -> c.total,
        ForgeSteps::new
    );

    public static final ForgeSteps EMPTY = new ForgeSteps(Optional.empty(), Optional.empty(), Optional.empty(), 0);

    public ForgeSteps withStep(@Nullable ForgeStep step)
    {
        return new ForgeSteps(Optional.ofNullable(step), last, secondLast, total + 1);
    }

    @Override
    @SuppressWarnings("OptionalGetWithoutIsPresent") // We know that thirdLast => secondLast => last
    public String toString()
    {
        return thirdLast.isPresent() ? "[" + last.get() + ", " + secondLast.get() + ", " + thirdLast + ", ...]"
            : secondLast.isPresent() ? "[" + last.get() + ", " + secondLast.get() + ", ...]"
            : last.isPresent() ? "[" + last.get() + "]"
            : "[]";
    }

    /**
     * Checks if this is fresh new (no forging has been done yet)
     *
     * @return {@code true} if has been worked at least once, {@code false} otherwise
     */
    public boolean any()
    {
        return total > 0;
    }
}