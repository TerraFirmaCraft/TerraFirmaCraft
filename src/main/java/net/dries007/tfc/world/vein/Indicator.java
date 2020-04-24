package net.dries007.tfc.world.vein;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.BlockState;

import net.dries007.tfc.util.collections.IWeighted;

public class Indicator
{
    private final int maxDepth;
    private final int rarity;
    private final boolean ignoreLiquids;

    private final IWeighted<BlockState> states;
    private final Set<BlockState> underStates;

    public Indicator(int maxDepth, int rarity, boolean ignoreLiquids, IWeighted<BlockState> states, List<BlockState> underStates)
    {
        this.maxDepth = maxDepth;
        this.rarity = rarity;
        this.ignoreLiquids = ignoreLiquids;
        this.states = states;
        this.underStates = new HashSet<>(underStates);
    }

    public BlockState getStateToGenerate(Random random)
    {
        return states.get(random);
    }

    public boolean validUnderState(BlockState state)
    {
        return underStates.isEmpty() || underStates.contains(state);
    }

    public int getMaxDepth()
    {
        return maxDepth;
    }

    public int getRarity()
    {
        return rarity;
    }

    public boolean shouldIgnoreLiquids()
    {
        return ignoreLiquids;
    }
}
