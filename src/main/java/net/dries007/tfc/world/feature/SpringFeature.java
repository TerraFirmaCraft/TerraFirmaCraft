/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.SpringConfiguration;

import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import net.dries007.tfc.util.Helpers;

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
        final WorldGenLevel world = context.level();
        final BlockPos pos = context.origin();
        final SpringConfiguration config = context.config();

        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos().set(pos).move(0, 1, 0);
        final BlockState stateAbove = world.getBlockState(mutablePos);
        if (config.validBlocks.contains(getHolder(stateAbove)))
        {
            mutablePos.move(0, -2, 0);
            final BlockState stateBelow = world.getBlockState(mutablePos);
            if (!config.requiresBlockBelow || config.validBlocks.contains(getHolder(stateBelow)))
            {
                final BlockState stateAt = world.getBlockState(pos);
                if (stateAt.isAir() || config.validBlocks.contains(getHolder(stateAt)))
                {
                    int rockCount = 0, holeCount = 0;
                    for (Direction direction : Helpers.DIRECTIONS)
                    {
                        mutablePos.set(pos).move(direction);
                        final BlockState stateAdjacent = world.getBlockState(mutablePos);
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
                        world.setBlock(pos, config.state.createLegacyBlock(), 2);
                        world.scheduleTick(pos, config.state.getType(), 0);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Holder<Block> getHolder(BlockState state)
    {
        return Helpers.getHolder(ForgeRegistries.BLOCKS, state.getBlock());
    }
}
