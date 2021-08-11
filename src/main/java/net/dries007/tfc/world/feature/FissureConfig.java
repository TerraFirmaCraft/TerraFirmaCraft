/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class FissureConfig implements FeatureConfiguration
{
    public static final Codec<FissureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.optionalFieldOf("wall_state").forGetter(c -> c.wallState),
        Codecs.LENIENT_BLOCKSTATE.fieldOf("fluid_state").forGetter(c -> c.fluidState),
        Codecs.POSITIVE_INT.optionalFieldOf("count", 5).forGetter(c -> c.count),
        Codecs.POSITIVE_INT.optionalFieldOf("radius", 12).forGetter(c -> c.radius),
        Codec.intRange(0, 256).optionalFieldOf("min_depth", 16).forGetter(c -> c.minDepth),
        Codecs.POSITIVE_INT.optionalFieldOf("min_pieces", 10).forGetter(c -> c.minPieces),
        Codecs.POSITIVE_INT.optionalFieldOf("max_pieces", 24).forGetter(c -> c.maxPieces),
        Codecs.POSITIVE_INT.optionalFieldOf("max_piece_length", 6).forGetter(c -> c.maxPieceLength),
        Decoration.CODEC.optionalFieldOf("decoration").forGetter(c -> c.decoration)
    ).apply(instance, FissureConfig::new));

    public final Optional<BlockState> wallState;
    public final BlockState fluidState;
    public final int count;
    public final int radius;
    public final int minDepth;
    public final int minPieces;
    public final int maxPieces;
    public final int maxPieceLength;
    public final Optional<Decoration> decoration;

    public FissureConfig(Optional<BlockState> wallState, BlockState fluidState, int count, int radius, int minDepth, int minPieces, int maxPieces, int maxPieceLength, Optional<Decoration> decoration)
    {
        this.wallState = wallState;
        this.fluidState = fluidState;
        this.count = count;
        this.radius = radius;
        this.minDepth = minDepth;
        this.minPieces = minPieces;
        this.maxPieces = maxPieces;
        this.maxPieceLength = maxPieceLength;
        this.decoration = decoration;
    }

    public static class Decoration
    {
        @SuppressWarnings("deprecation")
        public static final Codec<Decoration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codecs.mapKeyListCodec(Codec.mapPair(
                Registry.BLOCK.listOf().fieldOf("stone"),
                Codecs.weightedCodec(Codecs.LENIENT_BLOCKSTATE, "block").fieldOf("ore")
            ).codec()).fieldOf("blocks").forGetter(c -> c.states),
            Codecs.POSITIVE_INT.fieldOf("rarity").forGetter(c -> c.rarity),
            Codecs.POSITIVE_INT.fieldOf("radius").forGetter(c -> c.radius),
            Codecs.POSITIVE_INT.fieldOf("count").forGetter(c -> c.count)
        ).apply(instance, Decoration::new));
        public final int rarity;
        public final int radius;
        public final int count;
        private final Map<Block, IWeighted<BlockState>> states;

        public Decoration(Map<Block, IWeighted<BlockState>> states, int rarity, int radius, int count)
        {
            this.states = states;
            this.rarity = rarity;
            this.radius = radius;
            this.count = count;
        }

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
