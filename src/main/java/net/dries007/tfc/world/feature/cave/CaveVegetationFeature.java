/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.cave;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class CaveVegetationFeature extends Feature<CaveVegetationConfig>
{
    public CaveVegetationFeature(Codec<CaveVegetationConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CaveVegetationConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final Random random = context.random();
        final CaveVegetationConfig config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < 128; i++)
        {
            if (random.nextFloat() < 0.8f)//mossy cobble
            {
                mutablePos.setWithOffset(pos, random.nextInt(15) - random.nextInt(15), -1 * random.nextInt(2) - 1, random.nextInt(15) - random.nextInt(15));
                if (level.isEmptyBlock(mutablePos))
                {
                    for (int j = 0; j < 7; j++)
                    {
                        mutablePos.move(0, -1, 0);
                        if (!level.isEmptyBlock(mutablePos))
                        {
                            break;
                        }
                    }
                    BlockState generateState = config.getStateToGenerate(level.getBlockState(mutablePos), random);
                    if (generateState != null)
                    {
                        setBlock(level, mutablePos, generateState);
                    }
                }
            }
            if (random.nextFloat() < 0.003f)//extra springs
            {
                mutablePos.setWithOffset(pos, random.nextInt(15) - random.nextInt(15), 4 + random.nextInt(7), random.nextInt(15) - random.nextInt(15));
                if (level.isEmptyBlock(mutablePos))
                {
                    mutablePos.move(0, 1, 0);
                    if (isStone(level, mutablePos))
                    {
                        setBlock(level, mutablePos, Fluids.WATER.defaultFluidState().createLegacyBlock());
                        level.scheduleTick(mutablePos, Fluids.WATER, 0);
                    }
                }
            }
            if (random.nextFloat() < 0.04f)//cobwebs and roots
            {
                mutablePos.setWithOffset(pos, random.nextInt(15) - random.nextInt(15), 4 + random.nextInt(7), random.nextInt(15) - random.nextInt(15));
                if (isStone(level, pos))
                {
                    mutablePos.move(0, -1, 0);
                    if (level.isEmptyBlock(mutablePos))
                    {
                        final BlockState state = random.nextBoolean() ? Blocks.COBWEB.defaultBlockState() : Blocks.HANGING_ROOTS.defaultBlockState();
                        setBlock(level, mutablePos, state);
                    }
                }
            }
        }
        return true;
    }

    private static boolean isStone(WorldGenLevel level, BlockPos pos)
    {
        return Helpers.isBlock(level.getBlockState(pos), BlockTags.BASE_STONE_OVERWORLD);
    }
}
