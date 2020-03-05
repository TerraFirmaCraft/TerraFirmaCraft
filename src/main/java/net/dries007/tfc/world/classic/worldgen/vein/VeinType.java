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
import net.minecraft.client.resources.I18n;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
        return String.format("%s: {ore=%s, shape=%s, size=%s, rarity=%d, baseRocks=%s, minY=%d, maxY=%d, density=%.2f}", name, (getOre() != null ? getOre() : "special"), shape, getWidth(), getRarity(), baseRocks, getMinY(), getMaxY(), getDensity());
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

    public Rarity getRarityEnum()
    {
        return Rarity.valueOf(this.getRarity());
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

    public enum Rarity
    {
        COMMON(0, 50),
        UNCOMMON(50, 100),
        RARE(100, 150),
        VERY_RARE(150, 200),
        EPIC(200, Integer.MAX_VALUE);


        @Nonnull
        public static Rarity valueOf(int rarityValue)
        {
            for (Rarity rarity : Rarity.values())
            {
                if (rarity.test(rarityValue))
                {
                    return rarity;
                }
            }
            throw new IllegalArgumentException("Rarity can't be negative!");
        }

        private final int minValue;
        private final int maxValue;

        Rarity(int minValue, int maxValue)
        {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        public boolean test(int rarityValue)
        {
            return rarityValue >= minValue && rarityValue < maxValue;
        }

        public String getTranslationKey()
        {
            return MOD_ID + ".types.vein.rarity." + this.name().toLowerCase();
        }

        @SideOnly(Side.CLIENT)
        public String getFormattedText()
        {
            EnumRarity forgeRarity = EnumRarity.EPIC; // fallback for very rare
            for (EnumRarity rarity : EnumRarity.values())
            {
                if (rarity.getName().equalsIgnoreCase(this.name()))
                {
                    forgeRarity = rarity;
                    break;
                }
            }
            return forgeRarity.getColor() + I18n.format(getTranslationKey());
        }
    }
}
