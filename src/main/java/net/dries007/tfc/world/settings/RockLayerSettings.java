/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.settings;

import java.util.*;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.Codecs;

public class RockLayerSettings
{
    public static final Codec<RockLayerSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RockSettings.CODEC.listOf().comapFlatMap(RockLayerSettings::collect, RockLayerSettings::split).fieldOf("rocks").forGetter(c -> c.rocksById),
        Codecs.POSITIVE_INT.comapFlatMap(RockLayerSettings::guardScale, RockLayerSettings::convertScale).fieldOf("rock_layer_scale").forGetter(c -> c.rockLayerScale)
    ).apply(instance, RockLayerSettings::new));

    public static final RockLayerSettings EMPTY = new RockLayerSettings(Collections.emptyMap(), 1);

    public static RockLayerSettings getDefault()
    {
        return new RockLayerSettings(RockSettings.getDefaults(), 7);
    }

    private static DataResult<Map<ResourceLocation, RockSettings>> collect(List<RockSettings> rocks)
    {
        final Set<ResourceLocation> keys = new HashSet<>();
        final ImmutableMap.Builder<ResourceLocation, RockSettings> builder = ImmutableMap.builder();
        for (RockSettings rock : rocks)
        {
            if (keys.contains(rock.id()))
            {
                return DataResult.error("Duplicate rock id: " + rock.id());
            }
            keys.add(rock.id());
            builder.put(rock.id(), rock);
        }
        if (keys.isEmpty())
        {
            return DataResult.error("Must contain at least one rock!");
        }
        return DataResult.success(builder.build());
    }

    private static List<RockSettings> split(Map<ResourceLocation, RockSettings> rocks)
    {
        return new ArrayList<>(rocks.values());
    }

    private static DataResult<Integer> guardScale(Integer scale)
    {
        if (!Mth.isPowerOfTwo(scale))
        {
            return DataResult.error("rock_layer_scale must be a multiple of 2!");
        }
        return DataResult.success(Mth.ceillog2(scale));
    }

    private static Integer convertScale(Integer scale)
    {
        return 1 << scale;
    }

    private final Map<ResourceLocation, RockSettings> rocksById;
    private final List<RockSettings> rocks;
    private final Map<Block, RockSettings> rockBlocks;
    private final Map<Block, Block> rawToHardened;
    private final int rockLayerScale; // In [0, 32]

    public RockLayerSettings(Map<ResourceLocation, RockSettings> rocksById, int rockLayerScale)
    {
        this.rocksById = rocksById;
        this.rocks = rocksById.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .toList();
        this.rockBlocks = new IdentityHashMap<>();
        this.rockLayerScale = rockLayerScale;

        for (RockSettings rock : this.rocksById.values())
        {
            rockBlocks.put(rock.raw(), rock);
            rockBlocks.put(rock.hardened(), rock);
            rockBlocks.put(rock.gravel(), rock);
            rockBlocks.put(rock.cobble(), rock);
            rockBlocks.put(rock.gravel(), rock);
            rockBlocks.put(rock.sand(), rock);
            rockBlocks.put(rock.sandstone(), rock);
            rock.loose().ifPresent(loose -> rockBlocks.put(loose, rock));
            rock.spike().ifPresent(spike -> rockBlocks.put(spike, rock));
        }

        this.rawToHardened = getRocks()
            .stream()
            .collect(Collectors.toMap(RockSettings::raw, RockSettings::hardened));
    }

    @Nullable
    public Block getHardened(Block raw)
    {
        return rawToHardened.get(raw);
    }

    @Nullable
    public RockSettings getRock(Block block)
    {
        return rockBlocks.get(block);
    }

    public RockSettings getRock(ResourceLocation id)
    {
        return rocksById.get(id);
    }

    public int getScale()
    {
        return rockLayerScale;
    }

    public List<RockSettings> getRocks()
    {
        return rocks;
    }

    public List<RockSettings> getRocksForLayer(RockLayer layer)
    {
        return rocks
            .stream()
            .filter(rock -> switch (layer)
                {
                    case TOP -> rock.topLayer();
                    case MIDDLE -> rock.middleLayer();
                    case BOTTOM -> rock.bottomLayer();
                })
            .toList();
    }
}
