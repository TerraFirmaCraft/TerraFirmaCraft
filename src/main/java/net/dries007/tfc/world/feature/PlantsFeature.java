/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.ArrayList;
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
        final ChunkData data = ChunkDataProvider.get(world).map(provider -> provider.get(pos, ChunkData.Status.FLORA)).orElseThrow(() -> new IllegalStateException("Missing flora data, cannot place plants."));
        getPlants(data, random, config, pos).forEach(entry -> {
            entry.getFeature().place(world, generator, random, pos);
        });
        return false;
    }

    private List<PlantsConfig.Entry> getPlants(ChunkData data, Random random, PlantsConfig config, BlockPos pos)
    {
        List<PlantsConfig.Entry> entries = new ArrayList<>(4);
        float rainfall = data.getRainfall(pos);
        float averageTemperature = data.getAverageTemp(pos);
        for (PlantsConfig.Entry entry : config.getEntries())
        {
            if (entry.isValid(rainfall, averageTemperature))
            {
                entries.add(entry);
            }
        }
        //todo use forest weirdness?
        return entries;
    }
}
