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
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;
import net.dries007.tfc.world.chunkdata.ForestType;

public class ForestFeature extends Feature<ForestConfig>
{
    public ForestFeature(Codec<ForestConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, ForestConfig config)
    {
        final ChunkData data = ChunkDataProvider.get(worldIn).map(provider -> provider.get(pos, ChunkData.Status.FLORA)).orElseThrow(() -> new IllegalStateException("Missing flora data, cannot place forests."));
        final BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        final ForestType forestType = data.getForestType();

        int treeCount;
        boolean placedTrees = false;
        if (forestType == ForestType.SPARSE)
        {
            if (rand.nextFloat() < 0.08f)
            {
                int trees = 1 + rand.nextInt(3);
                for (int i = 0; i < trees; i++)
                {
                    placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos, false);
                }
            }
            return true;
        }
        else if (forestType == ForestType.NORMAL)
        {
            treeCount = 5;
        }
        else if (forestType == ForestType.OLD_GROWTH)
        {
            treeCount = 7;
        }
        else
        {
            return false;
        }

        final float density = data.getForestDensity();
        treeCount = (int) (treeCount * (0.6f + 0.9f * density));
        for (int i = 0; i < treeCount; i++)
        {
            placedTrees |= placeTree(worldIn, generator, rand, pos, config, data, mutablePos, forestType == ForestType.OLD_GROWTH);
        }
        return placedTrees;
    }

    private boolean placeTree(ISeedReader worldIn, ChunkGenerator generator, Random random, BlockPos chunkBlockPos, ForestConfig config, ChunkData data, BlockPos.Mutable mutablePos, boolean allowOldGrowth)
    {
        final int chunkX = chunkBlockPos.getX();
        final int chunkZ = chunkBlockPos.getZ();

        mutablePos.set(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
        mutablePos.setY(worldIn.getHeight(Heightmap.Type.WORLD_SURFACE_WG, mutablePos.getX(), mutablePos.getZ()));

        final ForestConfig.Entry entry = getTree(data, random, config, mutablePos);
        if (entry != null)
        {
            ConfiguredFeature<?, ?> feature;
            if (allowOldGrowth && random.nextInt(6) == 0)
            {
                feature = entry.getOldGrowthFeature();
            }
            else
            {
                feature = entry.getFeature();
            }
            return feature.place(worldIn, generator, random, mutablePos);
        }
        return false;
    }

    @Nullable
    private ForestConfig.Entry getTree(ChunkData chunkData, Random random, ForestConfig config, BlockPos pos)
    {
        List<ForestConfig.Entry> entries = new ArrayList<>(4);
        float rainfall = chunkData.getRainfall(pos);
        float averageTemperature = chunkData.getAverageTemp(pos);
        for (ForestConfig.Entry entry : config.getEntries())
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
        return entries.get(index);
    }
}