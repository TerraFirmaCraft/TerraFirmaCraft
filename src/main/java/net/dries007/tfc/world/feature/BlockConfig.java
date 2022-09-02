/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.function.Function;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.dries007.tfc.world.Codecs;
import org.jetbrains.annotations.Nullable;

public record BlockConfig<T extends Block>(T block) implements FeatureConfiguration
{
    public static <T extends Block> Codec<BlockConfig<T>> codec(Function<Block, T> converterOrNull, String onError)
    {
        return Codecs.BLOCK.fieldOf("block").codec().comapFlatMap(block -> {
            final @Nullable T t = converterOrNull.apply(block);
            if (t != null)
            {
                return DataResult.success(new BlockConfig<>(t));
            }
            return DataResult.error(onError + ": " + block.getRegistryName());
        }, BlockConfig::block);
    }
}
