/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.feature;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class BouldersFeature extends Feature<BoulderConfig>
{
    @SuppressWarnings("unused")
    public BouldersFeature(Function<Dynamic<?>, ? extends BoulderConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    public BouldersFeature()
    {
        super(BoulderConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, BoulderConfig config)
    {
        ChunkDataProvider.get(worldIn)
            .map(provider -> provider.get(pos, ChunkData.Status.ROCKS, false).getRockData().getRock(pos.getX(), pos.getY(), pos.getZ()))
            .ifPresent(rock -> {
                BlockState baseState = rock.getBlock(config.getBaseType()).getDefaultState();
                BlockState decorationState = rock.getBlock(config.getDecorationType()).getDefaultState();
                int size = 2 + rand.nextInt(4);
                for (BlockPos posAt : BlockPos.getAllInBoxMutable(pos.getX() - size, pos.getY() - size, pos.getZ() - size, pos.getX() + size, pos.getY() + size, pos.getZ() + size))
                {
                    if (posAt.distanceSq(pos) <= size * size)
                    {
                        if (rand.nextFloat() < 0.4f)
                        {
                            setBlockState(worldIn, posAt, decorationState);
                        }
                        else
                        {
                            setBlockState(worldIn, posAt, baseState);
                        }
                    }
                }
            });
        return true;
    }
}
