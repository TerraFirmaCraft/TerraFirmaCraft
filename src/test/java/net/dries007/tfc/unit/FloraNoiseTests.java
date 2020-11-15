package net.dries007.tfc.unit;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.mojang.datafixers.util.Pair;
import net.dries007.tfc.Artist;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkDataGenerator;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.decorator.ClimateConfig;
import net.dries007.tfc.world.noise.INoise2D;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class FloraNoiseTests
{
    static final Artist.Raw COLOR = Artist.raw().center(20_000);

    @TestFactory
    public Stream<DynamicTest> testFloraDistributions()
    {
        long seed = System.currentTimeMillis();

        ChunkDataGenerator generator = (ChunkDataGenerator) ChunkDataProvider.getOrThrow().getGenerator();
        Registry<ConfiguredFeature<?, ?>> registry = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);

        assertNotNull(generator);
        assertNotNull(registry);

        INoise2D temperature = generator.getTemperatureNoise();
        INoise2D rainfall = generator.getRainfallNoise();

        Map<Plant, ClimateConfig> climateConfigs = Arrays.stream(Plant.values()).map(plant -> {
            ResourceLocation id = Helpers.identifier("plant/" + plant.name().toLowerCase());
            ConfiguredFeature<?, ?> feature = registry.get(id);

            if (feature != null)
            {
                while (feature.feature() instanceof DecoratedFeature)
                {
                    IPlacementConfig decoratorConfig = ((DecoratedFeatureConfig) feature.config()).decorator.config();
                    if (decoratorConfig instanceof ClimateConfig)
                    {
                        return Pair.of(plant, (ClimateConfig) decoratorConfig);
                    }
                    feature = ((DecoratedFeatureConfig) feature.config()).feature.get();
                }

                fail("Unable to find a climate config for a plant feature");
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));

        Random random = new Random(seed);
        Map<Plant, Color> colorPalette = climateConfigs.keySet().stream().collect(Collectors.toMap(x -> x, x -> new Color(100 + random.nextInt(155), 100 + random.nextInt(155), 100 + random.nextInt(155))));

        return Arrays.stream(Plant.BlockType.values())
            .map(type -> DynamicTest.dynamicTest(type.name().toLowerCase(), () -> {
                List<Plant> plants = Arrays.stream(Plant.values()).filter(p -> p.getType() == type && climateConfigs.containsKey(p)).collect(Collectors.toList());
                COLOR.draw("plants_distribution_" + type.name().toLowerCase(), (x, z) -> {
                    float temp = temperature.noise((float) x, (float) z);
                    float rain = rainfall.noise((float) x, (float) z);

                    List<Plant> possiblePlants = new ArrayList<>();
                    for (Plant p : plants)
                    {
                        if (climateConfigs.get(p).isValid(temp, rain))
                        {
                            possiblePlants.add(p);
                        }
                    }

                    Plant entry = possiblePlants.isEmpty() ? null : possiblePlants.get(random.nextInt(possiblePlants.size()));
                    if (entry != null)
                    {
                        return colorPalette.get(entry);
                    }
                    return Color.BLACK;
                });
            }));
    }
}
