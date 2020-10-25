package net.dries007.tfc.world.feature.vein;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.registry.Registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.collections.IWeighted;
import net.dries007.tfc.world.Codecs;

public class Indicator
{
    @SuppressWarnings("deprecation")
    public static final Codec<Indicator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.intRange(0, 256).optionalFieldOf("depth", 35).forGetter(c -> c.depth),
        Codec.intRange(0, 15).optionalFieldOf("spread", 15).forGetter(c -> c.spread),
        Codecs.POSITIVE_INT.optionalFieldOf("rarity", 10).forGetter(c -> c.rarity),
        Codecs.POSITIVE_INT.optionalFieldOf("count", 3).forGetter(c -> c.count),
        Codecs.weightedCodec(BlockState.CODEC, "state").fieldOf("states").forGetter(c -> c.states),
        Codecs.setCodec(Registry.BLOCK).fieldOf("under_states").forGetter(c -> c.underStates)
    ).apply(instance, Indicator::new));

    private final int depth;
    private final int spread;
    private final int rarity;
    private final int count;

    private final IWeighted<BlockState> states;
    private final Set<Block> underStates;

    public Indicator(int depth, int spread, int rarity, int count, IWeighted<BlockState> states, Set<Block> underStates)
    {
        this.depth = depth;
        this.spread = spread;
        this.rarity = rarity;
        this.count = count;
        this.states = states;
        this.underStates = underStates;
    }
}
