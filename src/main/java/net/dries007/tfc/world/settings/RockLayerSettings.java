/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.region.ChooseRocks;

public final class RockLayerSettings
{
    public static final Codec<RockLayerSettings> CODEC = Data.CODEC.comapFlatMap(RockLayerSettings::decode, r -> r.data);

    public static DataResult<RockLayerSettings> decode(Data data)
    {
        final List<Layer> bottom = new ArrayList<>();
        for (String id : data.bottom)
        {
            final RockSettings rock = data.rocks.get(id);
            if (rock == null) return DataResult.error(() -> "No rock with id: " + id);
            bottom.add(new Layer(rock, bottom));
        }

        final Map<String, List<Layer>> layers = new HashMap<>();
        layers.put("bottom", bottom);

        for (LayerData layer : data.layers)
        {
            final List<Layer> baked = new ArrayList<>();
            if (layers.containsKey(layer.id)) return DataResult.error(() -> "More than one layer named " + layer.id);
            for (Map.Entry<String, String> entry : layer.layers.entrySet())
            {
                final List<Layer> next = layers.get(entry.getValue());
                if (next == null) return DataResult.error(() -> "No layer with id: " + entry.getValue());

                final RockSettings rock = data.rocks.get(entry.getKey());
                if (rock == null) return DataResult.error(() -> "No rock with id: " + entry.getKey());

                baked.add(new Layer(rock, next));
            }
            layers.put(layer.id, baked);
        }

        final Mutable<String> error = new MutableObject<>();
        final List<Layer> oceanFloor = processTopLevel(layers, data.oceanFloor, error);
        final List<Layer> land = processTopLevel(layers, data.land, error);
        final List<Layer> volcanic = processTopLevel(layers, data.volcanic, error);
        final List<Layer> uplift = processTopLevel(layers, data.uplift, error);

        return error.getValue() != null ?
            DataResult.error(error::getValue) :
            DataResult.success(new RockLayerSettings(oceanFloor, land, volcanic, uplift, data));
    }

    private static List<Layer> processTopLevel(Map<String, List<Layer>> layers, List<String> entries, Mutable<String> error)
    {
        final List<Layer> baked = new ArrayList<>();
        for (String id : entries)
        {
            final List<Layer> layer = layers.get(id);
            if (layer == null) error.setValue("No layer with id: " + id);
            else baked.addAll(layer);
        }
        return baked;
    }

    private final List<Layer> oceanFloor, land, volcanic, uplift;
    private final Map<Block, RockSettings> rockBlocks;
    private final Map<Block, Block> rawToHardened;
    private final Data data;

    private RockLayerSettings(List<Layer> oceanFloor, List<Layer> land, List<Layer> volcanic, List<Layer> uplift, Data data)
    {
        this.oceanFloor = oceanFloor;
        this.land = land;
        this.volcanic = volcanic;
        this.uplift = uplift;
        this.data = data;

        this.rockBlocks = new IdentityHashMap<>();

        for (RockSettings rock : data.rocks.values())
        {
            rockBlocks.put(rock.raw(), rock);
            rockBlocks.put(rock.hardened(), rock);
            rockBlocks.put(rock.gravel(), rock);
            rockBlocks.put(rock.cobble(), rock);
            rockBlocks.put(rock.gravel(), rock);
            rockBlocks.put(rock.sand(), rock);
            rockBlocks.put(rock.sandstone(), rock);
            rock.loose().ifPresent(loose -> rockBlocks.put(loose, rock));
            rock.mossyLoose().ifPresent(loose -> rockBlocks.put(loose, rock));
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

    public Collection<RockSettings> getRocks()
    {
        return data.rocks.values();
    }

    public RockSettings sampleAtLayer(int pointRock, int layerN)
    {
        final RandomSource source = new XoroshiroRandomSource(pointRock >> ChooseRocks.TYPE_BITS);
        final List<Layer> rootLayers = switch (pointRock & ChooseRocks.TYPE_MASK)
            {
                case ChooseRocks.OCEAN -> oceanFloor;
                case ChooseRocks.VOLCANIC -> volcanic;
                case ChooseRocks.LAND -> land;
                default -> uplift;
            };

        Layer layer = rootLayers.get(source.nextInt(rootLayers.size()));
        for (int i = 0; i < layerN; i++)
        {
            layer = layer.next.get(source.nextInt(layer.next.size()));
        }
        return layer.rock;
    }

    public Sampler sampler(int pointRock)
    {
        final List<Layer> initialLayer = switch (pointRock & ChooseRocks.TYPE_MASK)
            {
                case ChooseRocks.OCEAN -> oceanFloor;
                case ChooseRocks.VOLCANIC -> volcanic;
                case ChooseRocks.LAND -> land;
                default -> uplift;
            };

        return new Sampler()
        {
            final RandomSource source = new XoroshiroRandomSource(pointRock >> ChooseRocks.TYPE_BITS);
            List<Layer> layer = initialLayer;

            @Override
            public RockSettings next()
            {
                final Layer chosen = layer.get(source.nextInt(layer.size()));
                layer = chosen.next();
                return chosen.rock();
            }
        };
    }


    @FunctionalInterface
    public interface Sampler
    {
        RockSettings next();
    }

    public record Layer(RockSettings rock, List<Layer> next) {}

    public record Data(Map<String, RockSettings> rocks, List<String> bottom, List<LayerData> layers, List<String> oceanFloor, List<String> land, List<String> volcanic, List<String> uplift)
    {
        static final Codec<Data> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING, RockSettings.CODEC).fieldOf("rocks").forGetter(c -> c.rocks),
            Codec.STRING.listOf().fieldOf("bottom").forGetter(c -> c.bottom),
            LayerData.CODEC.listOf().fieldOf("layers").forGetter(c -> c.layers),
            Codec.STRING.listOf().fieldOf("ocean_floor").forGetter(c -> c.oceanFloor),
            Codec.STRING.listOf().fieldOf("land").forGetter(c -> c.land),
            Codec.STRING.listOf().fieldOf("volcanic").forGetter(c -> c.volcanic),
            Codec.STRING.listOf().fieldOf("uplift").forGetter(c -> c.uplift)
        ).apply(instance, Data::new));
    }

    public record LayerData(String id, Map<String, String> layers)
    {
        static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(c -> c.id),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("layers").forGetter(c -> c.layers)
        ).apply(instance, LayerData::new));
    }
}
