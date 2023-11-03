/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;

public class TFCBambooFeature extends Feature<ProbabilityFeatureConfiguration>
{
    private static final BlockState BAMBOO_TRUNK = Blocks.BAMBOO.defaultBlockState().setValue(BambooStalkBlock.AGE, 1).setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE).setValue(BambooStalkBlock.STAGE, 0);
    private static final BlockState BAMBOO_FINAL_LARGE = BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE).setValue(BambooStalkBlock.STAGE, 1);
    private static final BlockState BAMBOO_TOP_LARGE = BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE);
    private static final BlockState BAMBOO_TOP_SMALL = BAMBOO_TRUNK.setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL);

    public TFCBambooFeature(Codec<ProbabilityFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> context)
    {
        int placed = 0;
        final BlockPos blockpos = context.origin();
        final WorldGenLevel level = context.level();
        final var random = context.random();
        final ProbabilityFeatureConfiguration config = context.config();
        final BlockPos.MutableBlockPos cursor = blockpos.mutable();
        final BlockPos.MutableBlockPos cursor2 = blockpos.mutable();
        if (level.isEmptyBlock(cursor))
        {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive(level, cursor))
            {
                final int trunkSize = random.nextInt(12) + 5;
                if (random.nextFloat() < config.probability)
                {
                    final int radius = random.nextInt(4) + 1;

                    for (int x = blockpos.getX() - radius; x <= blockpos.getX() + radius; ++x)
                    {
                        for (int z = blockpos.getZ() - radius; z <= blockpos.getZ() + radius; ++z)
                        {
                            int dx = x - blockpos.getX();
                            int dz = z - blockpos.getZ();
                            if (dx * dx + dz * dz <= radius * radius)
                            {
                                cursor2.set(x, level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1, z);
                                Block under = level.getBlockState(cursor2).getBlock();
                                if (under instanceof IGrassBlock grass && grass.getDirt().getBlock() instanceof DirtBlock dirt)
                                {
                                    level.setBlock(cursor2, dirt.getRooted(), 2);
                                }
                                else if (under instanceof DirtBlock dirt)
                                {
                                    level.setBlock(cursor2, dirt.getRooted(), 2);
                                }
                            }
                        }
                    }
                }

                for (int j = 0; j < trunkSize && level.isEmptyBlock(cursor); ++j)
                {
                    level.setBlock(cursor, BAMBOO_TRUNK, 2);
                    cursor.move(Direction.UP, 1);
                }

                if (cursor.getY() - blockpos.getY() >= 3)
                {
                    level.setBlock(cursor, BAMBOO_FINAL_LARGE, 2);
                    level.setBlock(cursor.move(Direction.DOWN, 1), BAMBOO_TOP_LARGE, 2);
                    level.setBlock(cursor.move(Direction.DOWN, 1), BAMBOO_TOP_SMALL, 2);
                }
            }

            ++placed;
        }

        return placed > 0;
    }
}
