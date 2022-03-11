/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;
import net.minecraft.world.level.levelgen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock.STAGE;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class BananaFeature extends Feature<BlockStateConfiguration>
{
    public BananaFeature(Codec<BlockStateConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockStateConfiguration> context)
    {
        final WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        final Random random = context.random();
        final BlockStateConfiguration config = context.config();

        BlockState banana = config.state;

        pos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, pos);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < 15; i++)
        {
            mutablePos.setWithOffset(pos, random.nextInt(10) - random.nextInt(10), -1, random.nextInt(10) - random.nextInt(10));
            if (Helpers.isBlock(world.getBlockState(mutablePos), TFCTags.Blocks.BUSH_PLANTABLE_ON))
            {
                boolean blocked = false;
                for (int j = 1; j <= 10; j++)
                {
                    mutablePos.move(Direction.UP);
                    if (!world.isEmptyBlock(mutablePos))
                    {
                        blocked = true;
                        break;
                    }
                }
                if (!blocked)
                {
                    mutablePos.move(Direction.DOWN, 10);
                    for (int stage = 0; stage <= 2; stage++)
                    {
                        for (int k = 1; k < random.nextInt(3) + 1; k++)
                        {
                            mutablePos.move(Direction.UP);
                            world.setBlock(mutablePos, banana.setValue(STAGE, 0), 3);
                            if (stage == 2) return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
