/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class TFCVinesFeature extends Feature<VineConfig>
{
    private static final Direction[] DIRECTIONS = Direction.values();

    public TFCVinesFeature(Codec<VineConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VineConfig> context)
    {
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final Random rand = context.random();
        final VineConfig config = context.config();

        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        BlockState state = config.state();
        List<Direction> dirs = new ArrayList<>(4);
        int r = config.radius();

        for (int j = 0; j < config.tries(); j++)
        {
            for (int y = config.minHeight(); y < config.maxHeight(); ++y)
            {
                mutablePos.set(pos);
                mutablePos.move(rand.nextInt(r) - rand.nextInt(r), 0, rand.nextInt(r) - rand.nextInt(r));
                mutablePos.setY(y);
                if (world.isEmptyBlock(mutablePos))
                {
                    for (Direction direction : DIRECTIONS)
                    {
                        mutablePos.move(direction);
                        BlockState foundState = world.getBlockState(mutablePos);
                        if (direction != Direction.DOWN && (Helpers.isBlock(foundState, TFCTags.Blocks.CREEPING_PLANTABLE_ON) || Helpers.isBlock(foundState, BlockTags.LOGS) || Helpers.isBlock(foundState, BlockTags.LEAVES)))
                        {
                            mutablePos.move(direction.getOpposite());
                            world.setBlock(mutablePos, state.setValue(VineBlock.getPropertyForFace(direction), true), 2);
                            if (direction != Direction.UP)
                                dirs.add(direction);
                            break;
                        }
                        mutablePos.move(direction.getOpposite());
                    }
                    if (!dirs.isEmpty())
                    {
                        for (int k = 0; k < 6 + rand.nextInt(13); k++)
                        {
                            mutablePos.move(Direction.DOWN);
                            if (world.isEmptyBlock(mutablePos))
                            {
                                for (Direction direction : dirs)
                                {
                                    world.setBlock(mutablePos, state.setValue(VineBlock.getPropertyForFace(direction), true), 2);
                                }
                            }
                        }
                        dirs.clear();
                    }
                }
            }
        }


        return true;
    }
}
