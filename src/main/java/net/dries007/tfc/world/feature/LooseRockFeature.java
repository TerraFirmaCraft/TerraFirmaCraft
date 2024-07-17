/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.settings.RockSettings;

/**
 * Places a single loose rock at the target position
 */
public class LooseRockFeature extends Feature<NoneFeatureConfiguration>
{
    public LooseRockFeature(Codec<NoneFeatureConfiguration> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final var random = context.random();

        final ChunkData data = ChunkData.get(level, pos);
        final RockSettings rock = data.getRockData().getRock(pos);

        final @Nullable Block looseRock = rock.loose().orElse(null);
        final @Nullable Block mossyLooseRock = rock.mossyLoose().orElse(null);

        if (looseRock == null)
        {
            return false;
        }

        final BlockState stateAt = level.getBlockState(pos);
        BlockState rockState;

        if (mossyLooseRock != null && data.getRainfall(pos) > 250f && random.nextBoolean() && stateAt.getFluidState().isEmpty())
        {
            rockState = FluidHelpers.fillWithFluid(mossyLooseRock.defaultBlockState(), stateAt.getFluidState().getType());
        }
        else
        {
            rockState = FluidHelpers.fillWithFluid(looseRock.defaultBlockState(), stateAt.getFluidState().getType());
        }

        if (rockState != null && EnvironmentHelpers.isWorldgenReplaceable(stateAt) && rockState.canSurvive(level, pos) && canGenerateOn(level.getBlockState(pos.below())))
        {
            setBlock(level, pos, rockState.setValue(TFCBlockStateProperties.COUNT_1_3, 1 + random.nextInt(2)));
            return true;
        }
        return false;
    }

    private boolean canGenerateOn(BlockState state)
    {
        return !Helpers.isBlock(state, BlockTags.ICE);
    }
}
