package net.dries007.tfc.world.settings;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fmllegacy.RegistryObject;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.blocks.SandstoneBlockType;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.events.RockLoadingEvent;
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
        final Map<ResourceLocation, RockSettings> rocks = new HashMap<>();
        for (net.dries007.tfc.common.blocks.rock.Rock rock : net.dries007.tfc.common.blocks.rock.Rock.values())
        {
            final ResourceLocation id = Helpers.identifier(rock.getSerializedName());
            final RockCategory category = rock.getCategory();
            final Map<Rock.BlockType, RegistryObject<Block>> blocks = TFCBlocks.ROCK_BLOCKS.get(rock);
            final RockSettings instance = new RockSettings(
                id,
                blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.RAW).get(),
                blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.HARDENED).get(),
                blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.GRAVEL).get(),
                blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.COBBLE).get(),
                TFCBlocks.SAND.get(rock.getSandType()).get(),
                TFCBlocks.SANDSTONE.get(rock.getSandType()).get(SandstoneBlockType.RAW).get(),
                Optional.of(blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.SPIKE).get()),
                Optional.of(blocks.get(net.dries007.tfc.common.blocks.rock.Rock.BlockType.LOOSE).get()),
                category != RockCategory.IGNEOUS_INTRUSIVE,
                true,
                category == RockCategory.IGNEOUS_INTRUSIVE || category == RockCategory.METAMORPHIC
            );
            rocks.put(id, instance);
        }
        MinecraftForge.EVENT_BUS.post(new RockLoadingEvent(rocks)); // Allow addons to mutate rocks
        return new RockLayerSettings(ImmutableMap.copyOf(rocks), 1 << 7); // copy the map so no sneaky references get held
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
    private final int rockLayerScale;

    public RockLayerSettings(Map<ResourceLocation, RockSettings> rocksById, int rockLayerScale)
    {
        this.rocksById = rocksById;
        this.rocks = rocksById.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByKey())
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
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

    public List<RockSettings> getRocksForLayer(RockLayer layer)
    {
        return this.rocks
            .stream()
            .filter(rock -> switch (layer)
                {
                    case TOP -> rock.topLayer();
                    case MIDDLE -> rock.middleLayer();
                    case BOTTOM -> rock.bottomLayer();
                })
            .collect(Collectors.toList());
    }
}
