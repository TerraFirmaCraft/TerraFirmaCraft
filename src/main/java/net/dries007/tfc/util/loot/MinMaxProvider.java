/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.loot;

import java.util.function.BiFunction;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public abstract class MinMaxProvider implements NumberProvider
{
    public static <T extends MinMaxProvider> MapCodec<T> codec(BiFunction<NumberProvider, NumberProvider, T> factory)
    {
        return RecordCodecBuilder.<T>mapCodec(i -> i.group(
            NumberProviders.CODEC.fieldOf("min").forGetter(c -> c.min),
            NumberProviders.CODEC.fieldOf("max").forGetter(c -> c.max)
        ).apply(i, factory));
    }

    protected final NumberProvider min, max;

    protected MinMaxProvider(NumberProvider min, NumberProvider max)
    {
        this.min = min;
        this.max = max;
    }
}
