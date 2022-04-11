/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public record FissureConfig(Optional<BlockState> wallState, BlockState fluidState, int count, int radius, VerticalAnchor minDepth, int minPieces, int maxPieces, int maxPieceLength, Optional<Decoration> decoration) implements FeatureConfiguration
{
    public static final Codec<FissureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.BLOCK_STATE.optionalFieldOf("wall_state").forGetter(c -> c.wallState),
        Codecs.BLOCK_STATE.fieldOf("fluid_state").forGetter(c -> c.fluidState),
        Codecs.POSITIVE_INT.optionalFieldOf("count", 5).forGetter(c -> c.count),
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 12).forGetter(c -> c.radius),
        VerticalAnchor.CODEC.optionalFieldOf("min_depth", VerticalAnchor.aboveBottom(16)).forGetter(c -> c.minDepth),
        Codecs.POSITIVE_INT.optionalFieldOf("min_pieces", 10).forGetter(c -> c.minPieces),
        Codecs.POSITIVE_INT.optionalFieldOf("max_pieces", 24).forGetter(c -> c.maxPieces),
        Codecs.POSITIVE_INT.optionalFieldOf("max_piece_length", 6).forGetter(c -> c.maxPieceLength),
        Decoration.CODEC.optionalFieldOf("decoration").forGetter(c -> c.decoration)
    ).apply(instance, FissureConfig::new));

    public record Decoration(Map<Block, IWeighted<BlockState>> states, int rarity, int radius, int count)
    {
        public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.BLOCK_TO_WEIGHTED_BLOCKSTATE.fieldOf("blocks").forGetter(c -> c.states),
            Codecs.POSITIVE_INT.fieldOf("rarity").forGetter(c -> c.rarity),
            Codecs.POSITIVE_INT.fieldOf("radius").forGetter(c -> c.radius),
            Codecs.POSITIVE_INT.fieldOf("count").forGetter(c -> c.count)
        ).apply(instance, Decoration::new));

        @Nullable
        public BlockState getState(BlockState stoneState, Random random)
        {
            final IWeighted<BlockState> weighted = states.get(stoneState.getBlock());
            if (weighted != null)
            {
                return weighted.get(random);
            }
            return null;
        }
    }
}
