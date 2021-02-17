/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.serialization.Codec;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ForestType;

//only use this with TFC plants (it sets the AGE property)
public class RandomPatchDensityFeature extends Feature<BlockClusterFeatureConfig>
{
    public RandomPatchDensityFeature(Codec<BlockClusterFeatureConfig> codec)
    {
        super(codec);
    }

    //unused: project, canReplace
    @Override
    public boolean generate(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, BlockClusterFeatureConfig config)
    {
        BlockState blockstate = config.stateProvider.getBlockState(rand, pos);
        int i = 0;
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();
        ChunkData data = ChunkData.get(world, mutablePos);
        float density = (data.getForestDensity() + 0.5f);
        ForestType type = data.getForestType();
        if (type != ForestType.SPARSE && type != ForestType.NONE)
            density += 0.3f;
        int tries = Math.min((int) (config.tryCount * density), 128);
        for (int j = 0; j < tries; ++j)
        {
            mutablePos.setAndOffset(world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos), rand.nextInt(config.xSpread + 1) - rand.nextInt(config.xSpread + 1), -1, rand.nextInt(config.zSpread + 1) - rand.nextInt(config.zSpread + 1));
            BlockState state = world.getBlockState(mutablePos);
            mutablePos.move(Direction.UP);
            if ((world.isAirBlock(mutablePos) && blockstate.isReplaceable(world, mutablePos) && (config.whitelist.isEmpty() || config.whitelist.contains(state.getBlock())) && !config.blacklist.contains(state)))
            {
                config.blockPlacer.place(world, mutablePos, blockstate.with(TFCBlockStateProperties.AGE_3, rand.nextInt(4)), rand); //randomize age
                ++i;
            }
        }
        return i > 0;
    }
}
