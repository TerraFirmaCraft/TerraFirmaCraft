/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;

public class CaveColumnFeature extends Feature<NoneFeatureConfiguration>
{
    public CaveColumnFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();

        final var random = context.random();

        final RockData data = ChunkData.get(context.level(), pos).getRockData();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos);
        final float amp = Mth.nextFloat(random, 0.25f, 0.6f);
        final float offset = Mth.nextInt(random, 1, 5);
        final float period = Mth.nextFloat(random, 0.4f, 0.6f);
        final int xo = pos.getX();
        final int zo = pos.getZ();

        int y = mutablePos.getY();

        mutablePos.move(0, -1, 0);
        if (!Helpers.isBlock(level.getBlockState(mutablePos), BlockTags.BASE_STONE_OVERWORLD))
        {
            return false;
        }
        else
        {
            mutablePos.move(0, 1, 0);
        }

        while (true)
        {
            final BlockState state = data.getRock(mutablePos).hardened().defaultBlockState();
            final int radius = (int) (amp * Mth.sin(period * y + offset) + 2);
            for (int x = -radius; x <= radius; x++)
            {
                for (int z = -radius; z <= radius; z++)
                {
                    if (x * x * z * z < radius * radius)
                    {
                        mutablePos.set(xo + x, y, zo + z);
                        setBlock(level, mutablePos, state);
                    }
                }
            }
            y++;
            mutablePos.set(xo, y, zo);
            BlockState middleState = level.getBlockState(mutablePos);
            if (!Helpers.isBlock(middleState, Blocks.CAVE_AIR) && !Helpers.isBlock(middleState, Blocks.LAVA))
            {
                return true;
            }
        }
    }
}
