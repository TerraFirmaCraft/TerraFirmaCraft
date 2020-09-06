/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature.trees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ForestFeature extends Feature<ForestFeatureConfig>
{
    public ForestFeature()
    {
        super(ForestFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, ForestFeatureConfig config)
    {
        final ChunkData data = ChunkDataProvider.get(worldIn).map(provider -> provider.get(pos, ChunkData.Status.FLORA)).orElseThrow(() -> new IllegalStateException("Missing flora data, cannot place forests."));
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final ForestType forestType = data.getForestType();

        int treeCount;
        boolean placedTrees = false;
        if (forestType == ForestType.SPARSE)
        {
            for (int i = 0; i < 2; i++)
            {
                if (rand.nextFloat() < 0.2f)
                {
                    placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos);
                }
            }
            return true;
        }
        else if (forestType == ForestType.NORMAL)
        {
            treeCount = 4;
        }
        else if (forestType == ForestType.OLD_GROWTH)
        {
            treeCount = 6;
        }
        else
        {
            return false;
        }

        final float density = data.getForestDensity();
        treeCount = (int) (treeCount * (1 + 0.5f * density));
        for (int i = 0; i < treeCount; i++)
        {
            placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos);
        }
        return placedTrees;
    }

    private boolean placeTree(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos chunkBlockPos, ForestFeatureConfig config, ChunkData data, BlockPos.Mutable mutablePos)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.setPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
      mutablePos.setY(worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ConfiguredFeature<?, ?> feature = getTree(data, random, config, mutablePos);
        if (feature != null)
        {
            return feature.place(worldIn, generator, random, mutablePos);
        }
        return false;
    }

    @Nullable
    private ConfiguredFeature<?, ?> getTree(ChunkData chunkData, Random random, ForestFeatureConfig config, BlockPos pos)
    {
        List<ForestFeatureConfig.Entry> entries = new ArrayList<>(4);
        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = chunkData.getAverageTemp(pos);
        for (ForestFeatureConfig.Entry entry : config.getEntries())
        {
            if (entry.isValid(rainfall, averageTemperature))
            {
                entries.add(entry);
            }
        }

        float weirdness = chunkData.getForestWeirdness();
        Collections.rotate(entries, -(int) (weirdness * (entries.size() - 1f)));

        if (entries.isEmpty())
        {
            return null;
        }
        int index = 0;
        while (index < entries.size() - 1 && random.nextFloat() < 0.6f)
        {
            index++;
        }
        return entries.get(index).getFeature();
    }
}
