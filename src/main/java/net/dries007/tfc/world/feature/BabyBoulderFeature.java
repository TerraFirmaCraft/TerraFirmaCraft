/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.function.Supplier;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;

public class BabyBoulderFeature extends BouldersFeature
{
    public BabyBoulderFeature(Codec<BoulderConfig> codec)
    {
        super(codec);
    }

    @Override
    protected void place(WorldGenLevel level, BlockPos pos, Supplier<BlockState> state, RandomSource random)
    {
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        if (random.nextFloat() < 0.5f)
        {
            // small sphere-like boulder
            setBlock(level, pos, state.get());
            for (Direction dir : Helpers.DIRECTIONS)
            {
                if (random.nextFloat() < 0.8f)
                {
                    cursor.setWithOffset(pos, dir);
                    setBlock(level, cursor, state.get());
                }
            }
        }
        else if (random.nextFloat() < 0.5f)
        {
            // short column of rock with an optional second column next to it
            cursor.setWithOffset(pos, 0, -2, 0);
            final int height = 4 + random.nextInt(3);
            for (int i = 0; i < height; i++)
            {
                setBlock(level, cursor, state.get());
                cursor.move(0, 1, 0);
            }
            cursor.setWithOffset(pos, 0, -2, 0);
            cursor.move(Direction.Plane.HORIZONTAL.getRandomDirection(random));
            for (int i = 0; i < height - 2; i++)
            {
                setBlock(level, cursor, state.get());
                cursor.move(0, 1, 0);
            }
        }
        else
        {
            // a single block with a single block underneath
            cursor.set(pos);
            setBlock(level, cursor, state.get());
            cursor.move(0, -1, 0);
            setBlock(level, cursor, state.get());
        }
    }
}
