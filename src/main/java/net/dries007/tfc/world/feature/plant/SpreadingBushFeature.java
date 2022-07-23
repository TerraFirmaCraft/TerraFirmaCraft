/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.plant.KelpTreeFlowerBlock;
import net.dries007.tfc.common.blocks.plant.fruit.SpreadingBushBlock;
import net.dries007.tfc.common.blocks.plant.fruit.SpreadingCaneBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.feature.BlockConfig;

public class SpreadingBushFeature extends Feature<BlockConfig<SpreadingBushBlock>>
{
    public static final Codec<BlockConfig<SpreadingBushBlock>> CODEC = BlockConfig.codec(b -> b instanceof SpreadingBushBlock t ? t : null, "Must be a " + KelpTreeFlowerBlock.class.getSimpleName());

    public SpreadingBushFeature(Codec<BlockConfig<SpreadingBushBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<SpreadingBushBlock>> context)
    {
        final WorldGenLevel level = context.level();
        final Random random = context.random();
        final BlockPos pos = context.origin();
        final SpreadingBushBlock block = context.config().block();

        boolean any = false;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int tries = 0; tries < 5; tries++)
        {
            cursor.setWithOffset(pos,
                random.nextInt(3) - random.nextInt(3),
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(3) - random.nextInt(3));

            final BlockState stateToPlace = block.defaultBlockState();
            if (block.canSurvive(stateToPlace, level, cursor) && level.getBlockState(cursor.below()).getBlock() != block)
            {
                final int height = 1 + random.nextInt(3);
                for (int i = 0; i < height; i++)
                {
                    // Set a bush at this position, and optionally a cane
                    if (EnvironmentHelpers.isWorldgenReplaceable(level, cursor))
                    {
                        any = true;
                        final int stage = height - i - 1;
                        level.setBlock(cursor, stateToPlace.setValue(SpreadingBushBlock.STAGE, stage), 2);

                        final Direction offset = Direction.Plane.HORIZONTAL.getRandomDirection(random);
                        cursor.move(offset);
                        if (stage > 0 && EnvironmentHelpers.isWorldgenReplaceable(level, cursor))
                        {
                            level.setBlock(cursor, block.getCane().defaultBlockState().setValue(SpreadingCaneBlock.FACING, offset), 2);
                        }
                        cursor.move(offset.getOpposite());
                    }
                    else
                    {
                        break;
                    }
                    cursor.move(Direction.UP);
                }
            }
        }
        return any;
    }
}
