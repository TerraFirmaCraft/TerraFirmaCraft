package net.dries007.tfc.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;

import net.dries007.tfc.world.settings.RockSettings;

public record RockLayers(List<Layer> oceanFloor, List<Layer> land, List<Layer> volcanic, List<Layer> uplift, Data data)
{
    public static final Codec<RockLayers> CODEC = Data.CODEC.comapFlatMap(RockLayers::processData, RockLayers::data);

    private static DataResult<RockLayers> processData(Data data)
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

        return error.getValue() == null ?
            DataResult.error(error::getValue) :
            DataResult.success(new RockLayers(oceanFloor, land, volcanic, uplift, data));
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

    private static Sampler sampler(long seed, List<Layer> initialLayer)
    {
        return new Sampler() {
            final RandomSource source = new XoroshiroRandomSource(seed);
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

    record Data(Map<String, RockSettings> rocks, List<String> bottom, List<LayerData> layers, List<String> oceanFloor, List<String> land, List<String> volcanic, List<String> uplift)
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

    record LayerData(String id, Map<String, String> layers)
    {
        static final Codec<LayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(c -> c.id),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("layers").forGetter(c -> c.layers)
        ).apply(instance, LayerData::new));
    }
}
