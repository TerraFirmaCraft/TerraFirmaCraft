/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

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

import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.blocks.soil.IGrassBlock;

public class TFCBambooFeature extends Feature<TFCBambooConfig>
{
    public TFCBambooFeature(Codec<TFCBambooConfig> codec)
    {
        super(codec);
    }

    private BlockState trunk(TFCBambooConfig config)
    {
        return config.state().setValue(BambooStalkBlock.AGE, 1).setValue(BambooStalkBlock.LEAVES, BambooLeaves.NONE).setValue(BambooStalkBlock.STAGE, 0);
    }

    private BlockState finalLarge(TFCBambooConfig config)
    {
        return trunk(config).setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE).setValue(BambooStalkBlock.STAGE, 1);
    }

    private BlockState topLarge(TFCBambooConfig config)
    {
        return trunk(config).setValue(BambooStalkBlock.LEAVES, BambooLeaves.LARGE);
    }

    private BlockState topSmall(TFCBambooConfig config)
    {
        return trunk(config).setValue(BambooStalkBlock.LEAVES, BambooLeaves.SMALL);
    }

    @Override
    public boolean place(FeaturePlaceContext<TFCBambooConfig> context)
    {
        int placed = 0;
        final BlockPos blockpos = context.origin();
        final WorldGenLevel level = context.level();
        final var random = context.random();
        final TFCBambooConfig config = context.config();
        final BlockPos.MutableBlockPos cursor = blockpos.mutable();
        final BlockPos.MutableBlockPos cursor2 = blockpos.mutable();

        final BlockState trunk = trunk(config);
        final BlockState finalLarge = finalLarge(config);
        final BlockState topLarge = topLarge(config);
        final BlockState topSmall = topSmall(config);

        if (level.isEmptyBlock(cursor))
        {
            if (Blocks.BAMBOO.defaultBlockState().canSurvive(level, cursor))
            {
                final int trunkSize = random.nextInt(12) + 5;
                if (random.nextFloat() < config.probability())
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
                    level.setBlock(cursor, trunk, 2);
                    cursor.move(Direction.UP, 1);
                }

                if (cursor.getY() - blockpos.getY() >= 3)
                {
                    level.setBlock(cursor, finalLarge, 2);
                    level.setBlock(cursor.move(Direction.DOWN, 1), topLarge, 2);
                    level.setBlock(cursor.move(Direction.DOWN, 1), topSmall, 2);
                }
            }

            ++placed;
        }

        return placed > 0;
    }
}
