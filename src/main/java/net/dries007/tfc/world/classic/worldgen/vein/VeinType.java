/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.worldgen.vein;

import java.util.Collection;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;

@ParametersAreNonnullByDefault
public class VeinType
{
    private final Ore ore;
    private final ItemStack looseRock;
    private final Set<Rock> baseRocks;

    private final int width;
    private final int height;
    private final int minY;
    private final int maxY;

    private final double weight;
    private final double density;

    private final int rarity;
    private final Shape shape;
    private String name;

    public VeinType(@Nullable Ore ore, ItemStack looseRock, Collection<Rock> baseRocks, Shape shape, int width, int height, int rarity, int minY, int maxY, int density)
    {
        this.ore = ore;
        this.looseRock = looseRock;
        this.baseRocks = ImmutableSet.copyOf(baseRocks);
        this.shape = shape;

        this.width = width;
        this.height = height;
        this.rarity = rarity;
        this.weight = 1.0D / (double) rarity;
        this.minY = minY;
        this.maxY = maxY;
        this.density = 0.006 * density; // For debug purposes, removing the factor here will lead to ore veins being full size, easy to see shapes
    }

    /**
     * Creates a new instance of a vein at a given chunk. Used during world gen for various purposes.
     */
    public Vein createVein(Random rand, int chunkX, int chunkZ)
    {
        BlockPos startPos = new BlockPos(chunkX * 16 + 8 + rand.nextInt(16), minY + rand.nextInt(maxY - minY), chunkZ * 16 + 8 + rand.nextInt(16));
        Ore.Grade grade = Ore.Grade.NORMAL;
        if (ore != null && ore.isGraded())
        {
            float randomGrade = rand.nextFloat();
            if (randomGrade < 0.2)
            {
                grade = Ore.Grade.RICH;
            }
            else if (randomGrade < 0.5)
            {
                grade = Ore.Grade.POOR;
            }
        }
        switch (shape)
        {
            case SPHERE:
                return new VeinSphere(startPos, this, grade, rand);
            case CLUSTER:
                return new VeinCluster(startPos, this, grade, rand);
        }
        throw new IllegalStateException("Shape is missing!");
    }

    /**
     * Intended to be used by propick via {@code VeinRegistry.INSTANCE.getVeins().values().forEach(x -> x.ore.isOreBlock(state)}
     *
     * @return true if the given block state is part of this ore vein
     */
    public boolean isOreBlock(IBlockState state)
    {
        return state.getBlock() instanceof BlockOreTFC && ((BlockOreTFC) state.getBlock()).ore == this.getOre();
    }

    /**
     * Get the ore state for placement at a specific rock position
     */
    public IBlockState getOreState(Rock rock, Ore.Grade grade)
    {
        return BlockOreTFC.get(ore, rock, grade);
    }

    public boolean hasLooseRocks()
    {
        return !looseRock.isEmpty();
    }

    @Nonnull
    public ItemStack getLooseRockItem()
    {
        return looseRock.copy();
    }

    public String getRegistryName()
    {
        return name;
    }

    public void setRegistryName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        String ore = "special";
        if (getOre() != null)
        {
            ore = getOre().toString();
        }
        else if (getOreState(Rock.GRANITE, Ore.Grade.NORMAL) != null)
        {
            // print the registry name
            //noinspection ConstantConditions
            ore = getOreState(Rock.GRANITE, Ore.Grade.NORMAL).getBlock().getRegistryName().toString();
        }
        return String.format("%s: {ore=%s, shape=%s, size=%s, rarity=%d, baseRocks=%s, minY=%d, maxY=%d, density=%.2f}", name, ore, shape, getWidth(), getRarity(), baseRocks, getMinY(), getMaxY(), getDensity());
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getMinY()
    {
        return minY;
    }

    public int getMaxY()
    {
        return maxY;
    }

    public double getWeight()
    {
        return weight;
    }

    public double getDensity()
    {
        return density;
    }

    public int getRarity()
    {
        return rarity;
    }

    public Ore getOre()
    {
        return ore;
    }

    /**
     * Can the vein spawn in the specified rock type
     */
    public boolean canSpawnIn(Rock rock)
    {
        return baseRocks.contains(rock);
    }

    public enum Shape
    {
        SPHERE, CLUSTER
    }

    public static class CustomVeinType extends VeinType
    {
        private final IBlockState oreState;

        public CustomVeinType(@Nonnull IBlockState oreState, ItemStack looseRock, @Nonnull Collection<Rock> rocks, Shape shape, int width, int height, int rarity, int minY, int maxY, int density)
        {
            super(null, looseRock, rocks, shape, width, height, rarity, minY, maxY, density);
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
