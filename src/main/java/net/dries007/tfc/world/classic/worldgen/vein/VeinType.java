/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;

@ParametersAreNonnullByDefault
public class VeinType
{
    public final Ore ore;
    public final Shape shape;
    public final Size size;
    public final Set<Rock> baseRocks;
    public final int minY;
    public final int maxY;
    public final double weight;
    public final double density;
    private final int rarity;
    private String name;

    public VeinType(@Nullable Ore ore, Size size, Shape shape, Collection<Rock> baseRocks, int rarity, int minY, int maxY, int density)
    {
        this.ore = ore;
        this.size = size;
        this.shape = shape;
        this.baseRocks = ImmutableSet.copyOf(baseRocks);

        this.rarity = rarity;
        this.weight = 1.0D / (double) rarity;
        this.minY = minY;
        this.maxY = maxY;
        this.density = 0.01D * (double) density; // For debug purposes, removing the 0.01D will lead to ore veins being full size, easy to see shapes
    }

    /**
     * Intended to be used by propick via {@code VeinRegistry.INSTANCE.getVeins().values().forEach(x -> x.ore.isOreBlock(state)}
     * todo: override for non-vein based block states
     *
     * @return true if the given block state is part of this ore vein
     */
    public boolean isOreBlock(IBlockState state)
    {
        return state.getBlock() instanceof BlockOreTFC && ((BlockOreTFC) state.getBlock()).ore == this.ore;
    }

    public IBlockState getOreState(Rock rock, Ore.Grade grade)
    {
        return BlockOreTFC.get(this.ore, rock, grade);
    }

    public boolean hasLooseRocks()
    {
        return ore != null && ore.isGraded();
    }

    public ItemStack getLooseRockItem()
    {
        return ore != null ? ItemOreTFC.get(ore, 1) : ItemStack.EMPTY;
    }

    public void setRegistryName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return String.format("%s: {ore=%s, shape=%s, size=%s, rarity=%d, baseRocks=%s, minY=%d, maxY=%d, density=%.2f}", name, (ore != null ? ore : "special"), shape, size, rarity, baseRocks, minY, maxY, density);
    }

    public enum Shape
    {
        SCATTERED_CLUSTER(2, 5), // This is the default. It creates scattered spheriods
        SINGLE_CLUSTER(1, 1); // This is to create a single spheriod

        public final int minClusters;
        public final int maxClusters;

        Shape(int minClusters, int maxClusters)
        {
            this.minClusters = minClusters;
            this.maxClusters = maxClusters;
        }
    }

    public enum Size
    {
        SMALL(8.0F, 0.7F),
        MEDIUM(12.0F, 0.6F),
        LARGE(16.0F, 0.5F);

        public final float radius;
        public final float densityModifier;

        Size(float radius, float densityModifier)
        {
            this.radius = radius;
            this.densityModifier = densityModifier;
        }
    }

    public static class Special extends VeinType
    {
        private final IBlockState oreState;

        public Special(IBlockState oreState, Size size, Shape shape, Set<Rock> blocks, int rarity, int minY, int maxY, int density)
        {
            super(null, size, shape, blocks, rarity, minY, maxY, density);
            this.oreState = oreState;
        }

        @Override
        public boolean isOreBlock(IBlockState state)
        {
            return state == this.oreState;
        }

        @Override
        public IBlockState getOreState(Rock rock, Ore.Grade grade)
        {
            return this.oreState;
        }
    }
}
