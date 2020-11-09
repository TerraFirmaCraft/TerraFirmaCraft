/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class PlantsFeature extends Feature<PlantsConfig>
{
    public PlantsFeature(Codec<PlantsConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, PlantsConfig config)
    {
        final ChunkDataProvider provider = ChunkDataProvider.getOrThrow(generator);
        final ChunkData data = provider.get(pos, ChunkData.Status.FLORA);
        getPlants(data, random, config, pos).forEach(entry -> entry.getFeature().place(world, generator, random, pos));
        return true;
    }

    private List<PlantsConfig.Entry> getPlants(ChunkData data, Random random, PlantsConfig config, BlockPos pos)
    {
        List<PlantsConfig.Entry> validEntries = new ArrayList<>();
        float rainfall = data.getRainfall(pos);
        float averageTemperature = data.getAverageTemp(pos);
        float weirdness = data.getForestWeirdness();
        for (PlantsConfig.Entry entry : config.getEntries())
        {
            if (entry.isValid(rainfall, averageTemperature))
            {
                validEntries.add(entry);
            }
        }
        Collections.rotate(validEntries, -(int) (weirdness * (validEntries.size() - 1f)));
        List<PlantsConfig.Entry> entries = new ArrayList<>();
        for (PlantsConfig.PlantType type : PlantsConfig.PlantType.values())
        {
            int plants = (int) (config.getPlantsPerChunk(type) * (1 + data.getForestDensity()));
            while (plants > 0)
            {
                boolean added = false;
                for (PlantsConfig.Entry entry : validEntries)
                {
                    // todo: check for clay blocks if plant is clay indicator
                    if (!entry.isClayIndicator() && entry.getType().equals(type) && random.nextFloat() < 0.4f)
                    {
                        entries.add(entry);
                        added = true;
                        break;
                    }
                }
                if (added)
                {
                    plants--;
                }
                else
                {
                    break;
                }
            }
        }
        return entries;
    }
}
