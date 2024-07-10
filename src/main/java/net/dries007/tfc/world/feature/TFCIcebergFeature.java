/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.IcebergFeature;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

/**
 * This class has two modifications implemented in {@link IcebergFeature} via mixin, conditional to checking {@link #is(Object)}
 * for the given feature. It is an entire copy of the vanilla feature, except replacing water with salt water.
 */
public class TFCIcebergFeature extends IcebergFeature
{
    public static boolean is(Object feature)
    {
        return feature.getClass() == TFCIcebergFeature.class;
    }

    public TFCIcebergFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }
}
