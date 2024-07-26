/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;

public enum ForestType implements StringRepresentable
{
    GRASSLAND(0, zero(), zero(), zero(), zero(), 3, 0, 0),
    SHRUBLAND(1, value(2), value(10), range(0, 1), range(1, 4), 3, 1, 0),
    PRIMARY_MONOCULTURE(3, value(5), value(25), range(0, 1), zero(), 1, 1, 0),
    PRIMARY_DIVERSE(4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 0),
    PRIMARY_ALTERNATE(4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 2),
    SECONDARY_MONOCULTURE(3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    SECONDARY_MONOCULTURE_TALL(3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    SECONDARY_DIVERSE(3, value(5), value(25), zero(), range(1, 2), 3, 1, 0),
    SECONDARY_DIVERSE_TALL(3, value(5), value(25), zero(), range(1, 2), 2, 1, 0),
    SECONDARY_DENSE(4, value(7), value(40), range(0, 1), value(3), 2, 1, 0),
    SECONDARY_DENSE_TALL(4, value(7), value(40), range(0, 1), value(3), 2, 1, 0),
    SECONDARY_ALTERNATE(3, value(5), value(25), zero(), range(1, 2), 3, 1, 2),
    SECONDARY_SPARSE(1, value(3), value(6), zero(), range(0, 1), 3, 0.08f, 0),
    EDGE_MONOCULTURE(2, value(2), value(10), range(0, 1), range(0, 1), 1, 1, 0),
    EDGE_DIVERSE(2, value(2), value(10), range(0, 1), range(0, 1), 3, 1, 0),
    EDGE_ALTERNATE(2, value(2), value(10), range(0, 1), range(0, 1), 3, 1, 2),
    ALPINE_MONOCULTURE(3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    ALPINE_DIVERSE(3, value(5), value(25), zero(), range(1, 2), 3, 1, 0),
    ALPINE_ALTERNATE(3, value(5), value(25), zero(), zero(), 3, 1, 2),
    ALPINE_SPARSE(1, value(3), value(6), zero(), zero(), 3, 0.08f, 0),
    ALPINE_SHRUBLAND(1, value(2), value(10), range(0, 1), range(1, 4), 3, 1, 0),
    DEAD_MONOCULTURE(3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    DEAD_DIVERSE(3, value(5), value(25), zero(), range(1, 2), 3, 1, 0),
    DEAD_ALTERNATE(4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 2),
    SWAMP_SPARSE(1, value(3), value(6), zero(), zero(), 3, 0.08f, 0),
    SWAMP_DIVERSE(3, value(5), value(25), zero(), zero(), 3, 1, 0),
    SWAMP_ALTERNATE(3, value(5), value(25), zero(), zero(), 3, 1, 1),
    ;

    public static final Codec<ForestType> CODEC = StringRepresentable.fromEnum(ForestType::values);
    public static final StreamCodec<ByteBuf, ForestType> STREAM = ByteBufCodecs.BYTE.map(ForestType::valueOf, c -> (byte) c.ordinal());

    // todo: an actual generation model for these forest types
    private static final List<ForestType> DENSITY_1 = Arrays.stream(ForestType.values()).filter(type -> type.getDensity() == 1).toList();
    private static final List<ForestType> DENSITY_2 = Arrays.stream(ForestType.values()).filter(type -> type.getDensity() == 2).toList();
    private static final List<ForestType> DENSITY_3 = Arrays.stream(ForestType.values()).filter(type -> type.getDensity() == 3).toList();
    private static final List<ForestType> DENSITY_4 = Arrays.stream(ForestType.values()).filter(type -> type.getDensity() == 4).toList();
    public static int getSparseForestType(RandomSource random) { return DENSITY_1.get(random.nextInt(DENSITY_1.size())).ordinal(); }
    public static int getEdgeForestType(RandomSource random) { return DENSITY_2.get(random.nextInt(DENSITY_2.size())).ordinal(); }
    public static int getNormalForestType(RandomSource random) { return DENSITY_3.get(random.nextInt(DENSITY_3.size())).ordinal(); }
    public static int getOldGrowthForestType(RandomSource random) { return DENSITY_4.get(random.nextInt(DENSITY_4.size())).ordinal(); }

    private static final ForestType[] VALUES = values();

    public static ForestType valueOf(int i)
    {
        return i >= 0 && i < VALUES.length ? VALUES[i] : GRASSLAND;
    }

    public static ForestType byName(String name)
    {
        return valueOf(name.toUpperCase(Locale.ROOT));
    }

    private static IntProvider zero()
    {
        return ConstantInt.of(0);
    }

    private static IntProvider range(int min, int max)
    {
        return UniformInt.of(min, max);
    }

    private static IntProvider value(int i)
    {
        return ConstantInt.of(i);
    }

    private final int density;
    private final IntProvider treeCount;
    private final IntProvider groundcoverCount;
    private final IntProvider leafPileCount;
    private final IntProvider bushCount;
    private final int maxTreeTypes;
    private final float perChunkChance;
    private final int alternateSize;

    ForestType(int density, IntProvider treeCount, IntProvider groundcoverCount, IntProvider leafPileCount, IntProvider bushCount, int maxTreeTypes, float perChunkChance, int alternateSize)
    {
        this.density = density;
        this.treeCount = treeCount;
        this.groundcoverCount = groundcoverCount;
        this.leafPileCount = leafPileCount;
        this.bushCount = bushCount;
        this.maxTreeTypes = maxTreeTypes;
        this.perChunkChance = perChunkChance;
        this.alternateSize = alternateSize;
    }

    @Override
    public String getSerializedName()
    {
        return name().toLowerCase(Locale.ROOT);
    }

    public boolean isPrimary()
    {
        return this == PRIMARY_ALTERNATE || this == PRIMARY_DIVERSE || this == PRIMARY_MONOCULTURE;
    }

    public boolean isDead()
    {
        return this == DEAD_ALTERNATE || this == DEAD_DIVERSE || this == DEAD_MONOCULTURE;
    }

    public int sampleTrees(RandomSource random)
    {
        return treeCount.sample(random);
    }

    public int sampleGroundcover(RandomSource random)
    {
        return groundcoverCount.sample(random);
    }

    public int sampleLeafPiles(RandomSource random)
    {
        return leafPileCount.sample(random);
    }

    public int sampleBushes(RandomSource random)
    {
        return bushCount.sample(random);
    }

    public boolean isAsOrMoreDenseAs(ForestType other)
    {
        return getDensity() >= other.getDensity();
    }

    public int getDensity()
    {
        return density;
    }

    public int getMaxTreeTypes()
    {
        return maxTreeTypes;
    }

    public float getPerChunkChance()
    {
        return perChunkChance;
    }

    public int getAlternateSize()
    {
        return alternateSize;
    }
}