/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.vein;

import com.mojang.serialization.Codec;
import net.minecraft.tags.BlockTags;
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
        else if (Helpers.isBlock(stoneState, BlockTags.DIRT))
        {
            if (y > config.height() / 3f)
            {
                return TFCBlocks.RED_KAOLIN_CLAY.get().defaultBlockState();
            }
            if (y < -config.height() / 3f)
            {
                return TFCBlocks.PINK_KAOLIN_CLAY.get().defaultBlockState();
            }
            return TFCBlocks.WHITE_KAOLIN_CLAY.get().defaultBlockState();
        }
        return null;
    }


}
