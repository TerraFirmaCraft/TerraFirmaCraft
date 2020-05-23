package net.dries007.tfc.world.feature;

import java.util.Random;
import java.util.function.Function;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import com.mojang.datafixers.Dynamic;
import net.dries007.tfc.api.Rock;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.ChunkDataProvider;

public class BouldersFeature extends Feature<NoFeatureConfig>
{
    @SuppressWarnings("unused")
    public BouldersFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn)
    {
        super(configFactoryIn);
    }

    public BouldersFeature()
    {
        super(NoFeatureConfig::deserialize);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config)
    {
        ChunkDataProvider.get(worldIn)
            .map(provider -> provider.get(pos, ChunkData.Status.ROCKS, false).getRockData().getRock(pos.getX(), pos.getY(), pos.getZ()))
            .ifPresent(rock -> {
                int blockPicker = rand.nextInt(2);
                Rock.BlockType boulderType = null;
                switch(blockPicker) {
                    case 0:
                        boulderType = Rock.BlockType.MOSSY_COBBLE;
                        break;
                    case 1:
                        boulderType = Rock.BlockType.RAW;
                        break;
                    case 2:
                        boulderType = Rock.BlockType.COBBLE;
                        break;
                }
                BlockState state = rock.getBlock(boulderType).getDefaultState();
                int size = 2 + rand.nextInt(4);
                for (BlockPos posAt : BlockPos.getAllInBoxMutable(pos.getX() - size, pos.getY() - size, pos.getZ() - size, pos.getX() + size, pos.getY() + size, pos.getZ() + size))
                {
                    if (posAt.distanceSq(pos) <= size * size)
                    {
                        setBlockState(worldIn, posAt, state);
                    }
                }
            });
        return true;
    }
}
