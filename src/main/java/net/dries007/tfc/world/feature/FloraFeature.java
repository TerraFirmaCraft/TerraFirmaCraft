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
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.serialization.Codec;
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

    public FloraFeature(Codec<NoFeatureConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(ISeedReader worldIn, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        List<FloraType> list = getFloraAtChunk(worldIn, pos, rand);
        for (FloraType floraType : list)
        {
            floraType.generate(worldIn, pos, rand);
        }
        return !list.isEmpty();
    }
}
