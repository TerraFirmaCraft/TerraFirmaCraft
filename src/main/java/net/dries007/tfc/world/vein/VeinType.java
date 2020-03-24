package net.dries007.tfc.world.vein;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.util.collections.IWeighted;

public class VeinType
{
    protected final IWeighted<Indicator> indicator;
    protected final int verticalSize;
    protected final int horizontalSize;
    protected final float density;
    protected final int rarity;
    protected final int minY;
    protected final int maxY;
    private final ResourceLocation group;
    private final double groupWeight;
    protected Map<BlockState, IWeighted<BlockState>> blocks;

    public VeinType(@Nullable ResourceLocation group, int groupWeight, Map<BlockState, IWeighted<BlockState>> blocks, IWeighted<Indicator> indicator, int rarity, int minY, int maxY, int verticalSize, int horizontalSize, int density)
    {
        this.group = group;
        this.groupWeight = groupWeight;
        this.blocks = blocks;
        this.indicator = indicator;
        this.rarity = rarity;
        this.minY = minY;
        this.maxY = maxY;
        this.verticalSize = verticalSize;
        this.horizontalSize = horizontalSize;
        this.density = density;
    }

    @Nullable
    public ResourceLocation getGroup()
    {
        return group;
    }

    public double getGroupWeight()
    {
        return groupWeight;
    }

    public static class Indicator
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

        @Nonnull
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
}
