/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.world.flora.FloraType;
import net.dries007.tfc.world.flora.FloraTypeManager;

public class FloraFeature extends Feature<NoFeatureConfig>
{
    private static List<FloraType> getFloraAtChunk(IWorld world, BlockPos pos, Random random)
    {
        List<FloraType> list = new ArrayList<>();
        for (FloraType type : FloraTypeManager.INSTANCE.getOrderedValues())
        {
            if (type.canGenerate(world, pos) && random.nextInt(type.getRarity()) == 0)
            {
                list.add(type);
            }
        }
        return list;
    }

    @SuppressWarnings("unused")
    public FloraFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactory)
    {
        super(configFactory);
    }

    public FloraFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random random, BlockPos pos, NoFeatureConfig config)
    {
        List<FloraType> list = getFloraAtChunk(world, pos, random);
        for (FloraType floraType : list)
        {
            floraType.generate(world, pos, random);
        }
        return !list.isEmpty();
    }
}
