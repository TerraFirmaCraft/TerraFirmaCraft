/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

public class KaolinDiscVeinFeature extends DiscVeinFeature
{
    public KaolinDiscVeinFeature(Codec<DiscVeinConfig> codec)
    {
        super(codec);
    }

    @Override
    protected @Nullable BlockState getStateToGenerate(BlockState stoneState, RandomSource random, DiscVeinConfig config, int x, int y, int z)
    {
        if (Helpers.isBlock(stoneState, TFCTags.Blocks.GRASS))
        {
            return TFCBlocks.KAOLIN_CLAY_GRASS.get().defaultBlockState();
        }
        else if (Helpers.isBlock(stoneState, TFCTags.Blocks.KAOLIN_CLAY_REPLACEABLE))
        {
            if (y > -2)
            {
                return TFCBlocks.RED_KAOLIN_CLAY.get().defaultBlockState();
            }
            if (y > -4)
            {
                return TFCBlocks.PINK_KAOLIN_CLAY.get().defaultBlockState();
            }
            return TFCBlocks.WHITE_KAOLIN_CLAY.get().defaultBlockState();
        }
        return null;
    }

    @Override
    protected float getChanceToGenerate(int x, int y, int z, Vein vein, DiscVeinConfig config)
    {
        final float sample = (float) vein.metaballs().sample(x, z);
        if (Math.abs(y) <= config.height() && sample > 1f)
        {
            return config.config().density() * Mth.clampedMap(sample, 2f, 1f, 1f, 0.6f);
        }
        return 0;
    }
}
