/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.chunkdata;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
    GRASSLAND(ForestSubType.NONE, 0, zero(), zero(), zero(), zero(), 3, 0, 0),
    SHRUBLAND(ForestSubType.NONE, 1, value(2), value(10), range(0, 1), range(1, 4), 3, 1, 0),
    PRIMARY_MONOCULTURE(ForestSubType.PRIMARY, 3, value(5), value(25), range(0, 1), zero(), 1, 1, 0),
    PRIMARY_DIVERSE(ForestSubType.PRIMARY, 4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 0),
    PRIMARY_ALTERNATE(ForestSubType.PRIMARY, 4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 2),
    SECONDARY_MONOCULTURE(ForestSubType.SECONDARY, 3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    SECONDARY_MONOCULTURE_TALL(ForestSubType.SECONDARY, 3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    SECONDARY_DIVERSE(ForestSubType.SECONDARY, 3, value(5), value(25), zero(), range(1, 2), 3, 1, 0),
    SECONDARY_DIVERSE_TALL(ForestSubType.SECONDARY, 3, value(5), value(25), zero(), range(1, 2), 2, 1, 0),
    SECONDARY_DENSE(ForestSubType.SECONDARY, 4, value(7), value(40), range(0, 1), value(3), 2, 1, 0),
    SECONDARY_DENSE_TALL(ForestSubType.SECONDARY, 4, value(7), value(40), range(0, 1), value(3), 2, 1, 0),
    SECONDARY_ALTERNATE(ForestSubType.SECONDARY, 3, value(5), value(25), zero(), range(1, 2), 3, 1, 2),
    SECONDARY_SPARSE(ForestSubType.SECONDARY, 1, value(3), value(6), zero(), range(0, 1), 3, 0.08f, 0),
    EDGE_MONOCULTURE(ForestSubType.EDGE, 2, value(2), value(10), range(0, 1), range(0, 1), 1, 1, 0),
    EDGE_DIVERSE(ForestSubType.EDGE, 2, value(2), value(10), range(0, 1), range(0, 1), 3, 1, 0),
    EDGE_ALTERNATE(ForestSubType.EDGE, 2, value(2), value(10), range(0, 1), range(0, 1), 3, 1, 2),
    DEAD_MONOCULTURE(ForestSubType.DEAD, 3, value(5), value(25), zero(), range(1, 2), 1, 1, 0),
    DEAD_DIVERSE(ForestSubType.DEAD, 3, value(5), value(25), zero(), range(1, 2), 3, 1, 0),
    DEAD_ALTERNATE(ForestSubType.DEAD, 4, value(7), value(40), range(0, 1), range(0, 3), 3, 1, 2),
    ;

    public static final Codec<ForestType> CODEC = StringRepresentable.fromEnum(ForestType::values);
    public static final StreamCodec<ByteBuf, ForestType> STREAM = ByteBufCodecs.BYTE.map(ForestType::valueOf, c -> (byte) c.ordinal());

    private static final List<ForestType> EDGE_DENSITY = List.of(EDGE_MONOCULTURE, EDGE_DIVERSE);
    private static final List<ForestType> SECONDARY_FORESTS = List.of(SECONDARY_MONOCULTURE, SECONDARY_DENSE, SECONDARY_DIVERSE, SECONDARY_DENSE_TALL, SECONDARY_MONOCULTURE_TALL, SECONDARY_DIVERSE_TALL);
    private static final List<ForestType> PRIMARY_FORESTS = List.of(PRIMARY_DIVERSE, PRIMARY_MONOCULTURE);
    private static final List<ForestType> DEAD_FORESTS = List.of(DEAD_MONOCULTURE, DEAD_DIVERSE, DEAD_ALTERNATE);
    public static int getEdgeForestType(RandomSource random) { return EDGE_DENSITY.get(random.nextInt(EDGE_DENSITY.size())).ordinal(); }
    public static int getNormalForestType(RandomSource random) { return SECONDARY_FORESTS.get(random.nextInt(SECONDARY_FORESTS.size())).ordinal(); }
    public static int getOldGrowthForestType(RandomSource random) { return PRIMARY_FORESTS.get(random.nextInt(PRIMARY_FORESTS.size())).ordinal(); }
    public static int getDeadForestType(RandomSource random) { return PRIMARY_FORESTS.get(random.nextInt(PRIMARY_FORESTS.size())).ordinal(); }

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

    private final ForestSubType subType;
    private final int density;
    private final IntProvider treeCount;
    private final IntProvider groundcoverCount;
    private final IntProvider leafPileCount;
    private final IntProvider bushCount;
    private final int maxTreeTypes;
    private final float perChunkChance;
    private final int alternateSize;

    ForestType(ForestSubType subType, int density, IntProvider treeCount, IntProvider groundcoverCount, IntProvider leafPileCount, IntProvider bushCount, int maxTreeTypes, float perChunkChance, int alternateSize)
    {
        this.subType = subType;
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

    public ForestType getAlternate()
    {
        if (isSparse())
            return this;
        if (isEdge())
            return EDGE_ALTERNATE;
        if (isSecondary())
            return SECONDARY_ALTERNATE;
        if (isPrimary())
            return PRIMARY_ALTERNATE;
        if (isDead())
            return DEAD_ALTERNATE;
        return this;
    }

    public boolean isPrimary()
    {
        return subType == ForestSubType.PRIMARY;
    }

    public boolean isEdge()
    {
        return subType == ForestSubType.EDGE;
    }

    public boolean isSecondary()
    {
        return subType == ForestSubType.SECONDARY;
    }

    public boolean isDead()
    {
        return subType == ForestSubType.DEAD;
    }

    public boolean isNone()
    {
        return subType == ForestSubType.NONE;
    }

    public boolean isSparse()
    {
        return this == SECONDARY_SPARSE;
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

    public enum ForestSubType
    {
        NONE,
        PRIMARY,
        SECONDARY,
        EDGE,
        DEAD
    }
}