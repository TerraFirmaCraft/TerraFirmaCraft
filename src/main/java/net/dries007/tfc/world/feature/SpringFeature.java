/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.dries007.tfc.world.settings.RockLayerSettings;

/**
 * A cleaned up version of {@link net.minecraft.world.level.levelgen.feature.SpringFeature}
 */
public class SpringFeature extends Feature<SpringConfiguration>
{
    public SpringFeature(Codec<SpringConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpringConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final SpringConfiguration config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos).move(0, 1, 0);
        final BlockState stateAbove = level.getBlockState(mutablePos);

        final ChunkGeneratorExtension extension = (ChunkGeneratorExtension) context.chunkGenerator();
        final RockLayerSettings rockSettings = extension.rockLayerSettings();

        if (config.validBlocks.contains(getHolder(stateAbove)))
        {
            mutablePos.move(0, -2, 0);
            final BlockState stateBelow = level.getBlockState(mutablePos);
            if (!config.requiresBlockBelow || config.validBlocks.contains(getHolder(stateBelow)))
            {
                final BlockState stateAt = level.getBlockState(pos);
                if (stateAt.isAir() || config.validBlocks.contains(getHolder(stateAt)))
                {
                    int rockCount = 0, holeCount = 0;
                    for (Direction direction : Helpers.DIRECTIONS)
                    {
                        mutablePos.set(pos).move(direction);
                        final BlockState stateAdjacent = level.getBlockState(mutablePos);
                        if (config.validBlocks.contains(getHolder(stateAdjacent)))
                        {
                            rockCount++;
                        }
                        if (stateAdjacent.isAir())
                        {
                            holeCount++;
                        }
                    }

                    if (rockCount == config.rockCount && holeCount == config.holeCount)
                    {
                        level.setBlock(pos, config.state.createLegacyBlock(), 2);
                        level.scheduleTick(pos, config.state.getType(), 0);

                        final BlockPos posAbove = pos.above();
                        final Block hardened = rockSettings.getHardened(level.getBlockState(posAbove).getBlock());
                        if (hardened != null)
                        {
                            level.setBlock(posAbove, hardened.defaultBlockState(), 2);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private static Holder<Block> getHolder(BlockState state)
    {
        return state.getBlock().builtInRegistryHolder();
    }
}
